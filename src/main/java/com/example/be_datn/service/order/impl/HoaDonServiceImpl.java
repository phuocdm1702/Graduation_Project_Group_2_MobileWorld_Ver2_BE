package com.example.be_datn.service.order.impl;

import com.example.be_datn.common.order.HoaDonDetailMapper;
import com.example.be_datn.common.order.HoaDonMapper;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.order.LichSuHoaDon;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.entity.product.ImelDaBan;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Override
    @Cacheable(value = "hoaDonPage", key = "#loaiDon + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
        return hoaDonRepository.getHoaDon("Tại quầy", pageable);
    }

    @Override
    @Cacheable(value = "hoaDonFiltered", key = "#keyword + '-' + #minAmount + '-' + #maxAmount + '-' + #startDate + '-' + #endDate + '-' + #trangThai + '-' + (#loaiDon != null ? #loaiDon : '') + '-' + #pageable")
    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword,
                                                    Long minAmount,
                                                    Long maxAmount,
                                                    Timestamp startDate,
                                                    Timestamp endDate,
                                                    Short trangThai,
                                                    String loaiDon,
                                                    Pageable pageable) {
        return hoaDonRepository.getHoaDonAndFilters(keyword,
                minAmount,
                maxAmount,
                startDate,
                endDate,
                trangThai,
                false,
                loaiDon,
                pageable);
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
    public HoaDonResponse updateHoaDonStatus(Integer id, Short trangThai, Integer idNhanVien) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        if (!"online".equalsIgnoreCase(hoaDon.getLoaiDon())) {
            throw new RuntimeException("Chỉ có thể cập nhật trạng thái cho hóa đơn online");
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
    public HoaDonResponse confirmAndAssignIMEI(Integer idHD, Map<Integer, String> imelMap) {
        // Kiểm tra imelMap
        if (imelMap == null || imelMap.isEmpty()) {
            throw new IllegalArgumentException("imelMap cannot be null or empty");
        }

        // Tìm hóa đơn
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(idHD)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Hóa đơn không tồn tại hoặc đã bị xóa: idHD=" + idHD);
                });

        // Lấy danh sách HoaDonChiTiet hiện tại
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDonIdAndDeletedFalse(idHD);
        if (hoaDonChiTiets.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy chi tiết hóa đơn cho idHD: " + idHD);
        }

        for (Map.Entry<Integer, String> entry : imelMap.entrySet()) {
            Integer chiTietSanPhamId = entry.getKey();
            String imei = entry.getValue();

            // Kiểm tra imei hợp lệ
            if (imei == null || imei.trim().isEmpty()) {
                throw new IllegalArgumentException("IMEI không hợp lệ cho chiTietSanPhamId: " + chiTietSanPhamId);
            }

            // Tìm Imel
            Imel imel = imelRepository.findByImel(imei)
                    .orElseThrow(() -> {
                        return new IllegalArgumentException("Không tìm thấy IMEI: " + imei);
                    });

            // Tìm ChiTietSanPham dựa trên idImel
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findByIdImelId(imel.getId())
                    .orElseThrow(() -> {
                        return new IllegalArgumentException("Không tìm thấy ChiTietSanPham cho idImel: " + imel.getId());
                    });

            // Tìm HoaDonChiTiet hiện tại
            HoaDonChiTiet hoaDonChiTiet = hoaDonChiTiets.stream()
                    .filter(hdct -> hdct.getIdChiTietSanPham().getId().equals(chiTietSanPhamId))
                    .findFirst()
                    .orElseThrow(() -> {
                        return new IllegalArgumentException("Không tìm thấy chi tiết hóa đơn cho chiTietSanPhamId: " + chiTietSanPhamId);
                    });

            // Kiểm tra xem IMEI đã được bán chưa
            Optional<ImelDaBan> existingImelDaBan = imelDaBanRepository.findByImel(imei);
            if (existingImelDaBan.isPresent()) {
                throw new IllegalArgumentException("IMEI " + imei + " đã được bán trước đó");
            }

            // Tạo và lưu ImelDaBan mới
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

            // Cập nhật HoaDonChiTiet
            hoaDonChiTiet.setIdChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setIdImelDaBan(imelDaBan);
            hoaDonChiTiet.setGia(chiTietSanPham.getGiaBan());
            try {
                hoaDonChiTietRepository.save(hoaDonChiTiet);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi lưu HoaDonChiTiet: " + e.getMessage());
            }

            // Cập nhật lịch sử hóa đơn
            LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                    .ma("LSHD_" + System.currentTimeMillis())
                    .hanhDong("Thay thế sản phẩm với IMEI: " + imei)
                    .thoiGian(Instant.now())
                    .hoaDon(hoaDon)
                    .build();
            try {
                lichSuHoaDonRepository.save(lichSuHoaDon);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi lưu LichSuHoaDon: " + e.getMessage());
            }
        }

        try {
            hoaDonRepository.save(hoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu HoaDon: " + e.getMessage());
        }

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public HoaDonResponse updateHoaDonKH(Integer id, String tenKH, String sdt, String diaChi, String email) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id).orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));
        hoaDon.setTenKhachHang(tenKH);
        hoaDon.setSoDienThoaiKhachHang(sdt);
        hoaDon.setDiaChiKhachHang(diaChi);
        hoaDon.setEmail(email);
        //thêm thông tin vào lịch sử hóa đơn
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setMa("LSHD_" + System.currentTimeMillis());
        lichSuHoaDon.setHanhDong("Cập nhật thông tin khách hàng: " + tenKH);
        lichSuHoaDon.setThoiGian(Instant.now());
        lichSuHoaDon.setHoaDon(hoaDon);

        lichSuHoaDonRepository.save(lichSuHoaDon);
        hoaDonRepository.save(hoaDon);

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public HoaDonResponse updateHoaDon(Integer id, String maHD, String loaHD) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id).orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));
        hoaDon.setMa(maHD);
        hoaDon.setLoaiDon(loaHD);
        //thêm thông tin vào lịch sử hóa đơn
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setMa("LSHD_" + System.currentTimeMillis());
        lichSuHoaDon.setHanhDong("Cập nhật thông tin Hóa Đơn: " + maHD);
        lichSuHoaDon.setThoiGian(Instant.now());
        lichSuHoaDon.setHoaDon(hoaDon);

        lichSuHoaDonRepository.save(lichSuHoaDon);
        hoaDonRepository.save(hoaDon);

        return hoaDonMapper.mapToDto(hoaDon);
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
}