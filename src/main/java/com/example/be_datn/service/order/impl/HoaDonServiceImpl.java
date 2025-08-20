package com.example.be_datn.service.order.impl;

import com.example.be_datn.common.order.HoaDonDetailMapper;
import com.example.be_datn.common.order.HoaDonMapper;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

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
    private SimpMessagingTemplate messagingTemplate;

    public HoaDonServiceImpl(PhieuGiamGiaRepository phieuGiamGiaRepository) {
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
    }

    @Override
    @Cacheable(value = "hoaDonPage", key = "#loaiDon + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
        return hoaDonRepository.getHoaDon("Tại quầy", pageable);
    }

    @Override
    @Cacheable(value = "hoaDonFiltered", key = "#keyword + '-' + #minAmount + '-' + #maxAmount + '-' + #startDate + '-' + #endDate + '-' + #trangThai + '-' + (#loaiDon != null ? #loaiDon : '') + '-' + #pageable")
    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount,
                                                    Timestamp startDate, Timestamp endDate, Short trangThai,
                                                    String loaiDon, Pageable pageable) {
        return hoaDonRepository.getHoaDonAndFilters(keyword, minAmount, maxAmount,
                startDate, endDate, trangThai, false, loaiDon, pageable);
    }

    @Override
    public Page<HoaDonResponse> getHoaDonOfCustomerAndFilters(Integer idKhachHang, Timestamp startDate, Timestamp endDate, Short trangThai, Pageable pageable) {
        return hoaDonRepository.getHoaDonOfCustomerAndFilters(idKhachHang, startDate, endDate, trangThai, pageable);
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
        return hoaDonRepository.findByMa(maHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
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
    public HoaDonResponse updateHoaDonStatus(Integer id, Short trangThai, Integer idNhanVien) {
        if (!isValidTrangThai(trangThai)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }

        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        hoaDon.setTrangThai(trangThai);
        hoaDon.setUpdatedAt(new Date());
        hoaDon.setUpdatedBy(idNhanVien);

        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Cập nhật trạng thái hóa đơn thành: " + mapStatusToString(trangThai))
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .idNhanVien(nhanVienRepository.findById(idNhanVien)
                        .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại")))
                .build();

        lichSuHoaDonRepository.save(lichSuHoaDon);
        hoaDonRepository.save(hoaDon);

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public Page<Imel> getAllImelBySanPhamId(Pageable pageable, Boolean deleted, Integer idSanPham, Integer chiTietSanPhamId) {
        return hoaDonChiTietRepository.getAllImelBySanPhamId(pageable, deleted, idSanPham, chiTietSanPhamId);
    }

    @Override
    public HoaDonResponse confirmAndAssignIMEI(Integer idHD, Map<Integer, String> imelMap) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        for (Map.Entry<Integer, String> entry : imelMap.entrySet()) {
            Integer chiTietSanPhamId = entry.getKey();
            String imei = entry.getValue();

            HoaDonChiTiet hoaDonChiTiet = hoaDon.getChiTietHoaDon().stream()
                    .filter(hdct -> hdct.getIdChiTietSanPham().getId().equals(chiTietSanPhamId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Chi tiết hóa đơn không tồn tại"));

            Imel imel = imelRepository.findByImel(imei)
                    .orElseThrow(() -> new RuntimeException("IMEI không tồn tại"));

            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietSanPhamId)
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));

            ImelDaBan oldImelDaBan = hoaDonChiTiet.getIdImelDaBan();
            if (oldImelDaBan != null) {
                ChiTietSanPham oldChiTietSanPham = hoaDonChiTiet.getIdChiTietSanPham();
                if (oldChiTietSanPham != null) {
                    oldChiTietSanPham.setDeleted(false);
                    chiTietSanPhamRepository.save(oldChiTietSanPham);
                }
                imelDaBanRepository.delete(oldImelDaBan);
            }

            Optional<ImelDaBan> existingImelDaBan = imelDaBanRepository.findByImel(imei);
            if (existingImelDaBan.isPresent()) {
                throw new IllegalArgumentException("IMEI " + imei + " đã được bán trước đó");
            }

            imel.setDeleted(true);
            imelRepository.save(imel);

            chiTietSanPham.setDeleted(true);
            chiTietSanPhamRepository.save(chiTietSanPham);

            ImelDaBan imelDaBan = ImelDaBan.builder()
                    .imel(imei)
                    .ngayBan(new Date())
                    .ghiChu("Đã gán cho hóa đơn " + hoaDon.getMa())
                    .deleted(false)
                    .build();
            imelDaBanRepository.save(imelDaBan);

            hoaDonChiTiet.setIdChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setIdImelDaBan(imelDaBan);
            hoaDonChiTiet.setGia(chiTietSanPham.getGiaBan());
            hoaDonChiTietRepository.save(hoaDonChiTiet);
        }

        hoaDon.setTrangThai((short) 1); // Chuyển sang "Chờ giao hàng"
        hoaDonRepository.save(hoaDon);

        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .ma("LSHD_" + System.currentTimeMillis())
                .hanhDong("Xác nhận và gán IMEI, chuyển trạng thái sang Chờ giao hàng")
                .thoiGian(Instant.now())
                .hoaDon(hoaDon)
                .idNhanVien(nhanVienRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Nhân viên mặc định không tồn tại")))
                .build();
        lichSuHoaDonRepository.save(lichSuHoaDon);

        // Tạo response
        HoaDonResponse response = hoaDonMapper.mapToDto(hoaDon);
        if (response == null) {
            throw new RuntimeException("Lỗi khi ánh xạ hóa đơn sang DTO");
        }

        return response;
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
            voucherUpdate.put("giaTriGiam", phieuGiamGia.getSoTienGiamToiDa()); // Thêm giá trị giảm
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
        if (hoaDon.getTrangThai() != 1) { // Assuming 1 is "Chờ xác nhận"
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xác nhận'.");
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
}