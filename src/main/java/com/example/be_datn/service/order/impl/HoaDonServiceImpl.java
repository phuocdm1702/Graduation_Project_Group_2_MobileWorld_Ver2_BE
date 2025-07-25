package com.example.be_datn.service.order.impl;

import com.example.be_datn.common.order.HoaDonDetailMapper;
import com.example.be_datn.common.order.HoaDonMapper;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.order.LichSuHoaDon;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
import com.example.be_datn.repository.order.HoaDonChiTietRepository;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.repository.order.LichSuHoaDonRepository;
import com.example.be_datn.repository.pay.HinhThucThanhToanRepository;
import com.example.be_datn.service.order.HoaDonService;
import com.example.be_datn.service.order.XuatDanhSachHoaDon;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
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


    //    @Override
//    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
//        Page<HoaDon> hoaDonPage = hoaDonRepository.getHoaDon(pageable);
//        List<HoaDonResponse> filteredList = hoaDonPage.getContent()
//                .stream()
//                .filter(hoaDon -> "Tại quầy".equals(hoaDon.getLoaiDon()))
//                .map(hoaDonMapper::mapToDto)
//                .collect(Collectors.toList());
//        return new PageImpl<>(filteredList, pageable, hoaDonPage.getTotalElements());
//    }
//
//    @Override
//    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount, Timestamp startDate, Timestamp endDate, Short trangThai, Pageable pageable) {
//        return hoaDonRepository.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai, pageable)
//                .map(hoaDonMapper::mapToDto);
//    }

    @Override
    @Cacheable(value = "hoaDonPage", key = "#loaiDon + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
        return hoaDonRepository.getHoaDon("Tại quầy", pageable);
    }

    //    @Override
//    @Cacheable(value = "hoaDonFiltered", key = "#keyword + '-' + #minAmount + '-' + #maxAmount + '-' + #startDate + '-' + #endDate + '-' + #trangThai + '-' + #pageable")
//    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword,
//                                                    Long minAmount,
//                                                    Long maxAmount,
//                                                    Timestamp startDate,
//                                                    Timestamp endDate,
//                                                    Short trangThai,
//                                                    String loaiDon,
//                                                    Pageable pageable) {
//        Page<HoaDonResponse> result = hoaDonRepository.getHoaDonAndFilters(
//                keyword,
//                minAmount,
//                maxAmount,
//                startDate,
//                endDate,
//                trangThai,
//                false,
//                loaiDon,
//                pageable);
//        return result;
//    }

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

        // Ánh xạ chi tiết sản phẩm
        List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDon.getChiTietHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToSanPhamChiTietInfo)
                .collect(Collectors.toList());

        // Ánh xạ thông tin thanh toán
        List<HoaDonDetailResponse.ThanhToanInfo> thanhToanInfos = hoaDon.getHinhThucThanhToan()
                .stream()
                .map(hoaDonDetailMapper::mapToThanhToanInfo)
                .collect(Collectors.toList());

        // Ánh xạ lịch sử hóa đơn
        List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuHoaDonInfos = hoaDon.getLichSuHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToLichSuHoaDonInfo)
                .collect(Collectors.toList());

        // Sử dụng Builder để tạo response
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
        // Lấy danh sách hóa đơn từ repository
        List<HoaDon> hoaDonEntities = hoaDonRepository.findAll(); // Có thể thêm điều kiện lọc nếu cần
        if (hoaDonEntities.isEmpty()) {
            throw new RuntimeException("Không có hóa đơn nào để xuất.");
        }

        // Ánh xạ sang HoaDonResponse
        List<HoaDonResponse> hoaDonList = hoaDonEntities.stream()
                .map(hoaDonMapper::mapToDto)
                .collect(Collectors.toList());

        // Lấy chi tiết hóa đơn cho tất cả hóa đơn
        List<HoaDonDetailResponse.SanPhamChiTietInfo> chiTietList = hoaDonEntities.stream()
                .flatMap(hoaDon -> hoaDon.getChiTietHoaDon().stream()
                        .map(hoaDonDetailMapper::mapToSanPhamChiTietInfo))
                .collect(Collectors.toList());

        // Lấy lịch sử hóa đơn cho tất cả hóa đơn
        List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuList = hoaDonEntities.stream()
                .flatMap(hoaDon -> hoaDon.getLichSuHoaDon().stream()
                        .map(hoaDonDetailMapper::mapToLichSuHoaDonInfo))
                .collect(Collectors.toList());

        // Tạo workbook và sheet
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("DanhSachHoaDon");
            XuatDanhSachHoaDon exporter = new XuatDanhSachHoaDon(workbook, sheet, hoaDonList, chiTietList, lichSuList);
            exporter.export(response);
        }
    }

    @Override
    public HoaDonResponse updateHoaDonStatus(Integer id, Short trangThai, Integer idNhanVien) {
        // Tìm hóa đơn theo ID
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        // Kiểm tra xem hóa đơn có phải là đơn online không
        if (!"online".equalsIgnoreCase(hoaDon.getLoaiDon())) {
            throw new RuntimeException("Chỉ có thể cập nhật trạng thái cho hóa đơn online");
        }

        // Kiểm tra trạng thái hợp lệ
        if (!isValidTrangThai(trangThai)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        // Cập nhật trạng thái
        hoaDon.setTrangThai(trangThai);

        // Thêm bản ghi vào lịch sử hóa đơn
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setMa("LSHD_" + System.currentTimeMillis());
        lichSuHoaDon.setHanhDong("Cập nhật trạng thái: " + mapStatusToString(trangThai));
        lichSuHoaDon.setThoiGian(Instant.now());
        lichSuHoaDon.setIdNhanVien(idNhanVien != null ?
                nhanVienRepository.findById(idNhanVien)
                        .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"))
                : null);
        lichSuHoaDon.setHoaDon(hoaDon);

        // Lưu lịch sử hóa đơn
        lichSuHoaDonRepository.save(lichSuHoaDon);

        // Lưu hóa đơn đã cập nhật
        hoaDonRepository.save(hoaDon);

        // Trả về response
        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public Page<Imel> getAllImelSP(Pageable pageable, Boolean deleted) {
        return hoaDonChiTietRepository.getAllImelSP(pageable, false);
    }

    // Hàm kiểm tra trạng thái hợp lệ
    private boolean isValidTrangThai(Short trangThai) {
        return trangThai >= 0 && trangThai <= 4; // 0: Chờ xác nhận, 1: Chờ giao hàng, 2: Đang giao, 3: Hoàn thành, 4: Đã hủy
    }

    // Hàm ánh xạ trạng thái sang chuỗi
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
