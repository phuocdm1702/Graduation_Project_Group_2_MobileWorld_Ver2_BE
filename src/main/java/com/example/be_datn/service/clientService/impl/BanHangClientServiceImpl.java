package com.example.be_datn.service.clientService.impl;

import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.sale.GioHangTam;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.discount.ChiTietDotGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.order.LichSuHoaDon;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import com.example.be_datn.entity.pay.PhuongThucThanhToan;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.entity.product.ImelDaBan;
import com.example.be_datn.repository.account.KhachHang.KhachHangRepository;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
import com.example.be_datn.repository.discount.PhieuGiamGiaCaNhanRepository;
import com.example.be_datn.repository.discount.PhieuGiamGiaRepository;
import com.example.be_datn.repository.order.GioHangTamRepository;
import com.example.be_datn.repository.order.HoaDonChiTietRepository;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.repository.order.LichSuHoaDonRepository;
import com.example.be_datn.repository.pay.HinhThucThanhToanRepository;
import com.example.be_datn.repository.pay.PhuongThucThanhToanRepository;
import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.repository.product.ImelDaBanRepository;
import com.example.be_datn.repository.product.ImelRepository;
import com.example.be_datn.repository.sale.saleDetail.ChiTietDotGiamGiaRepository;
import com.example.be_datn.service.clientService.BanHangClientService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BanHangClientServiceImpl implements BanHangClientService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private ImelRepository imelRepository;

    @Autowired
    private ImelDaBanRepository imelDaBanRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;

    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository;

    @Autowired
    private GioHangTamRepository gioHangTamRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String GH_PREFIX = "gh:client:hd:";

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "HD_CLIENT_" + code;
    }

    private String generateUniqueMaHinhThucThanhToan() {
        return "HTTT_CLIENT_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String formatPrice(BigDecimal price) {
        return price != null
                ? String.format("%,.0f VND", price)
                : "0 VND";
    }

    @Override
    @Transactional
    public HoaDonDetailResponse taoHoaDonCho(Integer khachHangId) {
        // Xử lý khách vãng lai
        KhachHang khachHang;
        if (khachHangId != null) {
            khachHang = khachHangRepository.findById(khachHangId)
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại với ID: " + khachHangId));
        } else {
            khachHang = khachHangRepository.findByTen("Khách lẻ")
                    .orElseGet(() -> {
                        KhachHang guest = new KhachHang();
                        guest.setTen("Khách lẻ");
                        return khachHangRepository.save(guest);
                    });
        }

        // Lấy nhân viên mặc định (ID = 1)
        NhanVien nhanVien = nhanVienRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại"));

        // Tạo hóa đơn
        HoaDon hoaDon = HoaDon.builder()
                .idKhachHang(khachHang)
                .idNhanVien(nhanVien)
                .ma(generateRandomCode())
                .tienSanPham(BigDecimal.ZERO)
                .loaiDon("online")
                .phiVanChuyen(BigDecimal.ZERO)
                .tongTien(BigDecimal.ZERO)
                .tongTienSauGiam(BigDecimal.ZERO)
                .ghiChu("Hóa đơn chờ từ client")
                .tenKhachHang(khachHang.getTen())
                .diaChiKhachHang("N/A")
                .soDienThoaiKhachHang(khachHang.getIdTaiKhoan() != null ? khachHang.getIdTaiKhoan().getSoDienThoai() : "N/A")
                .email("N/A")
                .ngayTao(new Date())
                .trangThai((short) 0) // Chờ thanh toán
                .deleted(true) // Hóa đơn chờ, sẽ được cập nhật khi thanh toán
                .createdAt(new Date())
                .createdBy(1)
                .build();

        hoaDon = hoaDonRepository.save(hoaDon);

        // Tạo giỏ hàng trong Redis
        GioHangDTO gioHangDTO = new GioHangDTO();
        gioHangDTO.setGioHangId(GH_PREFIX + hoaDon.getId());
        gioHangDTO.setKhachHangId(khachHang.getId());
        gioHangDTO.setChiTietGioHangDTOS(new ArrayList<>());
        gioHangDTO.setTongTien(BigDecimal.ZERO);
        redisTemplate.opsForValue().set(GH_PREFIX + hoaDon.getId(), gioHangDTO, 24, TimeUnit.HOURS);

        return mapToHoaDonDetailResponse(hoaDon);
    }

    @Override
    @Transactional
    public GioHangDTO themSanPhamVaoGioHang(Integer idHD, ChiTietGioHangDTO chiTietGioHangDTO) {
        // Validate input
        if (chiTietGioHangDTO == null) {
            throw new RuntimeException("Chi tiết giỏ hàng không được null!");
        }

        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn có id: " + idHD));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn này không phải hóa đơn chờ!");
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietGioHangDTO.getChiTietSanPhamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm!"));

        if (chiTietGioHangDTO.getMaImel() == null || chiTietGioHangDTO.getMaImel().isEmpty()) {
            throw new RuntimeException("IMEI không được để trống!");
        }

        List<String> availableIMEIs = chiTietSanPhamRepository.findAvailableIMEIsBySanPhamIdAndAttributes(
                chiTietSanPham.getIdSanPham().getId(),
                chiTietSanPham.getIdMauSac().getMauSac(),
                chiTietSanPham.getIdRam().getDungLuongRam(),
                chiTietSanPham.getIdBoNhoTrong().getDungLuongBoNhoTrong()
        );

        String[] requestedIMEIs = chiTietGioHangDTO.getMaImel().split(",\\s*");
        for (String imei : requestedIMEIs) {
            if (!availableIMEIs.contains(imei.trim())) {
                throw new RuntimeException("IMEI " + imei + " không tồn tại hoặc không khả dụng!");
            }

            Optional<ChiTietSanPham> imelChiTietSanPham = chiTietSanPhamRepository.findByImel(imei.trim());
            if (imelChiTietSanPham.isEmpty() || !imelChiTietSanPham.get().getId().equals(chiTietGioHangDTO.getChiTietSanPhamId())) {
                throw new RuntimeException("IMEI " + imei + " không thuộc chi tiết sản phẩm này!");
            }

            Imel imelEntity = imelChiTietSanPham.get().getIdImel();
            imelEntity.setDeleted(true);
            imelRepository.save(imelEntity);
        }

        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gh = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gh == null) {
            gh = new GioHangDTO();
            gh.setGioHangId(ghKey);
            gh.setKhachHangId(hoaDon.getIdKhachHang() != null ? hoaDon.getIdKhachHang().getId() : null);
            gh.setChiTietGioHangDTOS(new ArrayList<>());
            gh.setTongTien(BigDecimal.ZERO);
        }

        for (String imei : requestedIMEIs) {
            ChiTietGioHangDTO newItem = new ChiTietGioHangDTO();
            newItem.setChiTietSanPhamId(chiTietGioHangDTO.getChiTietSanPhamId());
            newItem.setMaImel(imei.trim());
            newItem.setTenSanPham(chiTietSanPham.getIdSanPham().getTenSanPham());
            newItem.setMauSac(chiTietSanPham.getIdMauSac().getMauSac());
            newItem.setRam(chiTietSanPham.getIdRam().getDungLuongRam());
            newItem.setBoNhoTrong(chiTietSanPham.getIdBoNhoTrong().getDungLuongBoNhoTrong());

            // Kiểm tra giá giảm từ ChiTietDotGiamGia
            BigDecimal giaBan = chiTietSanPham.getGiaBan() != null ? chiTietSanPham.getGiaBan() : BigDecimal.ZERO;
            BigDecimal giaSauGiam = giaBan;
            String ghiChuGia = "";

            Optional<ChiTietDotGiamGia> chiTietDotGiamGiaOpt = chiTietDotGiamGiaRepository.findByChiTietSanPhamIdAndActive(chiTietGioHangDTO.getChiTietSanPhamId());
            if (chiTietDotGiamGiaOpt.isPresent()) {
                ChiTietDotGiamGia chiTietDotGiamGia = chiTietDotGiamGiaOpt.get();
                giaSauGiam = chiTietDotGiamGia.getGiaSauKhiGiam() != null ? chiTietDotGiamGia.getGiaSauKhiGiam() : giaBan;
                ghiChuGia = String.format("Giá gốc ban đầu: %s", giaBan);
            }

            newItem.setGiaBan(giaSauGiam);
            newItem.setGiaBanGoc(giaSauGiam);
            newItem.setGhiChuGia(ghiChuGia);
            newItem.setSoLuong(1);
            newItem.setTongTien(giaSauGiam);
            newItem.setImage(chiTietSanPham.getIdAnhSanPham() != null ? chiTietSanPham.getIdAnhSanPham().getDuongDan() : null);

            boolean imeiExists = gh.getChiTietGioHangDTOS().stream()
                    .anyMatch(item -> item.getMaImel().equals(imei.trim()));
            if (!imeiExists) {
                gh.getChiTietGioHangDTOS().add(newItem);

                GioHangTam gioHangTam = GioHangTam.builder()
                        .idHoaDon(idHD)
                        .imei(imei.trim())
                        .chiTietSanPhamId(chiTietGioHangDTO.getChiTietSanPhamId())
                        .idPhieuGiamGia(chiTietGioHangDTO.getIdPhieuGiamGia())
                        .createdAt(new Date().toInstant())
                        .deleted(false)
                        .build();
                gioHangTamRepository.save(gioHangTam);
            }
        }

        if (chiTietGioHangDTO.getIdPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(chiTietGioHangDTO.getIdPhieuGiamGia())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá!"));
            if (phieuGiamGia.getSoLuongDung() <= 0 || !phieuGiamGia.getTrangThai()) {
                throw new RuntimeException("Mã giảm giá không khả dụng!");
            }
            phieuGiamGia.setSoLuongDung(phieuGiamGia.getSoLuongDung() - 1);
            if (phieuGiamGia.getSoLuongDung() == 0) {
                phieuGiamGia.setTrangThai(false);
            }
            phieuGiamGiaRepository.save(phieuGiamGia);
        }

        gh.setTongTien(gh.getChiTietGioHangDTOS().stream()
                .map(item -> item.getTongTien() != null ? item.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        hoaDon.setTongTien(gh.getTongTien());
        hoaDonRepository.save(hoaDon);

        redisTemplate.opsForValue().set(ghKey, gh, 24, TimeUnit.HOURS);

        return gh;
    }

    @Override
    public GioHangDTO layGioHang(Integer idHD) {
        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gioHangDTO == null) {
            HoaDon hoaDon = hoaDonRepository.findById(idHD)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));
            gioHangDTO = new GioHangDTO();
            gioHangDTO.setGioHangId(ghKey);
            gioHangDTO.setKhachHangId(hoaDon.getIdKhachHang() != null ? hoaDon.getIdKhachHang().getId() : null);
            gioHangDTO.setChiTietGioHangDTOS(new ArrayList<>());
            gioHangDTO.setTongTien(BigDecimal.ZERO);
            redisTemplate.opsForValue().set(ghKey, gioHangDTO, 24, TimeUnit.HOURS);
        } else {
            // Cập nhật giá sản phẩm và image từ database
            for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId());
                if (chiTietSanPhamOpt.isEmpty()) {
                    continue;
                }
                ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
                BigDecimal giaBan = chiTietSanPham.getGiaBan() != null ? chiTietSanPham.getGiaBan() : BigDecimal.ZERO;
                BigDecimal giaSauGiam = giaBan;
                String ghiChuGia = "";

                Optional<ChiTietDotGiamGia> chiTietDotGiamGiaOpt = chiTietDotGiamGiaRepository.findByChiTietSanPhamIdAndActive(item.getChiTietSanPhamId());
                if (chiTietDotGiamGiaOpt.isPresent()) {
                    giaSauGiam = chiTietDotGiamGiaOpt.get().getGiaSauKhiGiam() != null ? chiTietDotGiamGiaOpt.get().getGiaSauKhiGiam() : giaBan;
                    ghiChuGia = String.format("Giá gốc: %s", formatPrice(giaBan));
                }

                item.setGiaBan(giaSauGiam);
                item.setGiaBanGoc(giaSauGiam);
                item.setGhiChuGia(ghiChuGia);
                item.setTongTien(giaSauGiam);
                item.setImage(chiTietSanPham.getIdAnhSanPham() != null ? chiTietSanPham.getIdAnhSanPham().getDuongDan() : null); // Thêm ánh xạ image
            }
            gioHangDTO.setTongTien(gioHangDTO.getChiTietGioHangDTOS().stream()
                    .map(item -> item.getTongTien() != null ? item.getTongTien() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            redisTemplate.opsForValue().set(ghKey, gioHangDTO, 24, TimeUnit.HOURS);
        }
        return gioHangDTO;
    }

    @Override
    @Transactional
    public GioHangDTO xoaSanPhamKhoiGioHang(Integer idHD, Integer spId, String maImel) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));
        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ!");
        }

        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gioHangDTO == null || gioHangDTO.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống hoặc không tồn tại!");
        }

        // Lọc các sản phẩm cần xóa
        List<ChiTietGioHangDTO> updatedItems = gioHangDTO.getChiTietGioHangDTOS().stream()
                .filter(item -> {
                    if (maImel != null && !maImel.isEmpty()) {
                        return !item.getMaImel().equals(maImel.trim());
                    } else {
                        return !item.getChiTietSanPhamId().equals(spId);
                    }
                })
                .collect(Collectors.toList());

        // Khôi phục trạng thái IMEI và phiếu giảm giá
        List<ChiTietGioHangDTO> removedItems = gioHangDTO.getChiTietGioHangDTOS().stream()
                .filter(item -> (maImel != null && !maImel.isEmpty()) ? item.getMaImel().equals(maImel.trim()) : item.getChiTietSanPhamId().equals(spId))
                .collect(Collectors.toList());

        for (ChiTietGioHangDTO item : removedItems) {
            Imel imel = imelRepository.findByImelAndDeleted(item.getMaImel(), true)
                    .orElse(null);
            if (imel != null && !imelDaBanRepository.existsByMa(item.getMaImel())) {
                imel.setDeleted(false);
                imelRepository.save(imel);
            }
            Optional<ChiTietDotGiamGia> chiTietDotGiamGiaOpt = chiTietDotGiamGiaRepository.findByChiTietSanPhamIdAndActive(item.getChiTietSanPhamId());
            if (chiTietDotGiamGiaOpt.isPresent()) {
                ChiTietDotGiamGia chiTietDotGiamGia = chiTietDotGiamGiaOpt.get();
                chiTietDotGiamGia.setDeleted(false);
                chiTietDotGiamGiaRepository.save(chiTietDotGiamGia);
            }
            // Khôi phục phiếu giảm giá
            if (item.getIdPhieuGiamGia() != null) {
                PhieuGiamGia pgg = phieuGiamGiaRepository.findById(item.getIdPhieuGiamGia())
                        .orElse(null);
                if (pgg != null) {
                    pgg.setSoLuongDung(pgg.getSoLuongDung() + 1);
                    pgg.setTrangThai(true);
                    phieuGiamGiaRepository.save(pgg);
                }
            }
        }

        gioHangDTO.setChiTietGioHangDTOS(updatedItems);
        gioHangDTO.setTongTien(updatedItems.stream()
                .map(item -> item.getTongTien() != null ? item.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        hoaDon.setTongTien(gioHangDTO.getTongTien());
        hoaDonRepository.save(hoaDon);

        redisTemplate.opsForValue().set(ghKey, gioHangDTO, 24, TimeUnit.HOURS);
        return gioHangDTO;
    }

    @Override
    @Transactional
    public HoaDonDetailResponse thanhToan(Integer idHD, HoaDonRequest hoaDonRequest) {
        // Kiểm tra hóa đơn
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));
        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ thanh toán!");
        }

        // Kiểm tra giỏ hàng
        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gioHangDTO == null || gioHangDTO.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể thanh toán!");
        }

        // Kiểm tra thông tin bắt buộc
        if (hoaDonRequest.getTenKhachHang() == null || hoaDonRequest.getTenKhachHang().trim().isEmpty()) {
            throw new RuntimeException("Tên khách hàng là bắt buộc!");
        }
        if (hoaDonRequest.getSoDienThoaiKhachHang() == null || hoaDonRequest.getSoDienThoaiKhachHang().trim().isEmpty()) {
            throw new RuntimeException("Số điện thoại khách hàng là bắt buộc!");
        }
        if (hoaDonRequest.getDiaChiKhachHang() == null || hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe() == null || hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe().trim().isEmpty()) {
            throw new RuntimeException("Địa chỉ khách hàng là bắt buộc!");
        }
        if (hoaDonRequest.getEmail() == null || hoaDonRequest.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email là bắt buộc để gửi thông tin đơn hàng!");
        }

        // Xử lý khách hàng
        // Gán idKhachHang = 1 cho khách lẻ
        KhachHang khachHang = khachHangRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Khách hàng mặc định (ID = 1) không tồn tại!"));
        hoaDon.setIdKhachHang(khachHang);
        hoaDon.setTenKhachHang(hoaDonRequest.getTenKhachHang());
        hoaDon.setSoDienThoaiKhachHang(hoaDonRequest.getSoDienThoaiKhachHang());
        hoaDon.setDiaChiKhachHang(hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe());
        hoaDon.setEmail(hoaDonRequest.getEmail());

        // Tính tổng tiền sản phẩm
        BigDecimal tienSanPham = gioHangDTO.getChiTietGioHangDTOS().stream()
                .map(ChiTietGioHangDTO::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Xử lý phí vận chuyển
        BigDecimal phiVanChuyen = "online".equals(hoaDonRequest.getLoaiDon()) ? new BigDecimal("30000") : BigDecimal.ZERO;
        hoaDon.setPhiVanChuyen(phiVanChuyen);

        // Xử lý phiếu giảm giá
        BigDecimal tienGiam = BigDecimal.ZERO;
        if (hoaDonRequest.getIdPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(hoaDonRequest.getIdPhieuGiamGia())
                    .orElseThrow(() -> new RuntimeException("Phiếu giảm giá không tồn tại!"));
            if (phieuGiamGia.getSoLuongDung() <= 0 || !phieuGiamGia.getTrangThai()) {
                throw new RuntimeException("Phiếu giảm giá không khả dụng!");
            }
            tienGiam = BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa());
            phieuGiamGia.setSoLuongDung(phieuGiamGia.getSoLuongDung() - 1);
            if (phieuGiamGia.getSoLuongDung() == 0) {
                phieuGiamGia.setTrangThai(false);
            }
            phieuGiamGiaRepository.save(phieuGiamGia);
            hoaDon.setIdPhieuGiamGia(phieuGiamGia);
        }

        // Tính tổng tiền sau giảm
        BigDecimal tongTienSauGiam = tienSanPham.add(phiVanChuyen).subtract(tienGiam);
        hoaDon.setTienSanPham(tienSanPham);
        hoaDon.setTongTien(tongTienSauGiam);
        hoaDon.setTongTienSauGiam(tongTienSauGiam);

        // Lưu chi tiết hóa đơn
        List<HoaDonChiTiet> chiTietList = new ArrayList<>();
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại!"));
            chiTietSanPham.setDeleted(true);
            chiTietSanPhamRepository.save(chiTietSanPham);

            ImelDaBan imelDaBan = new ImelDaBan();
            imelDaBan.setMa("IMELDB_CLIENT_" + UUID.randomUUID().toString().substring(0, 8));
            imelDaBan.setImel(item.getMaImel());
            imelDaBan.setNgayBan(Date.from(Instant.now()));
            imelDaBan.setGhiChu("Bán kèm hóa đơn " + hoaDon.getMa());
            imelDaBanRepository.save(imelDaBan);

            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setIdChiTietSanPham(chiTietSanPham);
            chiTiet.setGia(item.getGiaBan());
            chiTiet.setIdImelDaBan(imelDaBan);
            chiTiet.setTrangThai((short) 1);
            chiTiet.setDeleted(false);
            chiTietList.add(chiTiet);
        }
        hoaDonChiTietRepository.saveAll(chiTietList);

        // Xử lý phương thức thanh toán (COD)
        HinhThucThanhToan hinhThuc = new HinhThucThanhToan();
        // Thay dòng 521
        List<PhuongThucThanhToan> phuongThucList = phuongThucThanhToanRepository.findAllByKieuThanhToan("Tiền mặt");
        if (phuongThucList.isEmpty()) {
            throw new RuntimeException("Phương thức thanh toán COD không tồn tại!");
        }
        PhuongThucThanhToan phuongThuc = phuongThucList.get(0); // Chọn bản ghi đầu tiên
        hinhThuc.setHoaDon(hoaDon);
        hinhThuc.setIdPhuongThucThanhToan(phuongThuc);
        hinhThuc.setTienMat(tongTienSauGiam);
        hinhThuc.setTienChuyenKhoan(BigDecimal.ZERO);
        hinhThuc.setMa(generateUniqueMaHinhThucThanhToan());
        hinhThuc.setDeleted(false);
        hinhThucThanhToanRepository.save(hinhThuc);

        // Cập nhật trạng thái hóa đơn
        hoaDon.setLoaiDon("online");
        hoaDon.setTrangThai((short) 0); // Đã thanh toán
        hoaDon.setNgayThanhToan(Instant.now());
        hoaDon.setDeleted(false);
        hoaDon = hoaDonRepository.save(hoaDon);

        // Lưu lịch sử hóa đơn
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setIdNhanVien(nhanVienRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại")));
        lichSu.setMa(hoaDon.getMa());
        lichSu.setHanhDong("Thanh toán hóa đơn qua client (COD)");
        lichSu.setThoiGian(Instant.now());
        lichSu.setDeleted(false);
        lichSuHoaDonRepository.save(lichSu);

        // Xóa giỏ hàng
        redisTemplate.delete(ghKey);

        // Gửi email
        HoaDonDetailResponse response = mapToHoaDonDetailResponse(hoaDon);
        guiEmailThongTinDonHang(response, hoaDonRequest.getEmail());

        return response;
    }

    @Override
    public Page<ChiTietSanPhamGroupDTO> layDanhSachSanPham(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword : null;
        List<Object[]> results = chiTietSanPhamRepository.findGroupedProductsBySanPhamIdAndKeyword(searchKeyword);
        List<ChiTietSanPhamGroupDTO> dtos = results.stream()
                .map(this::convertToChiTietSanPhamGroupDTO)
                .collect(Collectors.toList());

        int start = Math.min((page * size), dtos.size());
        int end = Math.min(((page + 1) * size), dtos.size());
        List<ChiTietSanPhamGroupDTO> pagedDtos = dtos.subList(start, end);

        return new PageImpl<>(pagedDtos, pageable, dtos.size());
    }

    @Override
    public List<PhieuGiamGiaCaNhan> layPhieuGiamGiaCaNhan(Integer idKhachHang) {
        return phieuGiamGiaCaNhanRepository.findByIdKhachHangId(idKhachHang);
    }

    @Override
    public Map<String, Object> timSanPhamTheoBarcodeHoacImei(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new RuntimeException("Mã barcode/IMEI không được để trống!");
        }

        Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(code.trim());
        if (chiTietSanPhamOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với mã barcode/IMEI: " + code);
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
        BigDecimal giaBan = chiTietSanPham.getGiaBan() != null ? chiTietSanPham.getGiaBan() : BigDecimal.ZERO;
        BigDecimal giaSauGiam = giaBan;

        Optional<ChiTietDotGiamGia> chiTietDotGiamGiaOpt = chiTietDotGiamGiaRepository.findByChiTietSanPhamIdAndActive(chiTietSanPham.getId());
        if (chiTietDotGiamGiaOpt.isPresent()) {
            giaSauGiam = chiTietDotGiamGiaOpt.get().getGiaSauKhiGiam() != null ? chiTietDotGiamGiaOpt.get().getGiaSauKhiGiam() : giaBan;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("chiTietSanPhamId", chiTietSanPham.getId());
        result.put("maImel", chiTietSanPham.getIdImel().getImel());
        result.put("tenSanPham", chiTietSanPham.getIdSanPham().getTenSanPham());
        result.put("mauSac", chiTietSanPham.getIdMauSac().getMauSac());
        result.put("ram", chiTietSanPham.getIdRam().getDungLuongRam());
        result.put("boNhoTrong", chiTietSanPham.getIdBoNhoTrong().getDungLuongBoNhoTrong());
        result.put("giaBan", giaSauGiam);
        result.put("giaBanGoc", giaSauGiam);
        result.put("giaBanBanDau", giaBan);
        result.put("stock", chiTietSanPhamRepository.countAvailableById(chiTietSanPham.getId()));

        return result;
    }

    @Override
    public KhachHang layThongTinKhachHang(Integer idKhachHang) {
        return khachHangRepository.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + idKhachHang));
    }

    @Override
    public void guiEmailThongTinDonHang(HoaDonDetailResponse hoaDonDetailResponse, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Thông tin đơn hàng #" + hoaDonDetailResponse.getMaHoaDon());

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<h2>Thông tin đơn hàng #" + hoaDonDetailResponse.getMaHoaDon() + "</h2>");
            emailContent.append("<p>Cảm ơn bạn đã mua sắm tại MobileWorld!</p>");

            // Thông tin khách hàng
            emailContent.append("<h3>Thông tin khách hàng</h3>");
            emailContent.append("<p><strong>Tên khách hàng:</strong> ").append(hoaDonDetailResponse.getTenKhachHang() != null ? hoaDonDetailResponse.getTenKhachHang() : "N/A").append("</p>");
            emailContent.append("<p><strong>Số điện thoại:</strong> ").append(hoaDonDetailResponse.getSoDienThoaiKhachHang() != null ? hoaDonDetailResponse.getSoDienThoaiKhachHang() : "N/A").append("</p>");
            emailContent.append("<p><strong>Email:</strong> ").append(hoaDonDetailResponse.getEmail() != null ? hoaDonDetailResponse.getEmail() : "N/A").append("</p>");
            emailContent.append("<p><strong>Địa chỉ giao hàng:</strong> ").append(hoaDonDetailResponse.getDiaChiKhachHang() != null ? hoaDonDetailResponse.getDiaChiKhachHang() : "N/A").append("</p>");

            // Chi tiết sản phẩm
            emailContent.append("<h3>Chi tiết đơn hàng</h3>");
            List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDonDetailResponse.getSanPhamChiTietInfos();
            if (sanPhamChiTietInfos == null || sanPhamChiTietInfos.isEmpty()) {
                emailContent.append("<p>Không có sản phẩm nào trong đơn hàng này.</p>");
            } else {
                emailContent.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
                emailContent.append("<tr><th>Sản phẩm</th><th>IMEI</th><th>Màu sắc</th><th>RAM</th><th>Bộ nhớ</th><th>Giá bán</th></tr>");
                for (var item : sanPhamChiTietInfos) {
                    emailContent.append("<tr>")
                            .append("<td>").append(item.getTenSanPham() != null ? item.getTenSanPham() : "N/A").append("</td>")
                            .append("<td>").append(item.getImel() != null ? item.getImel() : "N/A").append("</td>")
                            .append("<td>").append(item.getMauSac() != null ? item.getMauSac() : "N/A").append("</td>")
                            .append("<td>").append(item.getDungLuongRam() != null ? item.getDungLuongRam() : "N/A").append("</td>")
                            .append("<td>").append(item.getDungLuongBoNhoTrong() != null ? item.getDungLuongBoNhoTrong() : "N/A").append("</td>")
                            .append("<td>").append(formatPrice(item.getGiaBan() != null ? item.getGiaBan() : BigDecimal.ZERO)).append("</td>")
                            .append("</tr>");
                }
                emailContent.append("</table>");
            }

            // Thông tin thanh toán
            emailContent.append("<h3>Thông tin thanh toán</h3>");
            BigDecimal tongTienTruocGiam = (sanPhamChiTietInfos != null && !sanPhamChiTietInfos.isEmpty())
                    ? sanPhamChiTietInfos.stream().map(HoaDonDetailResponse.SanPhamChiTietInfo::getGiaBan).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;
            emailContent.append("<p><strong>Tổng tiền trước giảm:</strong> ").append(formatPrice(tongTienTruocGiam)).append("</p>");
            if (hoaDonDetailResponse.getTienGiam() != null && hoaDonDetailResponse.getTienGiam().compareTo(BigDecimal.ZERO) > 0) {
                emailContent.append("<p><strong>Tiền giảm (").append(hoaDonDetailResponse.getPhanTramGiam() != null ? hoaDonDetailResponse.getPhanTramGiam() : 0).append("%):</strong> ")
                        .append(formatPrice(hoaDonDetailResponse.getTienGiam())).append("</p>");
            }
            emailContent.append("<p><strong>Phí vận chuyển:</strong> ").append(formatPrice(hoaDonDetailResponse.getPhiVanChuyen() != null ? hoaDonDetailResponse.getPhiVanChuyen() : BigDecimal.ZERO)).append("</p>");
            emailContent.append("<p><strong>Tổng tiền sau giảm:</strong> ").append(formatPrice(hoaDonDetailResponse.getTongTienSauGiam() != null ? hoaDonDetailResponse.getTongTienSauGiam() : BigDecimal.ZERO)).append("</p>");
            emailContent.append("<p><strong>Phương thức thanh toán:</strong> COD (Thanh toán khi nhận hàng)</p>");

            // Trạng thái đơn hàng
            emailContent.append("<p><strong>Trạng thái:</strong> ").append(getTrangThaiText(hoaDonDetailResponse.getTrangThai())).append("</p>");

            // Link tra cứu
            emailContent.append("<p><strong>Mã hóa đơn:</strong> ").append(hoaDonDetailResponse.getMaHoaDon() != null ? hoaDonDetailResponse.getMaHoaDon() : "N/A").append("</p>");
            emailContent.append("<p>Vui lòng sử dụng mã hóa đơn để tra cứu trạng thái đơn hàng tại trang <a href='http://localhost:5173/invoice-status'>Tra cứu đơn hàng</a>.</p>");

            helper.setText(emailContent.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }

    private String getTrangThaiText(Short trangThai) {
        switch (trangThai) {
            case 0: return "Chờ thanh toán";
            case 1: return "Đã thanh toán";
            case 2: return "Đang giao";
            case 3: return "Đã giao";
            case 4: return "Đã hủy";
            default: return "Không xác định";
        }
    }

    private HoaDonDetailResponse mapToHoaDonDetailResponse(HoaDon hoaDon) {
        List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDonChiTietRepository.findById(hoaDon.getId())
                .stream()
                .map(hdct -> {
                    ChiTietSanPham ctsp = hdct.getIdChiTietSanPham();
                    return new HoaDonDetailResponse.SanPhamChiTietInfo(
                            ctsp.getMa(),
                            hoaDon.getId(),
                            ctsp.getIdSanPham().getMa(),
                            ctsp.getIdSanPham().getTenSanPham(),
                            hdct.getIdImelDaBan() != null ? hdct.getIdImelDaBan().getImel() : null,
                            hdct.getGia(),
                            hdct.getGhiChu(),
                            ctsp.getIdMauSac().getMauSac(),
                            ctsp.getIdRam().getDungLuongRam(),
                            ctsp.getIdBoNhoTrong().getDungLuongBoNhoTrong(),
                            ctsp.getIdAnhSanPham() != null ? ctsp.getIdAnhSanPham().getDuongDan() : null
                    );
                })
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.ThanhToanInfo> thanhToanInfos = hinhThucThanhToanRepository.findById(hoaDon.getId())
                .stream()
                .map(httt -> new HoaDonDetailResponse.ThanhToanInfo(
                        httt.getMa(),
                        httt.getIdPhuongThucThanhToan().getKieuThanhToan(),
                        httt.getTienChuyenKhoan(),
                        httt.getTienMat()
                ))
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuHoaDonInfos = lichSuHoaDonRepository.findById(hoaDon.getId())
                .stream()
                .map(lshd -> new HoaDonDetailResponse.LichSuHoaDonInfo(
                        lshd.getMa(),
                        lshd.getHanhDong(),
                        lshd.getThoiGian(),
                        lshd.getIdNhanVien().getTenNhanVien(),
                        lshd.getHoaDon().getId()
                ))
                .collect(Collectors.toList());

        return new HoaDonDetailResponse.Builder()
                .withHoaDonInfo(hoaDon, hoaDon.getIdPhieuGiamGia())
                .withNhanVienInfo(hoaDon.getIdNhanVien())
                .withThanhToanInfos(thanhToanInfos)
                .withSanPhamChiTietInfos(sanPhamChiTietInfos)
                .withLichSuHoaDonInfos(lichSuHoaDonInfos)
                .build();
    }

    private ChiTietSanPhamGroupDTO convertToChiTietSanPhamGroupDTO(Object[] result) {
        ChiTietSanPhamGroupDTO dto = new ChiTietSanPhamGroupDTO();
        dto.setMa((String) result[0]);
        dto.setTenSanPham((String) result[1]);
        dto.setMauSac((String) result[2]);
        dto.setDungLuongRam((String) result[3]);
        dto.setDungLuongBoNhoTrong((String) result[4]);
        dto.setSoLuong(((Number) result[5]).intValue());
        BigDecimal giaBan = result[6] != null ? (BigDecimal) result[6] : BigDecimal.ZERO;
        Integer chiTietSanPhamId = ((Number) result[7]).intValue();

        BigDecimal giaSauGiam = giaBan;
        Optional<ChiTietDotGiamGia> chiTietDotGiamGiaOpt = chiTietDotGiamGiaRepository.findByChiTietSanPhamIdAndActive(chiTietSanPhamId);
        if (chiTietDotGiamGiaOpt.isPresent()) {
            giaSauGiam = chiTietDotGiamGiaOpt.get().getGiaSauKhiGiam() != null ? chiTietDotGiamGiaOpt.get().getGiaSauKhiGiam() : giaBan;
        }

        dto.setIdSanPham(chiTietSanPhamId);
        dto.setGiaBan(giaSauGiam);
        dto.setGiaBanGoc(giaSauGiam);
        dto.setGiaBanBanDau(giaBan);
        return dto;
    }

    @Override
    @Transactional
    public void xoaHoaDonCho(Integer idHD) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Chỉ có thể xóa hóa đơn ở trạng thái chờ!");
        }

        // Kiểm tra giỏ hàng
        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gioHangDTO != null && !gioHangDTO.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng chưa trống, không thể xóa hóa đơn!");
        }

        // Khôi phục trạng thái IMEI và phiếu giảm giá nếu có
        List<GioHangTam> gioHangTamList = gioHangTamRepository.findByIdHoaDonAndDeletedFalse(idHD);
        for (GioHangTam gioHangTam : gioHangTamList) {
            Imel imel = imelRepository.findByImelAndDeleted(gioHangTam.getImei(), true)
                    .orElse(null);
            if (imel != null && !imelDaBanRepository.existsByMa(gioHangTam.getImei())) {
                imel.setDeleted(false);
                imelRepository.save(imel);
            }
            if (gioHangTam.getIdPhieuGiamGia() != null) {
                PhieuGiamGia pgg = phieuGiamGiaRepository.findById(gioHangTam.getIdPhieuGiamGia())
                        .orElse(null);
                if (pgg != null) {
                    pgg.setSoLuongDung(pgg.getSoLuongDung() + 1);
                    pgg.setTrangThai(true);
                    phieuGiamGiaRepository.save(pgg);
                }
            }
        }

        // Đánh dấu các bản ghi GioHangTam là đã xóa
        gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);

        // Xóa giỏ hàng khỏi Redis
        redisTemplate.delete(ghKey);

        // Xóa hóa đơn
        hoaDonRepository.delete(hoaDon);
    }
}