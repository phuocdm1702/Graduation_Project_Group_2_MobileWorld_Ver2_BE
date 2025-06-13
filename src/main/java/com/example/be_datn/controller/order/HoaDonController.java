package com.example.be_datn.controller.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.service.order.HoaDonService;
import com.example.be_datn.service.order.InHoaDonService;
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

import java.sql.Timestamp;
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
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) Timestamp startDate,
            @RequestParam(required = false) Timestamp endDate,
            @RequestParam(required = false) Short trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(hoaDonService.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai,pageable));
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
}
