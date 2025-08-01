package com.example.be_datn.service.giao_ca.impl;

import com.example.be_datn.entity.giao_ca.GiaoCa;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.repository.giao_ca.GiaoCaRepository;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
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
import java.time.ZoneId;
import java.util.*;

@Service
public class GiaoCaServiceImpl implements GiaoCaService {

    @Autowired
    private GiaoCaRepository giaoCaRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    // Trạng thái ca làm việc
    private static final Short SHIFT_STATUS_ACTIVE = 1; // Đang diễn ra
    private static final Short SHIFT_STATUS_ENDED = 2;  // Đã kết thúc

    // Trạng thái hóa đơn
    private static final Short ORDER_STATUS_PENDING = 0; // Chờ xử lý
    private static final Short ORDER_STATUS_COMPLETED = 3; // Đã hoàn thành (giả định)

    @Override
    @Transactional
    public GiaoCa startShift(Integer nhanVienId, BigDecimal tienMatBanDau) {
        // 1. Kiểm tra xem nhân viên đã có ca nào đang hoạt động chưa
        Optional<GiaoCa> activeShift = giaoCaRepository.findByidNhanVien_IdAndTrangThai(nhanVienId, SHIFT_STATUS_ACTIVE);
        if (activeShift.isPresent()) {
            throw new IllegalStateException("Nhân viên đã có ca làm việc đang diễn ra.");
        }

        // 2. Lấy thông tin ca trước (nếu có)
        BigDecimal tienMatCaTruoc = BigDecimal.ZERO;
        Integer donHangChoXuLyCaTruoc = 0;
        Optional<GiaoCa> lastEndedShift = giaoCaRepository.findTopByTrangThaiOrderByIdDesc(SHIFT_STATUS_ENDED);
        if (lastEndedShift.isPresent()) {
            GiaoCa previousShift = lastEndedShift.get();
            tienMatCaTruoc = previousShift.getTienMatCuoiCa() != null ? previousShift.getTienMatCuoiCa() : BigDecimal.ZERO;
            donHangChoXuLyCaTruoc = previousShift.getDonHangChoXuLyCaTruoc(); // Lấy từ ca trước
        }

        // 3. Đếm số hóa đơn chờ xử lý hiện tại
        long currentPendingOrders = hoaDonRepository.countByTrangThaiAndDeleted(ORDER_STATUS_PENDING, true);

        // 4. Lấy đối tượng NhanVien từ ID
        com.example.be_datn.entity.account.NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + nhanVienId));

        // 5. Tạo bản ghi ca làm việc mới
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
    public GiaoCa endShift(Integer nhanVienId, BigDecimal tienMatCuoiCa) {
        // 1. Tìm ca làm việc đang hoạt động của nhân viên
        GiaoCa currentShift = giaoCaRepository.findByidNhanVien_IdAndTrangThai(nhanVienId, SHIFT_STATUS_ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Ca làm việc chưa được bắt đầu."));

        LocalDateTime startTime = currentShift.getThoiGianBatDau();
        LocalDateTime endTime = LocalDateTime.now();

        // 2. Lấy các hóa đơn đã hoàn thành trong ca này
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        List<HoaDon> completedOrdersInShift = hoaDonRepository.findByTrangThaiAndNgayTaoBetween(ORDER_STATUS_COMPLETED, startDate, endDate);

        // 3. Tính toán tổng tiền mặt và chuyển khoản
        BigDecimal tongTienMat = completedOrdersInShift.stream()
                .filter(hd -> "Tiền mặt".equals(hd.getLoaiDon()))
                .map(HoaDon::getTongTienSauGiam)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTienChuyenKhoan = completedOrdersInShift.stream()
                .filter(hd -> "Chuyển khoản".equals(hd.getLoaiDon()))
                .map(HoaDon::getTongTienSauGiam)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongDoanhThu = tongTienMat.add(tongTienChuyenKhoan);

        // 4. Cập nhật thông tin ca làm việc
        currentShift.setThoiGianKetThuc(endTime);
        currentShift.setTienMatCuoiCa(tienMatCuoiCa);
        currentShift.setTongTienMat(tongTienMat);
        currentShift.setTongTienChuyenKhoan(tongTienChuyenKhoan);
        currentShift.setTongDoanhThu(tongDoanhThu);
        currentShift.setTrangThai(SHIFT_STATUS_ENDED);

        return giaoCaRepository.save(currentShift);
    }

    @Override
    public Optional<GiaoCa> getActiveShift(Integer nhanVienId) {
        return giaoCaRepository.findByidNhanVien_IdAndTrangThai(nhanVienId, SHIFT_STATUS_ACTIVE);
    }

    @Override
    public ByteArrayInputStream generateExcelReport(Map<String, Object> reportData) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Biên Bản Giao Ca");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Mục", "Giá trị"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data
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