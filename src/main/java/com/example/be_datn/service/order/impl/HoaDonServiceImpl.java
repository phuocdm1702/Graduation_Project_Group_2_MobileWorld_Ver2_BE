package com.example.be_datn.service.order.impl;

import com.example.be_datn.common.order.HoaDonDetailMapper;
import com.example.be_datn.common.order.HoaDonMapper;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.dto.order.response.HoaDonChiTietImeiResponse;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.order.LichSuHoaDon;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.entity.product.ImelDaBan;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
import com.example.be_datn.repository.discount.PhieuGiamGiaRepository;
import com.example.be_datn.repository.order.HoaDonChiTietRepository;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.repository.order.LichSuHoaDonRepository;
import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.repository.product.ImelDaBanRepository;
import com.example.be_datn.repository.product.ImelRepository;
import com.example.be_datn.service.order.HoaDonService;
import com.example.be_datn.service.order.XuatDanhSachHoaDon;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonMapper hoaDonMapper;

    @Autowired
    private HoaDonDetailMapper hoaDonDetailMapper;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private ImelRepository imelRepository;

    @Autowired
    private ImelDaBanRepository imelDaBanRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    @Cacheable(value = "hoaDonPage", key = "#loaiDon + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
        return hoaDonRepository.getHoaDon("Tại quầy", pageable);
    }

//    @Override
//    @Cacheable(value = "hoaDonFiltered", key = "#keyword + '-' + #minAmount + '-' + #maxAmount + '-' + #startDate + '-' + #endDate + '-' + #trangThai + '-' + (#loaiDon != null ? #loaiDon : '') + '-' + #pageable")
//    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword,
//                                                    Long minAmount,
//                                                    Long maxAmount,
//                                                    Timestamp startDate,
//                                                    Timestamp endDate,
//                                                    Short trangThai,
//                                                    String loaiDon,
//                                                    Pageable pageable) {
//        return hoaDonRepository.getHoaDonAndFilters(keyword,
//                minAmount,
//                maxAmount,
//                startDate,
//                endDate,
//                trangThai,
//                false,
//                loaiDon,
//                pageable);
//    }

    @Override
    @Cacheable(value = "hoaDonFiltered", key = "#keyword + '-' + #minAmount + '-' + #maxAmount + '-' + #startDate + '-' + #endDate + '-' + #trangThai + '-' + (#loaiDon != null ? #loaiDon : '') + '-' + #pageable")
    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword,
                                                    Long minAmount,
                                                    Long maxAmount,
                                                    Timestamp startDate,
                                                    Timestamp endDate,
                                                    Short trangThai,
                                                    String loaiDon,
                                                    Pageable pageable) {  // Truyền Pageable đầy đủ (với sort)
        return hoaDonRepository.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai, false, loaiDon, pageable);
    }



    @Override
    public Page<HoaDonResponse> getHoaDonOfCustomerAndFilters(Integer idKhachHang, Timestamp startDate, Timestamp endDate, Short trangThai, Boolean deleted, Pageable pageable) {
        Page<HoaDonResponse> hoaDonPage = hoaDonRepository.getHoaDonOfCustomerAndFilters(idKhachHang, startDate, endDate, trangThai, deleted, pageable);
        hoaDonPage.getContent().forEach(hoaDon -> System.out.println("Order ID: " + hoaDon.getId() + ", Status: " + hoaDon.getTrangThai())); // Add this line
        return hoaDonPage;
    }

    @Override
    public HoaDonDetailResponse getHoaDonDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDon.getChiTietHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToSanPhamChiTietInfo)
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.ThanhToanInfo> thanhToanInfos = hoaDon.getHinhThucThanhToan()
                .stream()
                .map(hoaDonDetailMapper::mapToThanhToanInfo)
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuHoaDonInfos = hoaDon.getLichSuHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToLichSuHoaDonInfo)
                .collect(Collectors.toList());

        return new HoaDonDetailResponse.Builder()
                .withHoaDonInfo(hoaDon, hoaDon.getIdPhieuGiamGia())
                .withNhanVienInfo(hoaDon.getIdNhanVien())
                .withThanhToanInfos(thanhToanInfos)
                .withSanPhamChiTietInfos(sanPhamChiTietInfos)
                .withLichSuHoaDonInfos(lichSuHoaDonInfos)
                .build();
    }

    @Override
    public HoaDonResponse getHoaDonByMa(String maHD) {
        HoaDonResponse hoaDon = hoaDonRepository.findByMa(maHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHD));
        return hoaDon;
    }

    @Override
    public void exportHoaDonToExcel(HttpServletResponse response) throws IOException {
        List<HoaDon> hoaDonEntities = hoaDonRepository.findAll();
        if (hoaDonEntities.isEmpty()) {
            throw new RuntimeException("Không có hóa đơn nào để xuất.");
        }

        List<HoaDonResponse> hoaDonList = hoaDonEntities.stream()
                .map(hoaDonMapper::mapToDto)
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.SanPhamChiTietInfo> chiTietList = hoaDonEntities.stream()
                .flatMap(hoaDon -> hoaDon.getChiTietHoaDon().stream()
                        .map(hoaDonDetailMapper::mapToSanPhamChiTietInfo))
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuList = hoaDonEntities.stream()
                .flatMap(hoaDon -> hoaDon.getLichSuHoaDon().stream()
                        .map(hoaDonDetailMapper::mapToLichSuHoaDonInfo))
                .collect(Collectors.toList());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("DanhSachHoaDon");
            XuatDanhSachHoaDon exporter = new XuatDanhSachHoaDon(workbook, sheet, hoaDonList, chiTietList, lichSuList);
            exporter.export(response);
        }
    }

    @Override
    public synchronized HoaDonResponse updateHoaDonStatus(Integer id, Short trangThai, Integer idNhanVien) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        // Idempotency Check: Only process orders that are in "Pending Confirmation" state (0).
        // This prevents processing the same payment notification multiple times (e.g., from redirect and IPN).
        if (hoaDon.getTrangThai() != 0) {
            System.out.println("Order " + id + " has already been processed. Current status: " + hoaDon.getTrangThai() + ". Ignoring update request.");
            return hoaDonMapper.mapToDto(hoaDon); // Return current state without changes
        }

        if (!isValidTrangThai(trangThai)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        hoaDon.setTrangThai(trangThai);

        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setMa("LSHD_" + System.currentTimeMillis());
        lichSuHoaDon.setHanhDong("Cập nhật trạng thái: " + mapStatusToString(trangThai));
        lichSuHoaDon.setThoiGian(Instant.now());
        lichSuHoaDon.setIdNhanVien(idNhanVien != null ?
                nhanVienRepository.findById(idNhanVien)
                        .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"))
                : null);
        lichSuHoaDon.setHoaDon(hoaDon);

        lichSuHoaDonRepository.save(lichSuHoaDon);
        hoaDonRepository.save(hoaDon);

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public Page<Imel> getAllImelBySanPhamId(Pageable pageable, Boolean deleted, Integer idSanPham, Integer chiTietSanPhamId) {
        return hoaDonChiTietRepository.getAllImelBySanPhamId(pageable, deleted, idSanPham, chiTietSanPhamId);
    }

    @Override
    @Transactional  // THÊM ANNOTATION NÀY
    public HoaDonResponse confirmAndAssignIMEI(Integer idHD, Map<Integer, String> imelMap) {
        if (imelMap == null || imelMap.isEmpty()) {
            throw new IllegalArgumentException("imelMap cannot be null or empty");
        }

        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại hoặc đã bị xóa: idHD=" + idHD));

        if (!"online".equalsIgnoreCase(hoaDon.getLoaiDon())) {
            throw new IllegalArgumentException("Chỉ có thể gán IMEI cho hóa đơn online");
        }
        if (hoaDon.getTrangThai() != 0) {
            throw new IllegalArgumentException("Chỉ có thể gán IMEI cho hóa đơn ở trạng thái chờ xác nhận (trangThai = 0)");
        }

        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonIdAndDeletedFalse(idHD);
        if (chiTietList.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy chi tiết hóa đơn cho idHD: " + idHD);
        }

        // SỬA LOGIC XỬ LÝ KEY - ĐƠN GIẢN HÓA
        for (Map.Entry<Integer, String> entry : imelMap.entrySet()) {
            Integer chiTietSanPhamId = entry.getKey();  // Sử dụng trực tiếp chiTietSanPhamId
            String imel = entry.getValue();

            // Tìm trực tiếp HoaDonChiTiet theo chiTietSanPhamId
            HoaDonChiTiet chiTiet = chiTietList.stream()
                    .filter(ct -> ct.getIdChiTietSanPham().getId().equals(chiTietSanPhamId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Không tìm thấy chi tiết hóa đơn cho chiTietSanPhamId: " + chiTietSanPhamId));

            ChiTietSanPham chiTietSanPham = chiTiet.getIdChiTietSanPham();

            // Xử lý IMEI cũ (nếu có)
            ImelDaBan oldImelDaBan = chiTiet.getIdImelDaBan();
            if (oldImelDaBan != null) {
                chiTiet.setIdImelDaBan(null);
                hoaDonChiTietRepository.save(chiTiet);

                Imel oldImel = imelRepository.findByImelAndDeleted(oldImelDaBan.getImel(), true).orElse(null);
                if (oldImel != null) {
                    oldImel.setDeleted(false);
                    imelRepository.save(oldImel);
                }

                ChiTietSanPham oldChiTietSanPham = chiTiet.getIdChiTietSanPham();
                if (oldChiTietSanPham != null) {
                    oldChiTietSanPham.setDeleted(false);
                    chiTietSanPhamRepository.save(oldChiTietSanPham);
                }

                imelDaBanRepository.delete(oldImelDaBan);
            }

            // Kiểm tra IMEI mới
            Imel newImelEntity = imelRepository.findByImelAndDeleted(imel, false)
                    .orElseThrow(() -> new IllegalArgumentException("IMEI " + imel + " không tồn tại hoặc đã được sử dụng!"));

            Optional<ChiTietSanPham> newChiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(imel);
            if (newChiTietSanPhamOpt.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy chi tiết sản phẩm cho IMEI " + imel);
            }
            ChiTietSanPham newChiTietSanPham = newChiTietSanPhamOpt.get();

            // Kiểm tra khớp thông số sản phẩm
            if (!newChiTietSanPham.getIdSanPham().getId().equals(chiTietSanPham.getIdSanPham().getId()) ||
                    !newChiTietSanPham.getIdRam().getId().equals(chiTietSanPham.getIdRam().getId()) ||
                    !newChiTietSanPham.getIdBoNhoTrong().getId().equals(chiTietSanPham.getIdBoNhoTrong().getId()) ||
                    !newChiTietSanPham.getIdMauSac().getId().equals(chiTietSanPham.getIdMauSac().getId())) {
                throw new IllegalArgumentException("IMEI " + imel + " không khớp với thông số sản phẩm!");
            }

            // Cập nhật trạng thái IMEI mới
            newImelEntity.setDeleted(true);
            imelRepository.save(newImelEntity);

            // Tạo và lưu ImelDaBan mới
            ImelDaBan imelDaBan = ImelDaBan.builder()
                    .imel(imel)
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
            chiTiet.setGia(newChiTietSanPham.getGiaBan());
            chiTiet.setTrangThai((short) 2);
            hoaDonChiTietRepository.save(chiTiet);
        }

        // Cập nhật tổng tiền hóa đơn
        BigDecimal tongTienSanPham = chiTietList.stream()
                .filter(hdct -> !hdct.getDeleted())
                .map(HoaDonChiTiet::getGia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        hoaDon.setTienSanPham(tongTienSanPham);

        // Cập nhật tổng tiền sau giảm
        BigDecimal giamGia = BigDecimal.ZERO;
        if (hoaDon.getIdPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = hoaDon.getIdPhieuGiamGia();
            if (phieuGiamGia.getPhanTramGiamGia() != null) {
                BigDecimal phanTram = BigDecimal.valueOf(phieuGiamGia.getPhanTramGiamGia() / 100.0);
                giamGia = tongTienSanPham.multiply(phanTram);
                if (phieuGiamGia.getSoTienGiamToiDa() != null) {
                    BigDecimal toiDa = BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa());
                    if (giamGia.compareTo(toiDa) > 0) {
                        giamGia = toiDa;
                    }
                }
            }
        }
        hoaDon.setTongTienSauGiam(tongTienSanPham.subtract(giamGia));
        hoaDon.setUpdatedAt(new Date());

        hoaDonRepository.save(hoaDon);

        // Ghi lịch sử hóa đơn
        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .ma("LSHD_" + UUID.randomUUID().toString().substring(0, 8))
                .hanhDong("Xác nhận và gán IMEI cho hóa đơn " + hoaDon.getMa())
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .idNhanVien(nhanVienRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại")))
                .deleted(false)
                .build();
        lichSuHoaDonRepository.save(lichSu);

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public HoaDonResponse updateHoaDonKH(Integer id, String tenKH, String sdt, String diaChi, String email) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));
        hoaDon.setTenKhachHang(tenKH);
        hoaDon.setSoDienThoaiKhachHang(sdt);
        hoaDon.setDiaChiKhachHang(diaChi);
        hoaDon.setEmail(email);

        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Cập nhật thông tin khách hàng: " + tenKH)
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .idNhanVien(nhanVienRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại")))
                .build();

        lichSuHoaDonRepository.save(lichSuHoaDon);
        hoaDonRepository.save(hoaDon);

        // Tạo response
        HoaDonResponse response = hoaDonMapper.mapToDto(hoaDon);
        if (response == null) {
            throw new RuntimeException("Lỗi khi ánh xạ hóa đơn sang DTO");
        }

        return response;
    }

    @Override
    public HoaDonResponse updateHoaDon(Integer id, String maHD, String loaiDon) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));
        hoaDon.setMa(maHD);
        hoaDon.setLoaiDon(loaiDon);

        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Cập nhật thông tin hóa đơn: " + maHD)
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .idNhanVien(nhanVienRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại")))
                .build();

        lichSuHoaDonRepository.save(lichSuHoaDon);
        hoaDonRepository.save(hoaDon);

        // Tạo response
        HoaDonResponse response = hoaDonMapper.mapToDto(hoaDon);
        if (response == null) {
            throw new RuntimeException("Lỗi khi ánh xạ hóa đơn sang DTO");
        }

        return response;
    }

    private boolean isValidTrangThai(Short trangThai) {
        return trangThai >= 0 && trangThai <= 4;
    }

    private String mapStatusToString(Short trangThai) {
        switch (trangThai) {
            case 0: return "Chờ xác nhận";
            case 1: return "Chờ giao hàng";
            case 2: return "Đang giao";
            case 3: return "Hoàn thành";
            case 4: return "Đã hủy";
            default: return "N/A";
        }
    }

    public HoaDon updatePhieuGiamGia(Integer hoaDonId, Integer idPhieuGiamGia) {
        if (hoaDonId == null) {
            throw new IllegalArgumentException("ID hóa đơn không được để trống");
        }

        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn với ID: " + hoaDonId));

        if (idPhieuGiamGia != null) {
            PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(idPhieuGiamGia)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá với ID: " + idPhieuGiamGia));

            if (!phieuGiamGia.getTrangThai() || phieuGiamGia.getSoLuongDung() <= 0) {
                throw new IllegalArgumentException("Phiếu giảm giá không hợp lệ hoặc đã hết số lượng sử dụng");
            }

            // Gán phiếu giảm giá cho hóa đơn
            hoaDon.setIdPhieuGiamGia(phieuGiamGia);

            // Tính tiền giảm
            BigDecimal giamGia = BigDecimal.ZERO;
            if (phieuGiamGia.getPhanTramGiamGia() != null) {
                BigDecimal phanTram = BigDecimal.valueOf(phieuGiamGia.getPhanTramGiamGia() / 100.0);
                giamGia = hoaDon.getTienSanPham().multiply(phanTram);

                // Giới hạn theo số tiền giảm tối đa
                if (phieuGiamGia.getSoTienGiamToiDa() != null) {
                    BigDecimal toiDa = BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa());
                    if (giamGia.compareTo(toiDa) > 0) {
                        giamGia = toiDa;
                    }
                }
            }

            // Cập nhật tổng tiền sau giảm
            hoaDon.setTongTienSauGiam(hoaDon.getTienSanPham().subtract(giamGia));
            sendVoucherUsedInOrder(hoaDonId, phieuGiamGia, "VOUCHER_USED");

        } else {
            // Không có phiếu giảm giá
            hoaDon.setIdPhieuGiamGia(null);
            hoaDon.setTongTienSauGiam(hoaDon.getTienSanPham());
        }

        hoaDon.setUpdatedAt(new Date());
        return hoaDonRepository.save(hoaDon);
    }

    private void sendVoucherUsedInOrder(Integer hoaDonId, PhieuGiamGia phieuGiamGia, String action) {
        try {
            Map<String, Object> voucherUpdate = new HashMap<>();
            voucherUpdate.put("action", action);
            voucherUpdate.put("hoaDonId", hoaDonId); // Thêm ID hóa đơn để biết phiếu được dùng cho đơn nào
            voucherUpdate.put("phieuGiamGiaId", phieuGiamGia.getId());
            voucherUpdate.put("maPhieu", phieuGiamGia.getMa());
            voucherUpdate.put("tenPhieu", phieuGiamGia.getTenPhieuGiamGia()); // Thêm tên phiếu nếu có
            voucherUpdate.put("giaTriGiam", phieuGiamGia.getSoTienGiamToiDa());
            voucherUpdate.put("phanTramGiamGia", phieuGiamGia.getPhanTramGiamGia());// Thêm giá trị giảm
            voucherUpdate.put("soLuongDung", phieuGiamGia.getSoLuongDung());
            voucherUpdate.put("trangThai", phieuGiamGia.getTrangThai());
            voucherUpdate.put("timestamp", Instant.now());
            messagingTemplate.convertAndSend("/topic/voucher-order-update", voucherUpdate);
            System.out.println("Đã gửi cập nhật phiếu giảm giá cho hóa đơn " + hoaDonId + " qua WebSocket: " + phieuGiamGia.getMa() + " - giá trị giảm: " + phieuGiamGia.getSoTienGiamToiDa());
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi cập nhật phiếu giảm giá cho hóa đơn qua WebSocket: " + e.getMessage());
        }
    }

    @Override
    public HoaDonResponse getHoaDonByMaForLookup(String maHD) {
        return hoaDonRepository.findByMaForLookup(maHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
    }

    @Override
    @Transactional
    public HoaDonResponse cancelOrder(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + orderId));

        // Check if order can be cancelled
        if (hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 1) { // Assuming 0 is "Chờ xác nhận"
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xác nhận' hoặc 'Chờ giao hàng'.");
        }

        // Update order status
        hoaDon.setTrangThai((short) 4); // 4 is "Đã hủy"
        hoaDon.setUpdatedAt(new Date());
        // You might want to set who updated it if you have user management
        // hoaDon.setUpdatedBy(userId);

        // Restore Imel status
        for (HoaDonChiTiet chiTiet : hoaDon.getChiTietHoaDon()) {
            if (chiTiet.getIdImelDaBan() != null) {
                String imelString = chiTiet.getIdImelDaBan().getImel();
                Imel imel = imelRepository.findByImelAndDeleted(imelString, true)
                        .orElse(null); // Find Imel by the string and deleted = true
                if (imel != null) {
                    imel.setDeleted(false);
                    imelRepository.save(imel);
                }
            }
        }

        hoaDonRepository.save(hoaDon);

        // Add to history
        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Hủy đơn hàng")
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .idNhanVien(hoaDon.getIdNhanVien()) // Assuming the original staff member is responsible
                .build();
        lichSuHoaDonRepository.save(lichSuHoaDon);

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse cancelOrderClient(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + orderId));

        // Check if order can be cancelled
        if (hoaDon.getTrangThai() != 0) { // Assuming 0 is "Chờ xác nhận"
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xác nhận'.");
        }

        // Update order status
        hoaDon.setTrangThai((short) 4); // 4 is "Đã hủy"
        hoaDon.setUpdatedAt(new Date());

        // Restore Imel status
        for (HoaDonChiTiet chiTiet : hoaDon.getChiTietHoaDon()) {
            if (chiTiet.getIdImelDaBan() != null) {
                String imelString = chiTiet.getIdImelDaBan().getImel();
                Imel imel = imelRepository.findByImelAndDeleted(imelString, true)
                        .orElse(null); // Find Imel by the string and deleted = true
                if (imel != null) {
                    imel.setDeleted(false);
                    imelRepository.save(imel);
                }
            }
        }

        hoaDonRepository.save(hoaDon);

        // Add to history
        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Hủy đơn hàng")
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .build();
        lichSuHoaDonRepository.save(lichSuHoaDon);

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse addProductToHoaDonChiTiet(Integer idHD, Integer chiTietSanPhamId, String imei) {
        // Kiểm tra hóa đơn
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(idHD)
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại hoặc đã bị xóa: idHD=" + idHD));

        // Kiểm tra loại hóa đơn và trạng thái
        if (!"online".equalsIgnoreCase(hoaDon.getLoaiDon())) {
            throw new IllegalArgumentException("Chỉ có thể thêm sản phẩm vào hóa đơn online");
        }
        if (hoaDon.getTrangThai() != 0) {
            throw new IllegalArgumentException("Chỉ có thể thêm sản phẩm vào hóa đơn ở trạng thái chờ xác nhận (trạng thái = 0)");
        }

        // Kiểm tra IMEI hợp lệ
        if (imei == null || imei.trim().isEmpty()) {
            throw new IllegalArgumentException("IMEI không hợp lệ");
        }

        // Tìm Imel
        Imel imel = imelRepository.findByImel(imei)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy IMEI: " + imei));

        // Kiểm tra xem IMEI đã được bán chưa
        Optional<ImelDaBan> existingImelDaBan = imelDaBanRepository.findByImel(imei);
        if (existingImelDaBan.isPresent()) {
            throw new IllegalArgumentException("IMEI " + imei + " đã được bán trước đó");
        }

        // Tìm ChiTietSanPham
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietSanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ChiTietSanPham với ID: " + chiTietSanPhamId));

        // Kiểm tra xem ChiTietSanPham có khớp với Imel
        if (!chiTietSanPham.getIdImel().getId().equals(imel.getId())) {
            throw new IllegalArgumentException("IMEI " + imei + " không khớp với ChiTietSanPham ID: " + chiTietSanPhamId);
        }

        // Đánh dấu ChiTietSanPham và Imel là đã bán
        chiTietSanPham.setDeleted(true);
        imel.setDeleted(true);
        try {
            chiTietSanPhamRepository.save(chiTietSanPham);
            imelRepository.save(imel);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái ChiTietSanPham hoặc Imel: " + e.getMessage());
        }

        // Tạo và lưu ImelDaBan
        ImelDaBan imelDaBan = ImelDaBan.builder()
                .imel(imei)
                .ngayBan(new Date())
                .ghiChu("Đã gán cho hóa đơn " + hoaDon.getMa())
                .deleted(false)
                .build();
        try {
            imelDaBanRepository.save(imelDaBan);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu ImelDaBan: " + e.getMessage());
        }

        // Tạo HoaDonChiTiet mới
        HoaDonChiTiet hoaDonChiTiet = HoaDonChiTiet.builder()
                .hoaDon(hoaDon)
                .idChiTietSanPham(chiTietSanPham)
                .idImelDaBan(imelDaBan)
                .ma("HDCT_" + System.currentTimeMillis())
                .gia(chiTietSanPham.getGiaBan())
                .trangThai((short) 1)
                .ghiChu("Thêm sản phẩm trực tiếp vào hóa đơn chi tiết")
                .deleted(false)
                .build();
        try {
            hoaDonChiTietRepository.save(hoaDonChiTiet);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu HoaDonChiTiet: " + e.getMessage());
        }

        // Cập nhật tổng tiền hóa đơn
        BigDecimal tongTienSanPham = hoaDon.getChiTietHoaDon().stream()
                .filter(hdct -> !hdct.getDeleted())
                .map(HoaDonChiTiet::getGia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        hoaDon.setTienSanPham(tongTienSanPham);

        // Cập nhật lại tổng tiền sau giảm (nếu có phiếu giảm giá)
        BigDecimal giamGia = BigDecimal.ZERO;
        if (hoaDon.getIdPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = hoaDon.getIdPhieuGiamGia();
            if (phieuGiamGia.getPhanTramGiamGia() != null) {
                BigDecimal phanTram = BigDecimal.valueOf(phieuGiamGia.getPhanTramGiamGia() / 100.0);
                giamGia = tongTienSanPham.multiply(phanTram);
                if (phieuGiamGia.getSoTienGiamToiDa() != null) {
                    BigDecimal toiDa = BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa());
                    if (giamGia.compareTo(toiDa) > 0) {
                        giamGia = toiDa;
                    }
                }
            }
        }
        hoaDon.setTongTienSauGiam(tongTienSanPham.subtract(giamGia));
        hoaDon.setUpdatedAt(new Date());

        // Lưu hóa đơn
        try {
            hoaDonRepository.save(hoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu HoaDon: " + e.getMessage());
        }

        // Ghi lịch sử hóa đơn
        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Thêm sản phẩm với IMEI: " + imei + " vào hóa đơn chi tiết")
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .build();
        try {
            lichSuHoaDonRepository.save(lichSuHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu LichSuHoaDon: " + e.getMessage());
        }

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse deleteProductFromHoaDonChiTiet(Integer idHD, Integer idHoaDonChiTiet) {
        // Kiểm tra hóa đơn
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(idHD)
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại hoặc đã bị xóa: idHD=" + idHD));

        // Kiểm tra loại hóa đơn và trạng thái
        if (!"online".equalsIgnoreCase(hoaDon.getLoaiDon())) {
            throw new IllegalArgumentException("Chỉ có thể xóa sản phẩm khỏi hóa đơn online");
        }
        if (hoaDon.getTrangThai() != 0) {
            throw new IllegalArgumentException("Chỉ có thể xóa sản phẩm khỏi hóa đơn ở trạng thái chờ xác nhận (trạng thái = 0)");
        }

        // Tìm HoaDonChiTiet
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository.findById(idHoaDonChiTiet)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi tiết hóa đơn với ID: " + idHoaDonChiTiet));

        // Kiểm tra xem HoaDonChiTiet thuộc hóa đơn đúng không
        if (!hoaDonChiTiet.getHoaDon().getId().equals(idHD)) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không thuộc hóa đơn ID: " + idHD);
        }

        // Đặt lại trạng thái deleted của Imel
        ImelDaBan imelDaBan = hoaDonChiTiet.getIdImelDaBan();
        if (imelDaBan != null) {
            String imei = imelDaBan.getImel();
            Imel imel = imelRepository.findByImel(imei)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Imel với IMEI: " + imei));
            imel.setDeleted(false);
            try {
                imelRepository.save(imel);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi cập nhật trạng thái Imel: " + e.getMessage());
            }

            // Xóa ImelDaBan
            try {
                imelDaBanRepository.delete(imelDaBan);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi xóa ImelDaBan: " + e.getMessage());
            }
        }

        // Đặt lại trạng thái deleted của ChiTietSanPham
        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getIdChiTietSanPham();
        if (chiTietSanPham != null) {
            chiTietSanPham.setDeleted(false);
            try {
                chiTietSanPhamRepository.save(chiTietSanPham);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi cập nhật trạng thái ChiTietSanPham: " + e.getMessage());
            }
        }

        // Xóa HoaDonChiTiet
        try {
            hoaDonChiTietRepository.delete(hoaDonChiTiet);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa HoaDonChiTiet: " + e.getMessage());
        }

        // Cập nhật tổng tiền hóa đơn
        BigDecimal tongTienSanPham = hoaDon.getChiTietHoaDon().stream()
                .filter(hdct -> !hdct.getDeleted())
                .map(HoaDonChiTiet::getGia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        hoaDon.setTienSanPham(tongTienSanPham);

        // Cập nhật lại tổng tiền sau giảm (nếu có phiếu giảm giá)
        BigDecimal giamGia = BigDecimal.ZERO;
        if (hoaDon.getIdPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = hoaDon.getIdPhieuGiamGia();
            if (phieuGiamGia.getPhanTramGiamGia() != null) {
                BigDecimal phanTram = BigDecimal.valueOf(phieuGiamGia.getPhanTramGiamGia() / 100.0);
                giamGia = tongTienSanPham.multiply(phanTram);
                if (phieuGiamGia.getSoTienGiamToiDa() != null) {
                    BigDecimal toiDa = BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa());
                    if (giamGia.compareTo(toiDa) > 0) {
                        giamGia = toiDa;
                    }
                }
            }
        }
        hoaDon.setTongTienSauGiam(tongTienSanPham.subtract(giamGia));
        hoaDon.setUpdatedAt(new Date());

        // Lưu hóa đơn
        try {
            hoaDonRepository.save(hoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu HoaDon: " + e.getMessage());
        }

        // Ghi lịch sử hóa đơn
        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Xóa sản phẩm với IMEI: " + (imelDaBan != null ? imelDaBan.getImel() : "N/A") + " khỏi hóa đơn chi tiết")
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .build();
        try {
            lichSuHoaDonRepository.save(lichSuHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu LichSuHoaDon: " + e.getMessage());
        }
        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public Page<HoaDonChiTietImeiResponse> getImeiByHoaDonId(Integer hoaDonId, Pageable pageable) {
        // Kiểm tra hóa đơn tồn tại
        hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + hoaDonId));

        // Lấy danh sách HoaDonChiTiet của hóa đơn
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonIdAndDeletedFalse(hoaDonId);

        // Chuyển đổi sang DTO
        List<HoaDonChiTietImeiResponse> responseList = chiTietList.stream()
                .map(this::mapToHoaDonChiTietImeiResponse)
                .collect(Collectors.toList());

        // Tạo Page từ List (simple pagination)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responseList.size());
        List<HoaDonChiTietImeiResponse> pageContent = responseList.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, responseList.size());
    }

    private HoaDonChiTietImeiResponse mapToHoaDonChiTietImeiResponse(HoaDonChiTiet chiTiet) {
        ChiTietSanPham ctsp = chiTiet.getIdChiTietSanPham();
        ImelDaBan imelDaBan = chiTiet.getIdImelDaBan();
        HoaDon hoaDon = chiTiet.getHoaDon();

        return HoaDonChiTietImeiResponse.builder()
                .id(chiTiet.getId())
                .ma(chiTiet.getMa())
                .gia(chiTiet.getGia())
                .trangThai(chiTiet.getTrangThai())
                .ghiChu(chiTiet.getGhiChu())

                // Thông tin sản phẩm
                .sanPhamId(ctsp != null ? ctsp.getIdSanPham().getId() : null)
                .tenSanPham(ctsp != null ? ctsp.getIdSanPham().getTenSanPham() : null)
                .anhSanPham(ctsp != null ? ctsp.getIdSanPham().getMa() : null)
                .thuongHieu(ctsp != null ? ctsp.getIdSanPham().getIdNhaSanXuat().getNhaSanXuat() : null)

                // Thông tin chi tiết sản phẩm
                .chiTietSanPhamId(ctsp != null ? ctsp.getId() : null)
                .ram(ctsp != null ? ctsp.getIdRam().getDungLuongRam() : null)
                .boNhoTrong(ctsp != null ? ctsp.getIdBoNhoTrong().getDungLuongBoNhoTrong() : null)
                .mauSac(ctsp != null ? ctsp.getIdMauSac().getMauSac() : null)
                .giaBan(ctsp != null ? ctsp.getGiaBan() : null)

                // Thông tin IMEI
                .imei(imelDaBan != null ? imelDaBan.getImel() : null)
                .ngayBan(imelDaBan != null ? imelDaBan.getNgayBan() : null)
                .ghiChuImei(imelDaBan != null ? imelDaBan.getGhiChu() : null)

                // Thông tin hóa đơn
                .hoaDonId(hoaDon.getId())
                .maHoaDon(hoaDon.getMa())
                .tenKhachHang(hoaDon.getTenKhachHang())
                .soDienThoaiKhachHang(hoaDon.getSoDienThoaiKhachHang())
                .build();
    }
}