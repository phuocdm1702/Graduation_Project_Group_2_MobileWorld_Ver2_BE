package com.example.be_datn.controller.giao_ca;

import com.example.be_datn.entity.giao_ca.GiaoCa;
import com.example.be_datn.service.giao_ca.GiaoCaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/giao-ca")
@CrossOrigin(origins = {"http://localhost:5173"})
public class GiaoCaController {

    @Autowired
    private GiaoCaService giaoCaService;

    @PostMapping("/bat-dau")
    public ResponseEntity<GiaoCa> batDauCa(@RequestParam Integer nhanVienId, @RequestParam BigDecimal tienMatBanDau) {
        GiaoCa newShift = giaoCaService.startShift(nhanVienId, tienMatBanDau);
        return ResponseEntity.ok(newShift);
    }

    @PostMapping("/ket-thuc")
    public ResponseEntity<GiaoCa> ketThucCa(
            @RequestParam Integer nhanVienId,
            @RequestParam BigDecimal tienMatCuoiCa) {
        GiaoCa endedShift = giaoCaService.endShift(nhanVienId, tienMatCuoiCa);
        return ResponseEntity.ok(endedShift);
    }

    @GetMapping("/active")
    public ResponseEntity<GiaoCa> getActiveShift(@RequestParam Integer nhanVienId) {
        Optional<GiaoCa> activeShift = giaoCaService.getActiveShift(nhanVienId);
        return activeShift.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/xuat-excel")
    public ResponseEntity<InputStreamResource> xuatExcel(@RequestBody Map<String, Object> reportData) {
        ByteArrayInputStream bis = giaoCaService.generateExcelReport(reportData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=BienBanGiaoCa.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/pending-orders-count")
    public ResponseEntity<Long> getPendingOrdersCount() {
        long count = giaoCaService.getPendingOrdersCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/last-ended-shift-cash")
    public ResponseEntity<BigDecimal> getLastEndedShiftCash() {
        Optional<BigDecimal> cash = giaoCaService.getLastEndedShiftCash();
        return cash.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}