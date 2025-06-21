package com.example.be_datn.service.sell.impl;

import com.example.be_datn.dto.sale.GioHangTam;
import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.pay.request.HinhThucThanhToanDTO;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
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

            Imel imelEntity = imelRepository.findByImelAndDeleted(imei.trim(), false)
                    .orElseThrow(() -> new RuntimeException("IMEI " + imei + " không khả dụng!"));

            imelEntity.setDeleted(true);
            imelRepository.save(imelEntity);
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
            newItem.setGiaBanGoc(chiTietSanPham.getGiaBan());
            newItem.setGhiChuGia("");
            newItem.setSoLuong(1);
            newItem.setTongTien(chiTietSanPham.getGiaBan());

            boolean imeiExists = false;
            for (ChiTietGioHangDTO item : gh.getChiTietGioHangDTOS()) {
                if (item.getMaImel().equals(imei.trim())) {
                    imeiExists = true;
                    break;
                }
            }

            if (!imeiExists) {
                for (ChiTietGioHangDTO existingItem : gh.getChiTietGioHangDTOS()) {
                    if (existingItem.getChiTietSanPhamId().equals(newItem.getChiTietSanPhamId()) &&
                            existingItem.getGiaBan().compareTo(newItem.getGiaBan()) != 0) {
                        newItem.setGhiChuGia(String.format("Tăng từ %s lên %s", existingItem.getGiaBan(), newItem.getGiaBan()));
                        break;
                    }
                }
                gh.getChiTietGioHangDTOS().add(newItem);

                // Lưu vào GioHangTam
                GioHangTam gioHangTam = GioHangTam.builder()
                        .idHoaDon(idHD)
                        .imei(imei.trim())
                        .chiTietSanPhamId(chiTietGioHangDTO.getChiTietSanPhamId())
                        .idPhieuGiamGia(chiTietGioHangDTO.getIdPhieuGiamGia()) // Giả định DTO có trường này
                        .createdAt(new Date().toInstant())
                        .deleted(false)
                        .build();
                gioHangTamRepository.save(gioHangTam);
            }
        }

        // Giảm tạm thời soLuongDung của PhieuGiamGia nếu áp dụng
        if (chiTietGioHangDTO.getIdPhieuGiamGia() != null) {
            Optional<PhieuGiamGia> optionalPGG = phieuGiamGiaRepository.findById(chiTietGioHangDTO.getIdPhieuGiamGia());
            if (!optionalPGG.isPresent()) {
                throw new IllegalArgumentException("Mã giảm giá không tồn tại.");
            }
            PhieuGiamGia pgg = optionalPGG.get();

            // Kiểm tra các điều kiện chung
            if (!pgg.getTrangThai() || pgg.getDeleted()) {
                throw new IllegalArgumentException("Mã giảm giá không hợp lệ hoặc đã bị vô hiệu hóa.");
            }
            if (pgg.getNgayKetThuc() != null && pgg.getNgayKetThuc().before(new Date())) {
                throw new IllegalArgumentException("Mã giảm giá đã hết hạn.");
            }
            if (pgg.getSoLuongDung() != null && pgg.getSoLuongDung() <= 0) {
                throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng.");
            }

            // Kiểm tra giá trị đơn hàng tối thiểu
            BigDecimal hoaDonToiThieu = pgg.getHoaDonToiThieu() != null ? new BigDecimal(pgg.getHoaDonToiThieu().toString()) : BigDecimal.ZERO;
            ghKey = GH_PREFIX + idHD;
            gh = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
            if (gh == null) {
                throw new IllegalStateException("Giỏ hàng chưa được khởi tạo.");
            }
            BigDecimal tongTien = gh.getTongTien();
            if (tongTien.compareTo(hoaDonToiThieu) < 0) {
                throw new IllegalArgumentException("Tổng tiền giỏ hàng không đủ để áp dụng mã giảm giá này.");
            }

            // Kiểm tra phiếu giảm giá riêng tư
            if (pgg.getRiengTu()) {
                Integer khachHangId = null;
                if (hoaDon.getIdKhachHang() != null) {
                    khachHangId = hoaDon.getIdKhachHang().getId();
                }
                if (khachHangId == null || khachHangId <= 0) {
                    throw new IllegalArgumentException("Phiếu giảm giá riêng tư yêu cầu thông tin khách hàng.");
                }
                Optional<PhieuGiamGiaCaNhan> pggCaNhan = phieuGiamGiaCaNhanRepository.findValidPrivateVoucherByIdPhieuGiamGiaAndIdKhachHang(
                        pgg.getId(), khachHangId, new Date());
                if (!pggCaNhan.isPresent()) {
                    throw new IllegalArgumentException("Phiếu giảm giá này không áp dụng cho khách hàng.");
                }
                PhieuGiamGiaCaNhan pggCaNhanEntity = pggCaNhan.get();
                if (!pggCaNhanEntity.getTrangThai() || pggCaNhanEntity.getDeleted()) {
                    throw new IllegalArgumentException("Phiếu giảm giá cá nhân không hợp lệ.");
                }
            } else {
                // Kiểm tra phiếu giảm giá công khai
                Optional<PhieuGiamGia> validPublicVoucher = phieuGiamGiaRepository.findValidPublicVoucherByMa(pgg.getMa(), new Date());
                if (!validPublicVoucher.isPresent()) {
                    throw new IllegalArgumentException("Phiếu giảm giá công khai không hợp lệ.");
                }
            }

            // Giảm số lượt sử dụng tạm thời
            if (pgg.getSoLuongDung() != null && pgg.getSoLuongDung() > 0) {
                pgg.setSoLuongDung(pgg.getSoLuongDung() - 1);
                phieuGiamGiaRepository.save(pgg);
            }
        }

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
            gh.setKhachHangId(hoaDon.getIdKhachHang().getId());
            gh.setChiTietGioHangDTOS(new ArrayList<>());
            gh.setTongTien(BigDecimal.ZERO);
            redisTemplate.opsForValue().set(ghKey, gh, 24, TimeUnit.HOURS);
        } else {
            // Kiểm tra và cập nhật ghi chú giá cho các mục trong giỏ hàng
            for (ChiTietGioHangDTO item : gh.getChiTietGioHangDTOS()) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm!"));
                if (item.getGiaBan().compareTo(chiTietSanPham.getGiaBan()) != 0) {
                    item.setGhiChuGia(String.format("Tăng từ %s lên %s", item.getGiaBan(), chiTietSanPham.getGiaBan()));
                    item.setGiaBan(chiTietSanPham.getGiaBan()); // Cập nhật giá mới
                    item.setTongTien(chiTietSanPham.getGiaBan()); // Cập nhật tổng tiền
                }
            }
            // Cập nhật tổng tiền giỏ hàng
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
        // Tìm hóa đơn
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn có id: " + idHD));
        // Kiểm tra giỏ hàng trong Redis
        String ghKey = GH_PREFIX + idHD;
        GioHangDTO gioHangDTO = (GioHangDTO) redisTemplate.opsForValue().get(ghKey);
        if (gioHangDTO == null || gioHangDTO.getChiTietGioHangDTOS().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể thanh toán!");
        }

        // Cập nhật giá và ghi chú giá trước khi thanh toán
        StringBuilder ghiChuGia = new StringBuilder();
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm!"));
            if (item.getGiaBan().compareTo(chiTietSanPham.getGiaBan()) != 0) {
                item.setGhiChuGia(String.format("Tăng từ %s lên %s", item.getGiaBan(), chiTietSanPham.getGiaBan()));
                item.setGiaBan(chiTietSanPham.getGiaBan()); // Cập nhật giá mới
                item.setTongTien(chiTietSanPham.getGiaBan()); // Cập nhật tổng tiền
                ghiChuGia.append(String.format("Sản phẩm %s (IMEI: %s): %s\n", item.getTenSanPham(), item.getMaImel(), item.getGhiChuGia()));
            }
        }

        // Tính tổng tiền sản phẩm
        BigDecimal tienSanPham = gioHangDTO.getChiTietGioHangDTOS().stream()
                .map(ChiTietGioHangDTO::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Xử lý phí vận chuyển
        BigDecimal phiVanChuyen = hoaDonRequest.getPhiVanChuyen() != null ? hoaDonRequest.getPhiVanChuyen() : BigDecimal.ZERO;

        // Xử lý mã giảm giá
        BigDecimal giamGia = BigDecimal.ZERO;
        if (hoaDonRequest.getIdPhieuGiamGia() != null) {
            Optional<PhieuGiamGia> optionalPGG = phieuGiamGiaRepository.findById(hoaDonRequest.getIdPhieuGiamGia());
            if (!optionalPGG.isPresent()) {
                throw new IllegalArgumentException("Mã giảm giá không tồn tại.");
            }
            PhieuGiamGia pgg = optionalPGG.get();
            Date currentDate = new Date();

            // Kiểm tra điều kiện chung
            if (!pgg.getTrangThai() || pgg.getDeleted()) {
                throw new IllegalArgumentException("Mã giảm giá không hợp lệ hoặc đã bị vô hiệu hóa.");
            }
            if (pgg.getNgayKetThuc() != null && pgg.getNgayKetThuc().before(currentDate)) {
                throw new IllegalArgumentException("Mã giảm giá đã hết hạn.");
            }
            if (pgg.getSoLuongDung() != null && pgg.getSoLuongDung() <= 0) {
                throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng.");
            }
            BigDecimal hoaDonToiThieu = pgg.getHoaDonToiThieu() != null ? new BigDecimal(pgg.getHoaDonToiThieu().toString()) : BigDecimal.ZERO;
            if (tienSanPham.compareTo(hoaDonToiThieu) < 0) {
                throw new IllegalArgumentException("Tổng tiền hóa đơn không đủ để áp dụng mã giảm giá này.");
            }

            // Kiểm tra phiếu giảm giá riêng tư
            if (pgg.getRiengTu()) {
                if (hoaDonRequest.getIdKhachHang() == null || hoaDonRequest.getIdKhachHang() <= 0) {
                    throw new IllegalArgumentException("Phiếu giảm giá riêng tư yêu cầu thông tin khách hàng.");
                }
                Optional<PhieuGiamGiaCaNhan> pggCaNhan = phieuGiamGiaCaNhanRepository.findValidPrivateVoucherByIdPhieuGiamGiaAndIdKhachHang(
                        pgg.getId(), hoaDonRequest.getIdKhachHang(), currentDate);
                if (!pggCaNhan.isPresent()) {
                    throw new IllegalArgumentException("Phiếu giảm giá này không áp dụng cho khách hàng.");
                }
            } else {
                // Kiểm tra phiếu giảm giá công khai
                Optional<PhieuGiamGia> validPublicVoucher = phieuGiamGiaRepository.findByma(pgg.getMa());
                if (!validPublicVoucher.isPresent()) {
                    throw new IllegalArgumentException("Phiếu giảm giá công khai không hợp lệ.");
                }
            }

            // Tính giá trị giảm giá
            giamGia = BigDecimal.valueOf(pgg.getSoTienGiamToiDa());

            // Giảm số lượt sử dụng
            if (pgg.getSoLuongDung() != null && pgg.getSoLuongDung() > 0) {
                pgg.setSoLuongDung(pgg.getSoLuongDung() - 1);
                phieuGiamGiaRepository.save(pgg);
            }
        }

        // Xử lý khách hàng
        if (hoaDonRequest.getIdKhachHang() == null || hoaDonRequest.getIdKhachHang() <= 0) {
            KhachHang defaultCustomer = khachHangRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Khách hàng mặc định với ID 1 không tồn tại"));
            hoaDon.setIdKhachHang(defaultCustomer);
            hoaDon.setTenKhachHang("Khách vãng lai");
        } else {
            KhachHang khachHang = khachHangRepository.findById(hoaDonRequest.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + hoaDonRequest.getIdKhachHang()));
            hoaDon.setIdKhachHang(khachHang);
            hoaDon.setTenKhachHang(hoaDonRequest.getTenKhachHang());
            hoaDon.setSoDienThoaiKhachHang(hoaDonRequest.getSoDienThoaiKhachHang());
            if (hoaDonRequest.getDiaChiKhachHang() != null) {
                hoaDon.setDiaChiKhachHang(
                        hoaDonRequest.getDiaChiKhachHang().getDiaChiCuThe() + ", " +
                                hoaDonRequest.getDiaChiKhachHang().getPhuong() + ", " +
                                hoaDonRequest.getDiaChiKhachHang().getQuan() + ", " +
                                hoaDonRequest.getDiaChiKhachHang().getThanhPho());
            }
        }

        // Lưu chi tiết hóa đơn và thêm IMEI vào ImelDaBan
        int index = 1;
        List<HoaDonChiTiet> chiTietList = new ArrayList<>();
        List<ImelDaBan> imelDaBanList = new ArrayList<>();
        for (ChiTietGioHangDTO item : gioHangDTO.getChiTietGioHangDTOS()) {
            Imel imelEntity = imelRepository.findByImelAndDeleted(item.getMaImel(), true)
                    .orElseThrow(() -> new RuntimeException("IMEI " + item.getMaImel() + " không hợp lệ để thanh toán!"));
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại!"));

            // Tạo chi tiết hóa đơn
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setIdChiTietSanPham(chiTietSanPham);
            chiTiet.setGia(item.getGiaBan()); // Sử dụng giá từ giỏ hàng (đã cập nhật)
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
        if (hoaDonRequest.getHinhThucThanhToan() == null || hoaDonRequest.getHinhThucThanhToan().isEmpty()) {
            throw new RuntimeException("Thông tin thanh toán không được cung cấp!");
        }

        BigDecimal tongThanhToan = BigDecimal.ZERO;
        for (HinhThucThanhToanDTO dto : hoaDonRequest.getHinhThucThanhToan()) {
            if (dto.getPhuongThucThanhToanId() == null) {
                throw new RuntimeException("ID phương thức thanh toán không được null!");
            }
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

        // Cập nhật hóa đơn
        hoaDon.setTienSanPham(tienSanPham);
        hoaDon.setPhiVanChuyen(phiVanChuyen);
        hoaDon.setTongTien(tienSanPham.add(phiVanChuyen));
        hoaDon.setTongTienSauGiam(tienSanPham.add(phiVanChuyen).subtract(giamGia));
        String loaiDon = hoaDonRequest.getLoaiDon();
        if (!"online".equals(loaiDon) && !"trực tiếp".equals(loaiDon)) {
            throw new RuntimeException("Loại đơn không hợp lệ: " + loaiDon);
        }
        hoaDon.setLoaiDon(loaiDon);
        if (hoaDonRequest.getIdPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(hoaDonRequest.getIdPhieuGiamGia())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá với ID: " + hoaDonRequest.getIdPhieuGiamGia()));
            hoaDon.setIdPhieuGiamGia(phieuGiamGia);
        }
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

        // Xóa giỏ hàng
        xoaGioHang(idHD);
        gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);

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
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findByIdSanPhamIdAndAttributes(
                sanPhamId,
                mauSac,
                dungLuongRam,
                dungLuongBoNhoTrong
        ).orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm!"));
        return chiTietSanPham.getId();
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