package com.example.be_datn.service.sell.impl;

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
import com.example.be_datn.entity.product.ImelDaBan;
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
    public void huyHDCho(Integer idHD) {
        if (!hoaDonRepository.existsById(idHD)) {
            throw new RuntimeException("Không tìm thấy hoá đơn có id: " + idHD);
        }
        hoaDonRepository.deleteById(idHD);
        xoaGioHang(idHD);
    }

    @Override
    @Transactional
    public HoaDonDTO taoHD(Integer khachHangId) {
        Integer khachHangIdToUse = (khachHangId != null) ? khachHangId : 1;
        KhachHang khachHang = khachHangRepository.findById(khachHangIdToUse)
                .orElseThrow(() -> new RuntimeException("Khách hàng với ID " + khachHangIdToUse + " không tồn tại"));

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
                .tenKhachHang("Khách lẻ")
                .diaChiKhachHang("N/A")
                .soDienThoaiKhachHang("N/A")
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
    public GioHangDTO themVaoGH(Integer idHD, ChiTietGioHangDTO chiTietGioHangDTO) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn có id: " + idHD));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hoá đơn này không phải hoá đơn chờ!");
        }

        // Validate product detail
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietGioHangDTO.getChiTietSanPhamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm!"));

        if (chiTietGioHangDTO.getMaImel() == null || chiTietGioHangDTO.getMaImel().isEmpty()) {
            throw new RuntimeException("IMEI không được để trống!");
        }

        List<String> availableIMEIs = chiTietSanPhamRepository.findIMEIsBySanPhamIdAndAttributes(
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
        }

        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gh = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gh == null) {
            gh = new GioHangDTO();
            gh.setGioHangId(ghKey);
            gh.setKhachHangId(hoaDon.getIdKhachHang().getId());
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
            newItem.setGiaBan(chiTietSanPham.getGiaBan());
            newItem.setSoLuong(1); // Each IMEI is one item
            newItem.setTongTien(chiTietSanPham.getGiaBan());

            // Check if IMEI already exists in cart
            boolean imeiExists = false;
            for (ChiTietGioHangDTO item : gh.getChiTietGioHangDTOS()) {
                if (item.getMaImel().equals(imei.trim())) {
                    imeiExists = true;
                    break;
                }
            }

            if (!imeiExists) {
                gh.getChiTietGioHangDTOS().add(newItem);
            }
        }

        // Update cart total
        gh.setTongTien(gh.getChiTietGioHangDTOS().stream()
                .map(ChiTietGioHangDTO::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        redisTemplate.opsForValue().set(ghKey, gh, 24, TimeUnit.HOURS);
        return gh;
    }
    public List<PhieuGiamGia> goiYPhieuGiamGia(Integer idKhachHang, BigDecimal tongTien) {
        List<PhieuGiamGia> goiY = new ArrayList<>();
        Date currentDate = new Date();
        Double tongTienDouble = tongTien.doubleValue();

        // Gợi ý phiếu giảm giá công khai
        List<PhieuGiamGia> congKhai = phieuGiamGiaRepository.findValidPublicVouchers(tongTienDouble, currentDate);
        goiY.addAll(congKhai);

        // Gợi ý phiếu giảm giá riêng tư
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

        if (maImel != null && !maImel.isEmpty()) {
            Imel imelEntity = imelRepository.findByImelAndDeleted(maImel.trim(), true)
                    .orElseThrow(() -> new RuntimeException("IMEI " + maImel + " không có trong giỏ hàng!"));

            if (!imelDaBanRepository.existsByMa(maImel.trim())) {
                imelEntity.setDeleted(false);
                imelRepository.save(imelEntity);
            }

            // Khôi phục PhieuGiamGia
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

            // Đánh dấu GioHangTam
            gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);
        }

        gh.setChiTietGioHangDTOS(updatedItems);
        gh.setTongTien(updatedItems.stream()
                .map(ChiTietGioHangDTO::getTongTien)
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

                if (item.getGiaBan().compareTo(chiTietSanPham.getGiaBan()) != 0) {
                    item.setGhiChuGia(String.format("Giá hiện tại: %s (Giá gốc: %s)",
                            chiTietSanPham.getGiaBan(), item.getGiaBan()));
                    // Không cập nhật giaBan và tongTien
                }
            }
            gh.setTongTien(gh.getChiTietGioHangDTOS().stream()
                    .map(ChiTietGioHangDTO::getTongTien)
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
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ");
        }

        return mapToHoaDonDto(hoaDon);
    }

    @Override
    public void xoaGioHang(Integer idHD) {
        String ghKey = GH_PREFIX + idHD;
        redisTemplate.delete(ghKey);
        gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);

        // Đánh dấu HoaDon là deleted nếu trangThai = 0
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn có id: " + idHD));
        if (hoaDon.getTrangThai() == 0) {
            hoaDon.setDeleted(true);
            hoaDonRepository.save(hoaDon);
        }

        // Khôi phục PhieuGiamGia
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
    }

    private String generateUniqueMaHinhThucThanhToan() {
        return "HTTT-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    @Transactional
    public HoaDonDTO thanhToan(Integer idHD, HoaDonRequest hoaDonRequest) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn có id: " + idHD));

        String ghKen = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKen);
        if (gioHangDTO == null || gioHangDTO.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể thanh toán!");
        }

        BigDecimal tienSanPham = gioHangDTO.getChiTietGioHangDTOS().stream()
                .map(ChiTietGioHangDTO::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTienSauGiam = tienSanPham;

        List<HoaDonChiTiet> chiTietList = new ArrayList<>();
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại!"));

            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setIdChiTietSanPham(chiTietSanPham);
            chiTiet.setGia(chiTietSanPham.getGiaBan());
            chiTiet.setTrangThai((short) 1); // Đánh dấu đã thanh toán
            chiTiet.setDeleted(false);
            chiTietList.add(chiTiet);
        }
        hoaDonChiTietRepository.saveAll(chiTietList);

        Set<HinhThucThanhToan> hinhThucThanhToans = hoaDonRequest.getHinhThucThanhToan();
        if (hinhThucThanhToans == null || hinhThucThanhToans.isEmpty()) {
            throw new RuntimeException("Thông tin thanh toán không được cung cấp!");
        }

        BigDecimal tongThanhToan = BigDecimal.ZERO;
        for (HinhThucThanhToan hinhThuc : hinhThucThanhToans) {
            hinhThuc.setHoaDon(hoaDon);
            hinhThucThanhToanRepository.save(hinhThuc);
            tongThanhToan = tongThanhToan.add(hinhThuc.getTienMat().add(hinhThuc.getTienChuyenKhoan()));
        }
        hoaDon.getHinhThucThanhToan().addAll(hinhThucThanhToans);

        if (tongThanhToan.compareTo(tongTienSauGiam) != 0) {
            throw new RuntimeException("Tổng tiền thanh toán không khớp với tổng tiền hóa đơn!");
        }

        hoaDon.setTienSanPham(tienSanPham);
        hoaDon.setTongTien(tienSanPham);
        hoaDon.setLoaiDon("trực tiếp");
        hoaDon.setTongTienSauGiam(tongTienSauGiam);
        hoaDon.setTrangThai((short) 1);
        hoaDon.setNgayThanhToan(Instant.now());
        hoaDon.setDeleted(false);
        hoaDon = hoaDonRepository.save(hoaDon);

        // Lưu lịch sử hóa đơn
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setIdNhanVien(nhanVienRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Nhân viên với ID 1 không tồn tại")));
        lichSu.setMa(hoaDon.getMa());
        lichSu.setHanhDong("Thanh toán hóa đơn");
        lichSu.setThoiGian(Instant.now());
        lichSu.setDeleted(false);
        lichSuHoaDonRepository.save(lichSu);

        xoaGioHang(idHD);

        return mapToHoaDonDto(hoaDon);
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

        // Chọn ChiTietSanPham có Imel khả dụng
        Optional<ChiTietSanPham> selected = chiTietSanPhams.stream()
                .filter(ctsp -> ctsp.getIdImel().getImel().equals(availableIMEIs.get(0)) && !ctsp.getIdImel().getDeleted())
                .findFirst();

        // Nếu không tìm thấy, chọn bản ghi có giá bán cao nhất
        if (selected.isEmpty()) {
            selected = chiTietSanPhams.stream()
                    .max(Comparator.comparing(ChiTietSanPham::getGiaBan));
        }

        ChiTietSanPham finalSelected = selected.orElse(chiTietSanPhams.get(0));
        return finalSelected.getId();
    }

    private ChiTietSanPhamGroupDTO convertToChiTietSanPhamGroupDTO(Object[] result) {
        ChiTietSanPhamGroupDTO dto = new ChiTietSanPhamGroupDTO();
        dto.setMa((String) result[0]);
        dto.setTenSanPham((String) result[1]);
        dto.setMauSac((String) result[2]);
        dto.setDungLuongRam((String) result[3]);
        dto.setDungLuongBoNhoTrong((String) result[4]);
        dto.setSoLuong(((Number) result[5]).intValue());
        dto.setGiaBan((BigDecimal) result[6]);
        dto.setIdSanPham(((Number) result[7]).intValue());
        return dto;
    }

    private HoaDonDTO mapToHoaDonDto(HoaDon hoaDon) {
        GioHangDTO gh = layGioHang(hoaDon.getId());
        List<ChiTietGioHangDTO> chiTietGioHangDTOS = gh.getChiTietGioHangDTOS();

        StringBuilder ghiChuGia = new StringBuilder();
        for (ChiTietGioHangDTO item : chiTietGioHangDTOS) {
            if (!item.getGhiChuGia().isEmpty()) {
                ghiChuGia.append(String.format("Sản phẩm %s (IMEI: %s): %s\n", item.getTenSanPham(), item.getMaImel(), item.getGhiChuGia()));
            }
        }

        return new HoaDonDTO(
                hoaDon.getId(),
                hoaDon.getIdKhachHang().getId(),
                hoaDon.getIdPhieuGiamGia() != null ? hoaDon.getIdPhieuGiamGia().getId() : null,
                hoaDon.getIdNhanVien().getId(),
                hoaDon.getMa(),
                hoaDon.getTienSanPham(),
                hoaDon.getLoaiDon(),
                hoaDon.getPhiVanChuyen(),
                hoaDon.getTongTien(),
                hoaDon.getTongTienSauGiam(),
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
}