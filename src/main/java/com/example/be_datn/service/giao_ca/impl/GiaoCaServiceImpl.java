
package com.example.be_datn.service.giao_ca.impl;

import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.service.giao_ca.GiaoCaService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GiaoCaServiceImpl implements GiaoCaService {

    private final Map<Integer, LocalDateTime> shiftStartTimes = new ConcurrentHashMap<>();

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Override
    public Map<String, Object> startShift(Integer nhanVienId) {
        shiftStartTimes.put(nhanVienId, LocalDateTime.now());

        // Logic to get previous shift's data
        Map<String, Object> previousShiftData = new HashMap<>();
        previousShiftData.put("tienMatCuoiCaTruoc", 1000000); // Example data
        previousShiftData.put("donHangChoXuLy", 5); // Example data

        return previousShiftData;
    }

    @Override
    public Map<String, Object> endShift(Integer nhanVienId, BigDecimal tienMatCuoiCa, BigDecimal tienMatBanDau) {
        LocalDateTime startTime = shiftStartTimes.get(nhanVienId);
        if (startTime == null) {
            throw new IllegalStateException("Ca làm việc chưa được bắt đầu.");
        }
        LocalDateTime endTime = LocalDateTime.now();

        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        List<HoaDon> completedOrders = hoaDonRepository.findAll(); // Simplified for now

        BigDecimal tongTienMat = completedOrders.stream()
                .filter(hd -> "Tiền mặt".equals(hd.getLoaiDon()))
                .map(HoaDon::getTongTienSauGiam)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTienChuyenKhoan = completedOrders.stream()
                .filter(hd -> "Chuyển khoản".equals(hd.getLoaiDon()))
                .map(HoaDon::getTongTienSauGiam)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("nhanVienId", nhanVienId);
        reportData.put("thoiGianBatDau", startTime);
        reportData.put("thoiGianKetThuc", endTime);
        reportData.put("tienMatBanDau", tienMatBanDau);
        reportData.put("tienMatCuoiCa", tienMatCuoiCa);
        reportData.put("tongTienMat", tongTienMat);
        reportData.put("tongTienChuyenKhoan", tongTienChuyenKhoan);
        reportData.put("tongDoanhThu", tongTienMat.add(tongTienChuyenKhoan));
        reportData.put("completedOrders", completedOrders);

        shiftStartTimes.remove(nhanVienId);

        return reportData;
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
}
