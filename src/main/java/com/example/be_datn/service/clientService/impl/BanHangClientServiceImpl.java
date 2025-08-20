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

    //    @Override
//    @Transactional
//    public HoaDonDetailResponse taoHoaDonCho(Integer khachHangId) {
//        // Xử lý khách hàng
//        KhachHang khachHang = null;
//        if (khachHangId != null) {
//            khachHang = khachHangRepository.findById(khachHangId)
//                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại với ID: " + khachHangId));
//
//            List<HoaDon> pendingHoaDons = hoaDonRepository.findByIdKhachHangAndTrangThai(khachHang, (short) 6);
//            if (!pendingHoaDons.isEmpty()) {
//                // Reuse hóa đơn chờ đầu tiên, tránh spam
//                return mapToHoaDonDetailResponse(pendingHoaDons.get(0));
//            }
//        } else {
//            // Gán mặc định khách lẻ với id = 1
//            khachHang = khachHangRepository.findById(1)
//                    .orElseThrow(() -> new RuntimeException("Khách lẻ không tồn tại với ID: 1"));
//        }
//
//        // Lấy nhân viên mặc định (ID = 1)
//        NhanVien nhanVien = nhanVienRepository.findById(1)
//                .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại"));
//
//        // Tạo hóa đơn
//        HoaDon hoaDon = HoaDon.builder()
//                .idKhachHang(khachHang)
//                .idNhanVien(nhanVien)
//                .ma(generateRandomCode())
//                .tienSanPham(BigDecimal.ZERO)
//                .loaiDon("online")
//                .phiVanChuyen(BigDecimal.ZERO)
//                .tongTien(BigDecimal.ZERO)
//                .tongTienSauGiam(BigDecimal.ZERO)
//                .ghiChu("Hóa đơn chờ từ client")
//                .tenKhachHang(khachHang.getTen() != null ? khachHang.getTen() : "Khách lẻ")
//                .diaChiKhachHang("N/A")
//                .soDienThoaiKhachHang(khachHang.getIdTaiKhoan() != null ? khachHang.getIdTaiKhoan().getSoDienThoai() : "N/A")
//                .email("N/A")
//                .ngayTao(new Date())
//                .trangThai((short) 6)
//                .deleted(true)
//                .createdAt(new Date())
//                .createdBy(1)
//                .build();
//
//        hoaDon = hoaDonRepository.save(hoaDon);
//
//        // Tạo giỏ hàng trong Redis
//        GioHangDTO gioHangDTO = new GioHangDTO();
//        gioHangDTO.setGioHangId(GH_PREFIX + hoaDon.getId());
//        gioHangDTO.setKhachHangId(khachHang.getId());
//        gioHangDTO.setChiTietGioHangDTOS(new ArrayList<>());
//        gioHangDTO.setTongTien(BigDecimal.ZERO);
//        redisTemplate.opsForValue().set(GH_PREFIX + hoaDon.getId(), gioHangDTO, 24, TimeUnit.HOURS);
//
//        return mapToHoaDonDetailResponse(hoaDon);
//    }
    @Override
    @Transactional
    public HoaDonDetailResponse taoHoaDonCho(Integer khachHangId) {
        // Xử lý khách hàng
        KhachHang khachHang;
        if (khachHangId != null) {
            khachHang = khachHangRepository.findById(khachHangId)
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại với ID: " + khachHangId));
        } else {
            // Gán mặc định khách lẻ với id = 1 nếu không có khachHangId
            khachHang = khachHangRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Khách lẻ không tồn tại với ID: 1"));
        }

        // Kiểm tra hóa đơn chờ hiện có cho khách hàng
        List<HoaDon> pendingHoaDons = hoaDonRepository.findByIdKhachHangAndTrangThai(khachHang, (short) 6);
        if (!pendingHoaDons.isEmpty()) {
            // Reuse hóa đơn chờ đầu tiên
            return mapToHoaDonDetailResponse(pendingHoaDons.get(0));
        }

        // Lấy nhân viên mặc định (ID = 1)
        NhanVien nhanVien = nhanVienRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại"));

        // Tạo hóa đơn mới
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
                .tenKhachHang(khachHang.getTen() != null ? khachHang.getTen() : "Khách lẻ")
                .diaChiKhachHang("N/A")
                .soDienThoaiKhachHang(khachHang.getIdTaiKhoan() != null ? khachHang.getIdTaiKhoan().getSoDienThoai() : "N/A")
                .email("N/A")
                .ngayTao(new Date())
                .trangThai((short) 6)
                .deleted(true)
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
    public List<HoaDonDetailResponse> getPendingInvoicesByCustomer(Integer khachHangId) {
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại với ID: " + khachHangId));

        List<HoaDon> pendingHoaDons = hoaDonRepository.findByIdKhachHangAndTrangThai(khachHang, (short) 0);

        return pendingHoaDons.stream()
                .map(this::mapToHoaDonDetailResponse)  // Sử dụng method mapToHoaDonDetailResponse đã có
                .collect(Collectors.toList());
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

        // Kiểm tra trạng thái hóa đơn
        if (hoaDon.getTrangThai() != 6) {
            throw new RuntimeException("Hóa đơn này không phải hóa đơn chờ! Trạng thái hiện tại: " + getTrangThaiText(hoaDon.getTrangThai()));
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietGioHangDTO.getChiTietSanPhamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm!"));

        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gh = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gh == null) {
            gh = new GioHangDTO();
            gh.setGioHangId(ghKey);
            gh.setKhachHangId(hoaDon.getIdKhachHang() != null ? hoaDon.getIdKhachHang().getId() : null);
            gh.setChiTietGioHangDTOS(new ArrayList<>());
            gh.setTongTien(BigDecimal.ZERO);
        }

        ChiTietGioHangDTO newItem = new ChiTietGioHangDTO();
        newItem.setChiTietSanPhamId(chiTietGioHangDTO.getChiTietSanPhamId());
        newItem.setMaImel(""); // Để IMEI rỗng
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

        // Thêm sản phẩm vào giỏ hàng
        gh.getChiTietGioHangDTOS().add(newItem);

        // Lưu vào GioHangTam
        GioHangTam gioHangTam = GioHangTam.builder()
                .idHoaDon(idHD)
                .imei("") // Để IMEI rỗng
                .chiTietSanPhamId(chiTietGioHangDTO.getChiTietSanPhamId())
                .idPhieuGiamGia(chiTietGioHangDTO.getIdPhieuGiamGia())
                .createdAt(new Date().toInstant())
                .deleted(false)
                .build();
        gioHangTamRepository.save(gioHangTam);

        // Xử lý phiếu giảm giá
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

        // Cập nhật tổng tiền
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
        if (hoaDon.getTrangThai() != 6) {
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
        if (hoaDon.getTrangThai() != 6) {
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

        // Kiểm tra trạng thái Imel của ChiTietSanPham
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại!"));
            if (chiTietSanPham.getIdImel() == null) {
                throw new RuntimeException("Sản phẩm ID " + item.getChiTietSanPhamId() + " không có IMEI liên kết!");
            }
            Imel imel = imelRepository.findById(chiTietSanPham.getIdImel().getId())
                    .orElseThrow(() -> new RuntimeException("IMEI của sản phẩm ID " + item.getChiTietSanPhamId() + " không tồn tại!"));
            if (imel.getDeleted()) {
                throw new RuntimeException("IMEI của sản phẩm ID " + item.getChiTietSanPhamId() + " đã được sử dụng!");
            }
        }

        // Xử lý khách hàng và địa chỉ
        KhachHang khachHang = null;
        if (hoaDonRequest.getIdKhachHang() != null && hoaDonRequest.getIdKhachHang() > 0) {
            khachHang = khachHangRepository.findById(hoaDonRequest.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại với ID: " + hoaDonRequest.getIdKhachHang()));
            hoaDon.setIdKhachHang(khachHang);
            // Đồng bộ thông tin khách hàng từ KhachHang nếu có, trừ khi được cung cấp trong request
            hoaDon.setTenKhachHang(hoaDonRequest.getTenKhachHang() != null ? hoaDonRequest.getTenKhachHang() : khachHang.getTen());
            hoaDon.setSoDienThoaiKhachHang(hoaDonRequest.getSoDienThoaiKhachHang() != null ? hoaDonRequest.getSoDienThoaiKhachHang() :
                    (khachHang.getIdTaiKhoan() != null ? khachHang.getIdTaiKhoan().getSoDienThoai() : "N/A"));
            hoaDon.setEmail(hoaDonRequest.getEmail() != null ? hoaDonRequest.getEmail() : "N/A");
        } else {
            // Khách vãng lai
            hoaDon.setIdKhachHang(khachHangRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Khách hàng mặc định không tồn tại!")));
            hoaDon.setTenKhachHang(hoaDonRequest.getTenKhachHang());
            hoaDon.setSoDienThoaiKhachHang(hoaDonRequest.getSoDienThoaiKhachHang());
            hoaDon.setEmail(hoaDonRequest.getEmail());
        }

        // Xử lý địa chỉ
        String diaChiCuThe = hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe();
        if ("online".equals(hoaDonRequest.getLoaiDon())) {
            diaChiCuThe = String.format("%s, %s, %s, %s",
                    hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe(),
                    hoaDonRequest.getDiaChiKhachHang().getPhuong(),
                    hoaDonRequest.getDiaChiKhachHang().getQuan(),
                    hoaDonRequest.getDiaChiKhachHang().getThanhPho());
        }
        hoaDon.setDiaChiKhachHang(diaChiCuThe);

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

        // Lưu chi tiết hóa đơn mà không gán IMEI ngay
        List<HoaDonChiTiet> chiTietList = new ArrayList<>();
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại!"));

            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setIdChiTietSanPham(chiTietSanPham);
            chiTiet.setGia(item.getGiaBan());
            chiTiet.setIdImelDaBan(null); // Không gán IMEI lúc này
            chiTiet.setTrangThai((short) 1); // Đặt trạng thái chờ xác nhận
            chiTiet.setDeleted(false);
            chiTietList.add(chiTiet);
        }
        hoaDonChiTietRepository.saveAll(chiTietList);

        // Xử lý phương thức thanh toán (COD)
        HinhThucThanhToan hinhThuc = new HinhThucThanhToan();
        List<PhuongThucThanhToan> phuongThucList = phuongThucThanhToanRepository.findAllByKieuThanhToan("Tiền mặt");
        if (phuongThucList.isEmpty()) {
            throw new RuntimeException("Phương thức thanh toán COD không tồn tại!");
        }
        PhuongThucThanhToan phuongThuc = phuongThucList.get(0);
        hinhThuc.setHoaDon(hoaDon);
        hinhThuc.setIdPhuongThucThanhToan(phuongThuc);
        hinhThuc.setTienMat(tongTienSauGiam);
        hinhThuc.setTienChuyenKhoan(BigDecimal.ZERO);
        hinhThuc.setMa(generateUniqueMaHinhThucThanhToan());
        hinhThuc.setDeleted(false);
        hinhThucThanhToanRepository.save(hinhThuc);

        // Cập nhật trạng thái hóa đơn
        hoaDon.setLoaiDon(hoaDonRequest.getLoaiDon());
        hoaDon.setTrangThai((short) 0); // Đã thanh toán, chờ xác nhận IMEI
        hoaDon.setNgayThanhToan(Instant.now());
        hoaDon.setDeleted(false);
        hoaDon = hoaDonRepository.save(hoaDon);

        // Lưu lịch sử hóa đơn
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setIdNhanVien(nhanVienRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại")));
        lichSu.setMa(hoaDon.getMa());
        lichSu.setHanhDong("Thanh toán hóa đơn qua client (COD), chờ xác nhận IMEI");
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
    @Transactional
    public HoaDonDetailResponse xacNhanVaGanImei(Integer idHD, Map<Integer, String> imelMap) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));
        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ xác nhận IMEI!");
        }

        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonIdAndDeletedFalse(idHD);
        for (HoaDonChiTiet chiTiet : chiTietList) {
            Integer chiTietId = chiTiet.getIdChiTietSanPham().getId();
            String newImel = imelMap.get(chiTietId);
            if (newImel == null || newImel.trim().isEmpty()) {
                throw new RuntimeException("IMEI cho sản phẩm ID " + chiTietId + " không được cung cấp!");
            }

            ChiTietSanPham chiTietSanPham = chiTiet.getIdChiTietSanPham();

            // Kiểm tra và xử lý IMEI cũ
            // Trong BanHangClientServiceImpl.java
// Kiểm tra và xử lý IMEI cũ
            ImelDaBan oldImelDaBan = chiTiet.getIdImelDaBan();
            if (oldImelDaBan != null) {
                // Gỡ tham chiếu trong HoaDonChiTiet trước khi xóa
                chiTiet.setIdImelDaBan(null);
                hoaDonChiTietRepository.save(chiTiet);

                Imel oldImel = imelRepository.findByImelAndDeleted(oldImelDaBan.getImel(), true)
                        .orElse(null);
                if (oldImel != null) {
                    oldImel.setDeleted(false);
                    imelRepository.save(oldImel);
                }

                // Khôi phục trạng thái ChiTietSanPham cũ
                ChiTietSanPham oldChiTietSanPham = chiTiet.getIdChiTietSanPham();
                if (oldChiTietSanPham != null) {
                    oldChiTietSanPham.setDeleted(false);
                    chiTietSanPhamRepository.save(oldChiTietSanPham);
                }

                imelDaBanRepository.delete(oldImelDaBan);
            }

            // Kiểm tra IMEI mới hợp lệ
            Imel newImelEntity = imelRepository.findByImelAndDeleted(newImel, false)
                    .orElseThrow(() -> new RuntimeException("IMEI " + newImel + " không tồn tại hoặc đã được sử dụng!"));

            Optional<ChiTietSanPham> newChiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(newImel);
            if (newChiTietSanPhamOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy chi tiết sản phẩm cho IMEI " + newImel);
            }
            ChiTietSanPham newChiTietSanPham = newChiTietSanPhamOpt.get();

            if (!newChiTietSanPham.getIdSanPham().getId().equals(chiTietSanPham.getIdSanPham().getId()) ||
                    !newChiTietSanPham.getIdRam().getId().equals(chiTietSanPham.getIdRam().getId()) ||
                    !newChiTietSanPham.getIdBoNhoTrong().getId().equals(chiTietSanPham.getIdBoNhoTrong().getId())) {
                throw new RuntimeException("IMEI " + newImel + " không khớp với thông số sản phẩm!");
            }

            // Đặt trạng thái deleted của IMEI mới
            newImelEntity.setDeleted(true);
            imelRepository.save(newImelEntity);

            // Tạo và lưu ImelDaBan mới
            ImelDaBan imelDaBan = ImelDaBan.builder()
                    .imel(newImel)
                    .ngayBan(new Date())
                    .ghiChu("Đã gán cho hóa đơn " + hoaDon.getMa())
                    .deleted(false)
                    .build();
            imelDaBanRepository.save(imelDaBan);

            // Cập nhật ChiTietSanPham mới
            chiTiet.setIdChiTietSanPham(newChiTietSanPham);
            newChiTietSanPham.setDeleted(true);
            chiTietSanPhamRepository.save(newChiTietSanPham);

            // Cập nhật HoaDonChiTiet
            chiTiet.setIdImelDaBan(imelDaBan);
            chiTiet.setTrangThai((short) 2);
            hoaDonChiTietRepository.save(chiTiet);
        }

        hoaDon.setTrangThai((short) 1);
        hoaDon = hoaDonRepository.save(hoaDon);

        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setIdNhanVien(nhanVienRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại")));
        lichSu.setMa("LSHD_" + UUID.randomUUID().toString().substring(0, 8));
        lichSu.setHanhDong("Xác nhận và gán IMEI cho hóa đơn " + hoaDon.getMa());
        lichSu.setThoiGian(Instant.now());
        lichSu.setDeleted(false);
        lichSuHoaDonRepository.save(lichSu);

        return mapToHoaDonDetailResponse(hoaDon);
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
            helper.setSubject("Xác nhận đơn hàng #" + hoaDonDetailResponse.getMaHoaDon() + " - MobileWorld");

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<!DOCTYPE html>")
                    .append("<html lang='vi'>")
                    .append("<head>")
                    .append("<meta charset='UTF-8'>")
                    .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                    .append("<style>")
                    .append("body { font-family: 'Arial', sans-serif; background-color: #f4f7fa; margin: 0; padding: 0; }")
                    .append(".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); overflow: hidden; }")
                    .append(".header { background: linear-gradient(90deg, #002c69, #13ad75); color: #ffffff; padding: 20px; text-align: center; position: relative; }")
                    .append(".logo { max-width: 100px; position: absolute; top: 10px; left: 20px; }")
                    .append(".header h1 { margin: 0; font-size: 24px; }")
                    .append(".content { padding: 20px; }")
                    .append("h2 { color: #002c69; font-size: 20px; margin-bottom: 15px; }")
                    .append("p { color: #4B5563; font-size: 16px; line-height: 1.6; margin: 10px 0; }")
                    .append(".info-box { background-color: #f9fafb; padding: 15px; border-radius: 6px; margin-bottom: 20px; }")
                    .append(".table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
                    .append(".table th, .table td { padding: 12px; text-align: left; border-bottom: 1px solid #e5e7eb; }")
                    .append(".table th { background-color: #002c69; color: #ffffff; font-weight: bold; }")
                    .append(".table td { background-color: #ffffff; color: #1F2937; }")
                    .append(".total { font-weight: bold; font-size: 16px; color: #002c69; }")
                    .append(".button { display: inline; padding: 0; background: none; color: #002c69; text-decoration: underline; transition: color 0.3s; }")
                    .append(".button:hover { color: #13ad75; }")
                    .append(".cancel-button { color: #EF4444; margin-left: 10px; }")
                    .append(".cancel-button:hover { color: #DC2626; }")
                    .append(".footer { background-color: #f4f7fa; padding: 15px; text-align: center; font-size: 14px; color: #6B7280; }")
                    .append(".footer a { color: #002c69; text-decoration: none; }")
                    .append("</style>")
                    .append("</head>")
                    .append("<body>")
                    .append("<div class='container'>")
                    .append("<div class='header'>")
                    .append("<img src='https://res.cloudinary.com/dwusxbhbr/image/upload/v1752927151/logo_rucu1t.png' alt='MobileWorld Logo' class='logo'>")
                    .append("<h1>Xác nhận đơn hàng #" + hoaDonDetailResponse.getMaHoaDon() + "</h1>")
                    .append("</div>")
                    .append("<div class='content'>")
                    .append("<p>Kính gửi Quý khách " + (hoaDonDetailResponse.getTenKhachHang() != null ? hoaDonDetailResponse.getTenKhachHang() : "N/A") + ",</p>")
                    .append("<p>Cảm ơn Quý khách đã tin tưởng và mua sắm tại <strong>MobileWorld</strong>! Dưới đây là thông tin chi tiết về đơn hàng của Quý khách.</p>")

                    // Thông tin khách hàng
                    .append("<div class='info-box'>")
                    .append("<h2>Thông tin khách hàng</h2>")
                    .append("<p><strong>Họ và tên:</strong> " + (hoaDonDetailResponse.getTenKhachHang() != null ? hoaDonDetailResponse.getTenKhachHang() : "N/A") + "</p>")
                    .append("<p><strong>Số điện thoại:</strong> " + (hoaDonDetailResponse.getSoDienThoaiKhachHang() != null ? hoaDonDetailResponse.getSoDienThoaiKhachHang() : "N/A") + "</p>")
                    .append("<p><strong>Email:</strong> " + (hoaDonDetailResponse.getEmail() != null ? hoaDonDetailResponse.getEmail() : "N/A") + "</p>")
                    .append("<p><strong>Địa chỉ giao hàng:</strong> " + (hoaDonDetailResponse.getDiaChiKhachHang() != null ? hoaDonDetailResponse.getDiaChiKhachHang() : "N/A") + "</p>")
                    .append("</div>")

                    // Chi tiết sản phẩm
                    .append("<h2>Chi tiết đơn hàng</h2>");
            List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDonDetailResponse.getSanPhamChiTietInfos();
            if (sanPhamChiTietInfos == null || sanPhamChiTietInfos.isEmpty()) {
                emailContent.append("<p>Không có sản phẩm nào trong đơn hàng này.</p>");
            } else {
                emailContent.append("<table class='table'>")
                        .append("<tr><th>Sản phẩm</th><th>IMEI</th><th>Màu sắc</th><th>RAM</th><th>Bộ nhớ</th><th>Giá bán</th></tr>");
                for (var item : sanPhamChiTietInfos) {
                    emailContent.append("<tr>")
                            .append("<td>" + (item.getTenSanPham() != null ? item.getTenSanPham() : "N/A") + "</td>")
                            .append("<td>" + (item.getImel() != null ? item.getImel() : "N/A") + "</td>")
                            .append("<td>" + (item.getMauSac() != null ? item.getMauSac() : "N/A") + "</td>")
                            .append("<td>" + (item.getDungLuongRam() != null ? item.getDungLuongRam() : "N/A") + "</td>")
                            .append("<td>" + (item.getDungLuongBoNhoTrong() != null ? item.getDungLuongBoNhoTrong() : "N/A") + "</td>")
                            .append("<td>" + formatPrice(item.getGiaBan() != null ? item.getGiaBan() : BigDecimal.ZERO) + "</td>")
                            .append("</tr>");
                }
                emailContent.append("</table>");
            }

            // Thông tin thanh toán
            emailContent.append("<div class='info-box'>")
                    .append("<h2>Thông tin thanh toán</h2>")
                    .append("<p><strong>Tổng tiền sản phẩm:</strong> " + formatPrice((sanPhamChiTietInfos != null && !sanPhamChiTietInfos.isEmpty())
                            ? sanPhamChiTietInfos.stream().map(HoaDonDetailResponse.SanPhamChiTietInfo::getGiaBan).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
                            : BigDecimal.ZERO) + "</p>");
            if (hoaDonDetailResponse.getTienGiam() != null && hoaDonDetailResponse.getTienGiam().compareTo(BigDecimal.ZERO) > 0) {
                emailContent.append("<p><strong>Giảm giá (" + (hoaDonDetailResponse.getPhanTramGiam() != null ? hoaDonDetailResponse.getPhanTramGiam() : 0) + "%):</strong> "
                        + formatPrice(hoaDonDetailResponse.getTienGiam()) + "</p>");
            }
            emailContent.append("<p><strong>Phí vận chuyển:</strong> " + formatPrice(hoaDonDetailResponse.getPhiVanChuyen() != null ? hoaDonDetailResponse.getPhiVanChuyen() : BigDecimal.ZERO) + "</p>")
                    .append("<p class='total'><strong>Tổng tiền thanh toán:</strong> " + formatPrice(hoaDonDetailResponse.getTongTienSauGiam() != null ? hoaDonDetailResponse.getTongTienSauGiam() : BigDecimal.ZERO) + "</p>")
                    .append("<p><strong>Phương thức thanh toán:</strong> COD (Thanh toán khi nhận hàng)</p>")
                    .append("</div>")

                    // Trạng thái đơn hàng
                    .append("<p><strong>Trạng thái đơn hàng:</strong> " + getTrangThaiText(hoaDonDetailResponse.getTrangThai()) + "</p>")

                    // Link tra cứu và hủy đơn
                    .append("<p><strong>Mã hóa đơn:</strong> " + (hoaDonDetailResponse.getMaHoaDon() != null ? hoaDonDetailResponse.getMaHoaDon() : "N/A") + "</p>")
                    .append("<p>Vui lòng sử dụng mã hóa đơn để tra cứu trạng thái đơn hàng tại <a href='http://localhost:5173/invoice-status' class='button'>Tra cứu đơn hàng</a> ")
                    .append("hoặc hủy đơn hàng tại<a href='http://localhost:5173/cancel-order?id=" + hoaDonDetailResponse.getId() + "' class='button cancel-button'>Hủy đơn hàng</a>.</p>")

                    // Footer
                    .append("</div>")
                    .append("<div class='footer'>")
                    .append("<p>Cảm ơn Quý khách đã lựa chọn MobileWorld. Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ qua email <a href='mailto:support@mobileworld.com'>support@mobileworld.com</a> hoặc số hotline: 1800-123-456.</p>")
                    .append("<p>© 2025 MobileWorld. All rights reserved.</p>")
                    .append("</div>")
                    .append("</div>")
                    .append("</body>")
                    .append("</html>");

            helper.setText(emailContent.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }

    private String getTrangThaiText(Short trangThai) {
        switch (trangThai) {
            case 0:
                return "Chờ thanh toán";
            case 1:
                return "Đã thanh toán";
            case 2:
                return "Đang giao";
            case 3:
                return "Đã giao";
            case 4:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    private HoaDonDetailResponse mapToHoaDonDetailResponse(HoaDon hoaDon) {
        List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDonChiTietRepository.findById(hoaDon.getId())
                .stream()
                .map(hdct -> {
                    ChiTietSanPham ctsp = hdct.getIdChiTietSanPham();
                    return new HoaDonDetailResponse.SanPhamChiTietInfo(
                            ctsp.getId(),
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
    @Transactional(rollbackFor = Exception.class)  // Đảm bảo rollback nếu lỗi
    public void xoaHoaDonCho(Integer idHD) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + idHD));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Chỉ có thể xóa hóa đơn ở trạng thái chờ (0)!");
        }

        // Kiểm tra giỏ rỗng qua Redis
        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gioHangDTO != null && !gioHangDTO.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng chưa trống, không thể xóa hóa đơn!");
        }

        // Kiểm tra và xóa gio_hang_tam (đánh dấu deleted và khôi phục IMEI/phiểu giảm)
        List<GioHangTam> gioHangTamList = gioHangTamRepository.findByIdHoaDonAndDeletedFalse(idHD);
        for (GioHangTam gioHangTam : gioHangTamList) {
            // Khôi phục IMEI
            Imel imel = imelRepository.findByImelAndDeleted(gioHangTam.getImei(), true).orElse(null);
            if (imel != null && !imelDaBanRepository.existsByMa(gioHangTam.getImei())) {
                imel.setDeleted(false);
                imelRepository.save(imel);
            }
            // Khôi phục phiếu giảm
            if (gioHangTam.getIdPhieuGiamGia() != null) {
                PhieuGiamGia pgg = phieuGiamGiaRepository.findById(gioHangTam.getIdPhieuGiamGia()).orElse(null);
                if (pgg != null) {
                    pgg.setSoLuongDung(pgg.getSoLuongDung() + 1);
                    pgg.setTrangThai(true);
                    phieuGiamGiaRepository.save(pgg);
                }
            }
        }
        gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);  // Đánh dấu deleted

        // Xóa các bảng phụ thuộc để tránh FK conflict
        // Xóa hoa_don_chi_tiet (nếu có, dù logic không nên có)
        List<HoaDonChiTiet> chiTiets = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());
        if (!chiTiets.isEmpty()) {
            hoaDonChiTietRepository.deleteAll(chiTiets);
            // Hoặc tối ưu: hoaDonChiTietRepository.deleteByHoaDonId(hoaDon.getId());
        }

        // Xóa lich_su_hoa_don
        lichSuHoaDonRepository.deleteByHoaDon(hoaDon);  // Giả sử có method deleteByHoaDon

        // Xóa hinh_thuc_thanh_toan
        hinhThucThanhToanRepository.deleteByHoaDon(hoaDon);  // Tương tự

        // Xóa Redis
        redisTemplate.delete(ghKey);

        // Cuối cùng xóa hóa đơn
        hoaDonRepository.delete(hoaDon);
    }


}