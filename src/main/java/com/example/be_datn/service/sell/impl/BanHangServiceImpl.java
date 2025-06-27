package com.example.be_datn.service.sell.impl;

import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.dto.sale.GioHangTam;
import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.pay.request.HinhThucThanhToanDTO;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
import com.example.be_datn.entity.account.DiaChiKhachHang;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.order.LichSuHoaDon;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import com.example.be_datn.entity.pay.PhuongThucThanhToan;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.repository.account.DiaChiKH.DiaChiKhachHangRepository;
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
import com.example.be_datn.service.sell.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BanHangServiceImpl implements BanHangService {
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

    @Autowired
    private ImelRepository imelRepository;

    @Autowired
    private ImelDaBanRepository imelDaBanRepository;

    @Autowired
    private GioHangTamRepository gioHangTamRepository;

    private static final String GH_PREFIX = "gh:hd:";

    private String generatedRandomCode() {
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(character.charAt(random.nextInt(character.length())));
        }
        return "HD_" + code.toString();
    }

    @Override
    public List<HoaDon> getHDCho() {
        return hoaDonRepository.findAllHDNotConfirm();
    }

    @Override
    @Transactional
    public void huyHD(Integer idHD) {
        if (!hoaDonRepository.existsById(idHD)) {
            throw new RuntimeException("Không tìm thấy hoá đơn có id: " + idHD);
        }
        hoaDonRepository.deleteById(idHD);

    }

    @Override
    @Transactional
    public HoaDonDTO taoHD(Integer khachHangId) {
        Integer khachHangIdToUse = (khachHangId != null) ? khachHangId : 1;
        KhachHang khachHang = khachHangRepository.findById(khachHangIdToUse)
                .orElseGet(() -> {
                    KhachHang guest = new KhachHang();
                    guest.setId(1);
                    guest.setTen("Khách lẻ");
                    return khachHangRepository.save(guest);
                });

        Integer nhanVienId = 1;
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Nhân viên với ID 1 không tồn tại"));

        HoaDon hoaDon = HoaDon.builder()
                .idKhachHang(khachHang)
                .idNhanVien(nhanVien)
                .ma(generatedRandomCode())
                .tienSanPham(BigDecimal.ZERO)
                .loaiDon("trực tiếp")
                .phiVanChuyen(BigDecimal.ZERO)
                .tongTien(BigDecimal.ZERO)
                .tongTienSauGiam(BigDecimal.ZERO)
                .ghiChu("N/A")
                .tenKhachHang(khachHang.getTen())
                .diaChiKhachHang("N/A")
                .soDienThoaiKhachHang(khachHang.getIdTaiKhoan() != null ? khachHang.getIdTaiKhoan().getSoDienThoai() : "N/A")
                .email("N/A")
                .ngayTao(new Date())
                .trangThai((short) 0)
                .deleted(true)
                .createdAt(new Date())
                .createdBy(nhanVienId)
                .build();

        hoaDon = hoaDonRepository.save(hoaDon);

        GioHangDTO gioHangDTO = new GioHangDTO();
        gioHangDTO.setGioHangId(GH_PREFIX + hoaDon.getId());
        gioHangDTO.setKhachHangId(khachHangId);
        gioHangDTO.setChiTietGioHangDTOS(new ArrayList<>());
        gioHangDTO.setTongTien(BigDecimal.ZERO);
        redisTemplate.opsForValue().set(GH_PREFIX + hoaDon.getId(), gioHangDTO, 24, TimeUnit.HOURS);
        return mapToHoaDonDto(hoaDon);
    }

    @Override
    @Transactional
    public GioHangDTO themVaoGH(Integer idHD, ChiTietGioHangDTO chiTietGioHangDTO) {
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

            // Ensure giaBan is not null
            BigDecimal giaBan = chiTietSanPham.getGiaBan() != null ? chiTietSanPham.getGiaBan() : BigDecimal.ZERO;
            newItem.setGiaBan(giaBan);
            newItem.setGiaBanGoc(giaBan);
            newItem.setGhiChuGia("");
            newItem.setSoLuong(1);
            newItem.setTongTien(giaBan);

            Optional<ChiTietSanPham> latestChiTietSanPham = chiTietSanPhamRepository.findByImel(imei.trim());
            if (latestChiTietSanPham.isPresent()) {
                BigDecimal latestGiaBan = latestChiTietSanPham.get().getGiaBan() != null ? latestChiTietSanPham.get().getGiaBan() : BigDecimal.ZERO;
                BigDecimal inputGiaBan = chiTietGioHangDTO.getGiaBan() != null ? chiTietGioHangDTO.getGiaBan() : BigDecimal.ZERO;

                if (latestGiaBan.compareTo(inputGiaBan) != 0) {
                    newItem.setGhiChuGia(String.format("Giá hiện tại: %s (Giá gốc: %s)",
                            latestGiaBan, inputGiaBan));
                    newItem.setGiaBan(latestGiaBan);
                    newItem.setTongTien(latestGiaBan);
                }
            }

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

    public List<PhieuGiamGia> goiYPhieuGiamGia(Integer idKhachHang, BigDecimal tongTien) {
        List<PhieuGiamGia> goiY = new ArrayList<>();
        Date currentDate = new Date();
        Double tongTienDouble = tongTien != null ? tongTien.doubleValue() : 0.0;

        List<PhieuGiamGia> congKhai = phieuGiamGiaRepository.findValidPublicVouchers(tongTienDouble, currentDate);
        goiY.addAll(congKhai);

        if (idKhachHang != null && idKhachHang > 0) {
            List<PhieuGiamGiaCaNhan> pggCaNhans = phieuGiamGiaCaNhanRepository.findValidPrivateVouchersByKhachHang(
                    idKhachHang, tongTienDouble, currentDate);
            for (PhieuGiamGiaCaNhan pggCaNhan : pggCaNhans) {
                goiY.add(pggCaNhan.getIdPhieuGiamGia());
            }
        }

        return goiY;
    }

    @Override
    @Transactional
    public GioHangDTO xoaSanPhamKhoiGioHang(Integer idHD, Integer spId, String maImel) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn có id: " + idHD));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hoá đơn này không phải hoá đơn chờ!");
        }

        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gh = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gh == null || gh.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống hoặc không tồn tại!");
        }

        List<ChiTietGioHangDTO> updatedItems = gh.getChiTietGioHangDTOS().stream()
                .filter(item -> {
                    boolean keepItem;
                    if (maImel != null && !maImel.isEmpty()) {
                        keepItem = !item.getMaImel().equals(maImel.trim());
                    } else {
                        keepItem = !item.getChiTietSanPhamId().equals(spId);
                    }
                    return keepItem;
                })
                .collect(Collectors.toList());

        // Xử lý xóa IMEI
        if (maImel != null && !maImel.isEmpty()) {
            Imel imelEntity = imelRepository.findByImelAndDeleted(maImel.trim(), true)
                    .orElseThrow(() -> new RuntimeException("IMEI " + maImel + " không có trong giỏ hàng!"));
            if (!imelDaBanRepository.existsByMa(maImel.trim())) {
                imelEntity.setDeleted(false);
                imelRepository.save(imelEntity);
                System.out.println("Đã đặt lại deleted = false cho IMEI: " + maImel);
            }
        } else {
            // Xử lý khi xóa toàn bộ sản phẩm (spId)
            List<ChiTietGioHangDTO> removedItems = gh.getChiTietGioHangDTOS().stream()
                    .filter(item -> item.getChiTietSanPhamId().equals(spId))
                    .collect(Collectors.toList());
            for (ChiTietGioHangDTO item : removedItems) {
                Imel imelEntity = imelRepository.findByImelAndDeleted(item.getMaImel().trim(), true)
                        .orElse(null);
                if (imelEntity != null && !imelDaBanRepository.existsByMa(item.getMaImel().trim())) {
                    imelEntity.setDeleted(false);
                    imelRepository.save(imelEntity);
                    System.out.println("Đã đặt lại deleted = false cho IMEI: " + item.getMaImel());
                }
            }
        }

        // Cập nhật phiếu giảm giá
        List<Integer> idPhieuGiamGias = gioHangTamRepository.findIdPhieuGiamGiaByIdHoaDonAndDeletedFalse(idHD);
        for (Integer idPhieuGiamGia : idPhieuGiamGias) {
            if (idPhieuGiamGia != null) {
                PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idPhieuGiamGia).orElse(null);
                if (pgg != null) {
                    pgg.setSoLuongDung(pgg.getSoLuongDung() + 1);
                    phieuGiamGiaRepository.save(pgg);
                }
            }
        }

        gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);

        gh.setChiTietGioHangDTOS(updatedItems);
        gh.setTongTien(updatedItems.stream()
                .map(item -> item.getTongTien() != null ? item.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        redisTemplate.opsForValue().set(ghKey, gh, 24, TimeUnit.HOURS);
        return gh;
    }

    @Override
    public GioHangDTO layGioHang(Integer idHD) {
        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gh = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gh == null) {
            HoaDon hoaDon = hoaDonRepository.findById(idHD)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn với ID " + idHD + " không tồn tại"));
            gh = new GioHangDTO();
            gh.setGioHangId(ghKey);
            gh.setKhachHangId(hoaDon.getIdKhachHang() != null ? hoaDon.getIdKhachHang().getId() : null);
            gh.setChiTietGioHangDTOS(new ArrayList<>());
            gh.setTongTien(BigDecimal.ZERO);
            redisTemplate.opsForValue().set(ghKey, gh, 24, TimeUnit.HOURS);
        } else {
            for (ChiTietGioHangDTO item : gh.getChiTietGioHangDTOS()) {
                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(item.getMaImel());
                if (chiTietSanPhamOpt.isEmpty()) {
                    continue;
                }
                ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();

                BigDecimal itemGiaBan = item.getGiaBan() != null ? item.getGiaBan() : BigDecimal.ZERO;
                BigDecimal chiTietGiaBan = chiTietSanPham.getGiaBan() != null ? chiTietSanPham.getGiaBan() : BigDecimal.ZERO;

                if (itemGiaBan.compareTo(chiTietGiaBan) != 0) {
                    item.setGhiChuGia(String.format("Giá hiện tại: %s (Giá gốc: %s)",
                            chiTietGiaBan, itemGiaBan));
                }
            }
            gh.setTongTien(gh.getChiTietGioHangDTOS().stream()
                    .map(item -> item.getTongTien() != null ? item.getTongTien() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            redisTemplate.opsForValue().set(ghKey, gh, 24, TimeUnit.HOURS);
        }
        return gh;
    }

    @Override
    public HoaDonDTO layChiTietHoaDonCho(Integer idHD) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn có id: " + idHD));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ!");
        }

        return mapToHoaDonDto(hoaDon);
    }

    @Override
    @Transactional
    public void xoaGioHang(Integer idHD) {
        String ghKey = GH_PREFIX + idHD;
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn có id: " + idHD));

        if (hoaDon.getTrangThai() == 0) {
            hoaDon.setDeleted(true);
            hoaDonRepository.save(hoaDon);
        }

        List<Integer> idPhieuGiamGias = gioHangTamRepository.findIdPhieuGiamGiaByIdHoaDonAndDeletedFalse(idHD);
        for (Integer idPhieuGiamGia : idPhieuGiamGias) {
            if (idPhieuGiamGia != null) {
                PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idPhieuGiamGia).orElse(null);
                if (pgg != null) {
                    pgg.setSoLuongDung(pgg.getSoLuongDung() + 1);
                    phieuGiamGiaRepository.save(pgg);
                }
            }
        }

        redisTemplate.delete(ghKey);
        gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);
    }

    private String generateUniqueMaHinhThucThanhToan() {
        return "HTTT-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    @Transactional
    public HoaDonDTO thanhToan(Integer idHD, HoaDonRequest hoaDonRequest) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn có id: " + idHD));

        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gioHangDTO == null || gioHangDTO.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể thanh toán!");
        }

        // Kiểm tra giá và cập nhật ghiChuGia
        StringBuilder ghiChuGia = new StringBuilder();
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(item.getMaImel());
            if (chiTietSanPhamOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy chi tiết sản phẩm cho IMEI: " + item.getMaImel());
            }
            ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();

            BigDecimal itemGiaBan = item.getGiaBan() != null ? item.getGiaBan() : BigDecimal.ZERO;
            BigDecimal chiTietGiaBan = chiTietSanPham.getGiaBan() != null ? chiTietSanPham.getGiaBan() : BigDecimal.ZERO;

            if (itemGiaBan.compareTo(chiTietGiaBan) != 0) {
                item.setGhiChuGia(String.format("Giá hiện tại: %s (Giá gốc: %s)",
                        chiTietGiaBan, itemGiaBan));
                ghiChuGia.append(String.format("Sản phẩm %s (IMEI: %s): %s\n",
                        item.getTenSanPham(), item.getMaImel(), item.getGhiChuGia()));
            }
        }

        // Xử lý thông tin khách hàng
        KhachHang khachHang = null;
        if (hoaDonRequest.getIdKhachHang() != null) {
            khachHang = khachHangRepository.findById(hoaDonRequest.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng với ID " + hoaDonRequest.getIdKhachHang() + " không tồn tại"));
            hoaDon.setIdKhachHang(khachHang);
        }

        // Gán tenKhachHang và soDienThoaiKhachHang từ hoaDonRequest
        hoaDon.setTenKhachHang(hoaDonRequest.getTenKhachHang() != null ? hoaDonRequest.getTenKhachHang() : (khachHang != null ? khachHang.getTen() : "Khách lẻ"));
        hoaDon.setSoDienThoaiKhachHang(hoaDonRequest.getSoDienThoaiKhachHang() != null ? hoaDonRequest.getSoDienThoaiKhachHang() : (khachHang != null ? khachHang.getIdTaiKhoan().getSoDienThoai() : null));

        // Xử lý diaChiKhachHang
        if ("online".equals(hoaDonRequest.getLoaiDon()) && hoaDonRequest.getDiaChiKhachHang() != null) {
            DiaChiKhachHang diaChi = hoaDonRequest.getDiaChiKhachHang();
            String diaChiChuoi = String.format("%s, %s, %s, %s",
                    diaChi.getDiaChiCuThe() != null ? diaChi.getDiaChiCuThe() : "",
                    diaChi.getPhuong() != null ? diaChi.getPhuong() : "",
                    diaChi.getQuan() != null ? diaChi.getQuan() : "",
                    diaChi.getThanhPho() != null ? diaChi.getThanhPho() : "");
            hoaDon.setDiaChiKhachHang(diaChiChuoi);
        } else {
            hoaDon.setDiaChiKhachHang(hoaDonRequest.getDiaChiKhachHang() != null ?
                    String.format("%s, %s, %s, %s",
                            hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe() != null ? hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe() : "Trực tiếp",
                            hoaDonRequest.getDiaChiKhachHang().getPhuong() != null ? hoaDonRequest.getDiaChiKhachHang().getPhuong() : "",
                            hoaDonRequest.getDiaChiKhachHang().getQuan() != null ? hoaDonRequest.getDiaChiKhachHang().getQuan() : "",
                            hoaDonRequest.getDiaChiKhachHang().getThanhPho() != null ? hoaDonRequest.getDiaChiKhachHang().getThanhPho() : "") : "Trực tiếp");
        }

        // Tính tiền sản phẩm
        BigDecimal tienSanPham = gioHangDTO.getChiTietGioHangDTOS().stream()
                .map(item -> item.getTongTien() != null ? item.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Gán phí vận chuyển
        BigDecimal phiVANChuyen = hoaDonRequest.getPhiVanChuyen() != null
                ? hoaDonRequest.getPhiVanChuyen()
                : BigDecimal.ZERO;
        hoaDon.setPhiVanChuyen(phiVANChuyen);

        // Xử lý phiếu giảm giá
        BigDecimal giamGia = BigDecimal.ZERO;
        if (hoaDonRequest.getIdPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(hoaDonRequest.getIdPhieuGiamGia())
                    .orElseThrow(() -> new RuntimeException("Phiếu giảm giá với ID " + hoaDonRequest.getIdPhieuGiamGia() + " không tồn tại"));
            Date currentDate = new Date();

            // Kiểm tra điều kiện phiếu giảm giá
            if (!phieuGiamGia.getTrangThai() || phieuGiamGia.getDeleted()) {
                throw new IllegalArgumentException("Mã giảm giá không hợp lệ hoặc đã bị vô hiệu hóa.");
            }
            if (phieuGiamGia.getNgayKetThuc() != null && phieuGiamGia.getNgayKetThuc().before(currentDate)) {
                throw new IllegalArgumentException("Mã giảm giá đã hết hạn.");
            }
            if (phieuGiamGia.getSoLuongDung() != null && phieuGiamGia.getSoLuongDung() <= 0) {
                throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng.");
            }
            BigDecimal hoaDonToiThieu = phieuGiamGia.getHoaDonToiThieu() != null ? new BigDecimal(phieuGiamGia.getHoaDonToiThieu().toString()) : BigDecimal.ZERO;
            if (tienSanPham.compareTo(hoaDonToiThieu) < 0) {
                throw new IllegalArgumentException("Tổng tiền hóa đơn không đủ để áp dụng mã giảm giá này.");
            }

            // Kiểm tra phiếu giảm giá riêng tư
            if (phieuGiamGia.getRiengTu()) {
                if (hoaDonRequest.getIdKhachHang() == null || hoaDonRequest.getIdKhachHang() <= 0) {
                    throw new IllegalArgumentException("Phiếu giảm giá riêng tư yêu cầu thông tin khách hàng.");
                }
                Optional<PhieuGiamGiaCaNhan> pggCaNhan = phieuGiamGiaCaNhanRepository.findValidPrivateVoucherByIdPhieuGiamGiaAndIdKhachHang(
                        phieuGiamGia.getId(), hoaDonRequest.getIdKhachHang(), currentDate);
                if (!pggCaNhan.isPresent()) {
                    throw new IllegalArgumentException("Phiếu giảm giá này không áp dụng cho khách hàng.");
                }
            } else {
                Optional<PhieuGiamGia> validPublicVoucher = phieuGiamGiaRepository.findByma(phieuGiamGia.getMa());
                if (!validPublicVoucher.isPresent()) {
                    throw new IllegalArgumentException("Phiếu giảm giá công khai không hợp lệ.");
                }
            }

            // Tính giá trị giảm giá
            giamGia = BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa());
            if (phieuGiamGia.getSoLuongDung() != null && phieuGiamGia.getSoLuongDung() > 0) {
                phieuGiamGia.setSoLuongDung(phieuGiamGia.getSoLuongDung() - 1);
                phieuGiamGiaRepository.save(phieuGiamGia);
            }
            hoaDon.setIdPhieuGiamGia(phieuGiamGia);
        }

        // Tính tổng tiền sau giảm giá
        BigDecimal tongTienSauGiam = tienSanPham.add(phiVANChuyen).subtract(giamGia);
        hoaDon.setTongTien(tienSanPham.add(phiVANChuyen));
        hoaDon.setTongTienSauGiam(tongTienSauGiam);

        // Xử lý chi tiết hóa đơn và ImelDaBan
        List<HoaDonChiTiet> chiTietList = new ArrayList<>();
        List<ImelDaBan> imelDaBanList = new ArrayList<>();
        int index = 1;
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(item.getMaImel());
            if (chiTietSanPhamOpt.isEmpty()) {
                throw new RuntimeException("Chi tiết sản phẩm không tồn tại cho IMEI: " + item.getMaImel());
            }
            ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();

            // Kiểm tra Imel đã được đánh dấu là deleted
            Imel imelEntity = chiTietSanPham.getIdImel();
            if (!imelEntity.getDeleted()) {
                throw new RuntimeException("IMEI " + item.getMaImel() + " không hợp lệ để thanh toán!");
            }

            // Tạo chi tiết hóa đơn
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setIdChiTietSanPham(chiTietSanPham);
            chiTiet.setGia(chiTietSanPham.getGiaBan()); // Sử dụng giá từ cơ sở dữ liệu
            chiTiet.setTrangThai((short) 1);
            chiTiet.setDeleted(false);
            String ma = String.format("HDCT-%d-%d", idHD, index++);
            chiTiet.setMa(ma);
            chiTietList.add(chiTiet);

            // Tạo bản ghi ImelDaBan
            ImelDaBan imelDaBan = ImelDaBan.builder()
                    .imel(item.getMaImel())
                    .ngayBan(new Date())
                    .deleted(false)
                    .ma("IMELDB-" + item.getMaImel())
                    .ghiChu("Đã bán qua hóa đơn " + hoaDon.getMa() + (item.getGhiChuGia().isEmpty() ? "" : "; " + item.getGhiChuGia()))
                    .build();
            imelDaBanList.add(imelDaBan);
        }
        hoaDonChiTietRepository.saveAll(chiTietList);
        imelDaBanRepository.saveAll(imelDaBanList);

        // Xử lý hình thức thanh toán
        Set<HinhThucThanhToan> hinhThucThanhToans = new HashSet<>();
        Set<HinhThucThanhToanDTO> dtos = hoaDonRequest.getHinhThucThanhToan();
        if (dtos == null || dtos.isEmpty()) {
            throw new RuntimeException("Thông tin thanh toán không được cung cấp!");
        }
        BigDecimal tongThanhToan = BigDecimal.ZERO;
        for (HinhThucThanhToanDTO dto : dtos) {
            PhuongThucThanhToan phuongThuc = phuongThucThanhToanRepository.findById(dto.getPhuongThucThanhToanId())
                    .orElseThrow(() -> new RuntimeException("Phương thức thanh toán với ID " + dto.getPhuongThucThanhToanId() + " không tồn tại!"));
            HinhThucThanhToan hinhThuc = new HinhThucThanhToan();
            hinhThuc.setHoaDon(hoaDon);
            hinhThuc.setIdPhuongThucThanhToan(phuongThuc);
            hinhThuc.setTienMat(dto.getTienMat() != null ? dto.getTienMat() : BigDecimal.ZERO);
            hinhThuc.setTienChuyenKhoan(dto.getTienChuyenKhoan() != null ? dto.getTienChuyenKhoan() : BigDecimal.ZERO);
            hinhThuc.setMa(generateUniqueMaHinhThucThanhToan());
            hinhThuc.setDeleted(false);
            hinhThucThanhToanRepository.save(hinhThuc);
            hinhThucThanhToans.add(hinhThuc);
            tongThanhToan = tongThanhToan.add(hinhThuc.getTienMat().add(hinhThuc.getTienChuyenKhoan()));
        }
        hoaDon.getHinhThucThanhToan().addAll(hinhThucThanhToans);

        if (tongThanhToan.compareTo(tongTienSauGiam) != 0) {
            throw new RuntimeException("Tổng tiền thanh toán (" + tongThanhToan + ") không khớp với tổng tiền hóa đơn (" + tongTienSauGiam + ")!");
        }

        // Cập nhật thông tin hóa đơn
        hoaDon.setTienSanPham(tienSanPham);
        hoaDon.setLoaiDon(hoaDonRequest.getLoaiDon() != null ? hoaDonRequest.getLoaiDon() : "trực tiếp");
        hoaDon.setTrangThai("online".equals(hoaDonRequest.getLoaiDon()) ? (short) 0 : (short) 1);
        hoaDon.setNgayThanhToan(Instant.now());
        hoaDon.setDeleted(false);
        hoaDon = hoaDonRepository.save(hoaDon);

        // Lưu lịch sử hóa đơn
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setIdNhanVien(nhanVienRepository.findById(hoaDonRequest.getIdNhanVien() != null ? hoaDonRequest.getIdNhanVien() : 1)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại")));
        lichSu.setMa(hoaDon.getMa());
        lichSu.setHanhDong("online".equals(hoaDonRequest.getLoaiDon()) ? "Tạo đơn hàng online" : "Thanh toán hóa đơn");
        lichSu.setThoiGian(Instant.now());
        lichSu.setDeleted(false);
        lichSuHoaDonRepository.save(lichSu);

        xoaGioHang(idHD);

        // Cập nhật ghi chú giá trong HoaDonDTO
        HoaDonDTO hoaDonDTO = mapToHoaDonDto(hoaDon);
        hoaDonDTO.setGhiChuGia(ghiChuGia.toString());
        return hoaDonDTO;
    }

    @Override
    @Cacheable(value = "sanPhamCache", key = "#page + '-' + #size + '-' + #keyword")
    public Page<ChiTietSanPhamGroupDTO> getAllCTSP(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        List<Object[]> results = chiTietSanPhamRepository.findGroupedProductsBySanPhamId(null);
        List<ChiTietSanPhamGroupDTO> dtos = results.stream().map(this::convertToChiTietSanPhamGroupDTO).collect(Collectors.toList());

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            dtos = dtos.stream()
                    .filter(dto -> dto.getTenSanPham().toLowerCase().contains(lowerKeyword) ||
                            dto.getMa().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }

        int start = Math.min((page * size), dtos.size());
        int end = Math.min(((page + 1) * size), dtos.size());
        List<ChiTietSanPhamGroupDTO> pagedDtos = dtos.subList(start, end);

        return new PageImpl<>(pagedDtos, pageable, dtos.size());
    }

    @Override
    public List<String> getIMEIsBySanPhamIdAndAttributes(Integer sanPhamId, String mauSac, String dungLuongRam, String dungLuongBoNhoTrong) {
        return chiTietSanPhamRepository.findIMEIsBySanPhamIdAndAttributes(sanPhamId, mauSac, dungLuongRam, dungLuongBoNhoTrong);
    }

    @Override
    public Integer getChiTietSanPhamId(Integer sanPhamId, String mauSac, String dungLuongRam, String dungLuongBoNhoTrong) {
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findByIdSanPhamIdAndAttributes(
                sanPhamId, mauSac, dungLuongRam, dungLuongBoNhoTrong);

        if (chiTietSanPhams.isEmpty()) {
            throw new RuntimeException("Không tìm thấy chi tiết sản phẩm!");
        }

        List<String> availableIMEIs = chiTietSanPhamRepository.findAvailableIMEIsBySanPhamIdAndAttributes(
                sanPhamId, mauSac, dungLuongRam, dungLuongBoNhoTrong);

        if (availableIMEIs.isEmpty()) {
            throw new RuntimeException("Không có IMEI khả dụng cho sản phẩm này!");
        }

        Optional<ChiTietSanPham> selected = chiTietSanPhams.stream()
                .filter(ctsp -> ctsp.getIdImel().getImel().equals(availableIMEIs.get(0)) && !ctsp.getIdImel().getDeleted())
                .findFirst();

        if (selected.isEmpty()) {
            selected = chiTietSanPhams.stream()
                    .max(Comparator.comparing(ctsp -> ctsp.getGiaBan() != null ? ctsp.getGiaBan() : BigDecimal.ZERO));
        }

        ChiTietSanPham finalSelected = selected.orElse(chiTietSanPhams.get(0));
        return finalSelected.getId();
    }

    @Override
    public ChiTietSanPham getChiTietSanPhamByIMEI(String imei) {
        Optional<ChiTietSanPham> chiTietSanPham = chiTietSanPhamRepository.findByImel(imei);
        return chiTietSanPham.orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm với IMEI: " + imei));
    }

    private ChiTietSanPhamGroupDTO convertToChiTietSanPhamGroupDTO(Object[] result) {
        ChiTietSanPhamGroupDTO dto = new ChiTietSanPhamGroupDTO();
        dto.setMa((String) result[0]);
        dto.setTenSanPham((String) result[1]);
        dto.setMauSac((String) result[2]);
        dto.setDungLuongRam((String) result[3]);
        dto.setDungLuongBoNhoTrong((String) result[4]);
        dto.setSoLuong(((Number) result[5]).intValue());
        dto.setGiaBan(result[6] != null ? (BigDecimal) result[6] : BigDecimal.ZERO);
        dto.setIdSanPham(((Number) result[7]).intValue());
        return dto;
    }

    private HoaDonDTO mapToHoaDonDto(HoaDon hoaDon) {
        GioHangDTO gh = layGioHang(hoaDon.getId());
        List<ChiTietGioHangDTO> chiTietGioHangDTOS = gh.getChiTietGioHangDTOS();

        StringBuilder ghiChuGia = new StringBuilder();
        for (ChiTietGioHangDTO item : chiTietGioHangDTOS) {
            if (item.getGhiChuGia() != null && !item.getGhiChuGia().isEmpty()) {
                ghiChuGia.append(String.format("Sản phẩm %s (IMEI: %s): %s\n", item.getTenSanPham(), item.getMaImel(), item.getGhiChuGia()));
            }
        }

        return new HoaDonDTO(
                hoaDon.getId(),
                hoaDon.getIdKhachHang() != null ? hoaDon.getIdKhachHang().getId() : null,
                hoaDon.getIdPhieuGiamGia() != null ? hoaDon.getIdPhieuGiamGia().getId() : null,
                hoaDon.getIdNhanVien() != null ? hoaDon.getIdNhanVien().getId() : null,
                hoaDon.getMa(),
                hoaDon.getTienSanPham() != null ? hoaDon.getTienSanPham() : BigDecimal.ZERO,
                hoaDon.getLoaiDon(),
                hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO,
                hoaDon.getTongTien() != null ? hoaDon.getTongTien() : BigDecimal.ZERO,
                hoaDon.getTongTienSauGiam() != null ? hoaDon.getTongTienSauGiam() : BigDecimal.ZERO,
                hoaDon.getGhiChu(),
                hoaDon.getTenKhachHang(),
                hoaDon.getDiaChiKhachHang(),
                hoaDon.getSoDienThoaiKhachHang(),
                hoaDon.getEmail(),
                hoaDon.getNgayTao(),
                hoaDon.getTrangThai(),
                chiTietGioHangDTOS,
                ghiChuGia.toString()
        );
    }

    @Override
    public List<PhieuGiamGiaCaNhan> findByKhachHangId(Integer idKhachHang) {
        return phieuGiamGiaCaNhanRepository.findByIdKhachHangId(idKhachHang);
    }

    @Override
    public Map<String, Object> findProductByBarcodeOrImei(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new RuntimeException("Mã barcode/IMEI không được để trống!");
        }

        // Tìm chi tiết sản phẩm dựa trên IMEI
        Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(code.trim());
        if (chiTietSanPhamOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với mã barcode/IMEI: " + code);
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
        Imel imel = chiTietSanPham.getIdImel();

        Map<String, Object> result = new HashMap<>();
        result.put("chiTietSanPhamId", chiTietSanPham.getId());
        result.put("maImel", imel.getImel());
        result.put("tenSanPham", chiTietSanPham.getIdSanPham().getTenSanPham());
        result.put("mauSac", chiTietSanPham.getIdMauSac().getMauSac());
        result.put("ram", chiTietSanPham.getIdRam().getDungLuongRam());
        result.put("boNhoTrong", chiTietSanPham.getIdBoNhoTrong().getDungLuongBoNhoTrong());
        result.put("giaBan", chiTietSanPham.getGiaBan() != null ? chiTietSanPham.getGiaBan() : BigDecimal.ZERO);
        result.put("stock", 1); // Giả định stock là 1 vì IMEI là duy nhất

        return result;
    }
}