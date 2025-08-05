package com.example.be_datn.service.giao_ca.impl;

import com.example.be_datn.dto.giao_ca.GiaoCaDTO;
import com.example.be_datn.dto.giao_ca.HoaDonReportDTO;
import com.example.be_datn.entity.giao_ca.GiaoCa;
import com.example.be_datn.entity.giao_ca.GiaoCaChiTiet;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import com.example.be_datn.repository.giao_ca.GiaoCaRepository;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
import com.example.be_datn.repository.pay.HinhThucThanhToanRepository;
import com.example.be_datn.service.giao_ca.GiaoCaService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GiaoCaServiceImpl implements GiaoCaService {

    @Autowired
    private GiaoCaRepository giaoCaRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;

    // Trạng thái ca làm việc
    private static final Short SHIFT_STATUS_ACTIVE = 1; // Đang diễn ra
    private static final Short SHIFT_STATUS_ENDED = 2;  // Đã kết thúc

    // Trạng thái hóa đơn
    private static final Short ORDER_STATUS_PENDING = 0; // Chờ xử lý
    private static final Short ORDER_STATUS_COMPLETED = 3; // Đã hoàn thành
    private static final Short ORDER_STATUS_ONLINE = 1; // Đơn online (chờ xử lý)

    @Override
    @Transactional
    public GiaoCa startShift(Integer nhanVienId, BigDecimal tienMatBanDau) {
        Optional<GiaoCa> activeShift = giaoCaRepository.findByidNhanVien_IdAndTrangThai(nhanVienId, SHIFT_STATUS_ACTIVE);
        if (activeShift.isPresent()) {
            throw new IllegalStateException("Nhân viên đã có ca làm việc đang diễn ra.");
        }

        BigDecimal tienMatCaTruoc = BigDecimal.ZERO;
        Integer donHangChoXuLyCaTruoc = 0;
        Optional<GiaoCa> lastEndedShift = giaoCaRepository.findTopByTrangThaiOrderByIdDesc(SHIFT_STATUS_ENDED);
        if (lastEndedShift.isPresent()) {
            GiaoCa previousShift = lastEndedShift.get();
            tienMatCaTruoc = previousShift.getTienMatCuoiCa() != null ? previousShift.getTienMatCuoiCa() : BigDecimal.ZERO;
            donHangChoXuLyCaTruoc = previousShift.getDonHangChoXuLyCaTruoc();
        }

        long currentPendingOrders = hoaDonRepository.countByTrangThaiAndDeleted(ORDER_STATUS_PENDING, true);

        com.example.be_datn.entity.account.NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + nhanVienId));

        GiaoCa newShift = GiaoCa.builder()
                .idNhanVien(nhanVien)
                .thoiGianBatDau(LocalDateTime.now())
                .tienMatBanDau(tienMatBanDau)
                .tienMatCaTruoc(tienMatCaTruoc)
                .donHangChoXuLyCaTruoc((int) currentPendingOrders)
                .trangThai(SHIFT_STATUS_ACTIVE)
                .build();

        return giaoCaRepository.save(newShift);
    }

    @Override
    @Transactional
    public GiaoCaDTO endShift(Integer nhanVienId) {
        GiaoCa currentShift = giaoCaRepository.findByidNhanVien_IdAndTrangThai(nhanVienId, SHIFT_STATUS_ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Ca làm việc chưa được bắt đầu."));

        LocalDateTime endTime = LocalDateTime.now();

        List<HoaDon> completedOrdersInShift = hoaDonRepository.findByGiaoCa_IdAndDeletedFalseAndTrangThai(currentShift.getId(), ORDER_STATUS_COMPLETED);

        BigDecimal tongTienMatThuDuoc = completedOrdersInShift.stream()
                .flatMap(hoaDon -> hinhThucThanhToanRepository.findByHoaDonId(hoaDon.getId()).stream())
                .filter(httt -> httt.getTienMat() != null)
                .map(HinhThucThanhToan::getTienMat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTienChuyenKhoan = completedOrdersInShift.stream()
                .flatMap(hoaDon -> hinhThucThanhToanRepository.findByHoaDonId(hoaDon.getId()).stream())
                .filter(httt -> httt.getTienChuyenKhoan() != null)
                .map(HinhThucThanhToan::getTienChuyenKhoan)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tienMatCuoiCa = currentShift.getTienMatBanDau().add(tongTienMatThuDuoc);

        List<GiaoCaChiTiet> giaoCaChiTiets = completedOrdersInShift.stream().map(hoaDon -> {
            GiaoCaChiTiet chiTiet = new GiaoCaChiTiet();
            chiTiet.setGiaoCa(currentShift);
            chiTiet.setHoaDon(hoaDon);
            return chiTiet;
        }).collect(Collectors.toList());

        BigDecimal tongDoanhThu = tongTienMatThuDuoc.add(tongTienChuyenKhoan);

        currentShift.setThoiGianKetThuc(endTime);
        currentShift.setTienMatCuoiCa(tienMatCuoiCa);
        currentShift.setTongTienMat(tongTienMatThuDuoc);
        currentShift.setTongTienChuyenKhoan(tongTienChuyenKhoan);
        currentShift.setTongDoanhThu(tongDoanhThu);
        currentShift.setTrangThai(SHIFT_STATUS_ENDED);

        GiaoCa savedShift = giaoCaRepository.save(currentShift);

        List<HoaDonReportDTO> hoaDonReportDTOs = completedOrdersInShift.stream().map(hoaDon ->
                HoaDonReportDTO.builder()
                        .ma(hoaDon.getMa())
                        .tongTien(hoaDon.getTongTienSauGiam())
                        .trangThai(hoaDon.getTrangThai())
                        .build()
        ).collect(Collectors.toList());

        return GiaoCaDTO.builder()
                .id(savedShift.getId())
                .tenNhanVien(savedShift.getIdNhanVien().getTenNhanVien())
                .thoiGianBatDau(savedShift.getThoiGianBatDau())
                .thoiGianKetThuc(savedShift.getThoiGianKetThuc())
                .tienMatBanDau(savedShift.getTienMatBanDau())
                .tienMatCuoiCa(savedShift.getTienMatCuoiCa())
                .tongTienMat(savedShift.getTongTienMat())
                .tongTienChuyenKhoan(savedShift.getTongTienChuyenKhoan())
                .tongDoanhThu(savedShift.getTongDoanhThu())
                .hoaDons(hoaDonReportDTOs)
                .build();
    }

    @Override
    public Optional<GiaoCa> getActiveShift(Integer nhanVienId) {
        return giaoCaRepository.findByidNhanVien_IdAndTrangThai(nhanVienId, SHIFT_STATUS_ACTIVE);
    }

    @Override
    public ByteArrayInputStream generateExcelReport(Map<String, Object> reportData) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Biên Bản Giao Ca");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Mục", "Giá trị"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Map.Entry<String, Object> entry : reportData.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }

    @Override
    public long getPendingOrdersCount() {
        return hoaDonRepository.countByTrangThaiAndDeleted(ORDER_STATUS_PENDING, true);
    }

    @Override
    public Optional<BigDecimal> getLastEndedShiftCash() {
        Optional<GiaoCa> lastEndedShift = giaoCaRepository.findTopByTrangThaiOrderByIdDesc(SHIFT_STATUS_ENDED);
        return lastEndedShift.map(GiaoCa::getTienMatCuoiCa);
    }
}