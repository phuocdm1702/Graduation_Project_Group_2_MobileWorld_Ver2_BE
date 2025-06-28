package com.example.be_datn.controller.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.service.order.HoaDonService;
import com.example.be_datn.service.order.InHoaDonService;
import com.example.be_datn.service.order.XuatDanhSachHoaDon;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@CrossOrigin(origins = "http://localhost:5173")
@Controller
@RequestMapping("/api/hoa-don")
public class HoaDonController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private InHoaDonService inHoaDonService;

    @GetMapping("/home/trang-thai")
    public ResponseEntity<Page<HoaDonResponse>> getHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(hoaDonService.getHoaDon(pageable));
    }

    @GetMapping("/home")
    public ResponseEntity<Page<HoaDonResponse>> getAllHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) Timestamp startDate,
            @RequestParam(required = false) Timestamp endDate,
            @RequestParam(required = false) Short trangThai,
            @RequestParam(required = false) String loaiDon) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDonResponse> response = hoaDonService.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai, loaiDon, pageable);
        return ResponseEntity.ok(response); // Trả về Page trống nếu không có dữ liệu
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<HoaDonDetailResponse> getHoaDonDetail(@PathVariable Integer id) {
        HoaDonDetailResponse response = hoaDonService.getHoaDonDetail(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> inHoaDon(@PathVariable Integer id) throws Exception {
        HoaDonDetailResponse hoaDon = hoaDonService.getHoaDonDetail(id);
        byte[] pdfBytes = inHoaDonService.generateHoaDonPdf(hoaDon);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "hoa_don_" + hoaDon.getMaHoaDon() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // API lấy hóa đơn theo mã QR
    @GetMapping("/QR-by-ma/{ma}")
    public ResponseEntity<HoaDonResponse> getHoaDonByMa(@PathVariable String maHD) {
        try {
            return ResponseEntity.ok(hoaDonService.getHoaDonByMa(maHD));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // API xuất danh sách hóa đơn ra Excel
    @GetMapping("/export-excel")
    public void exportHoaDonToExcel(HttpServletResponse response) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "DanhSachHoaDon_" + timestamp + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        hoaDonService.exportHoaDonToExcel(response);
    }
}
