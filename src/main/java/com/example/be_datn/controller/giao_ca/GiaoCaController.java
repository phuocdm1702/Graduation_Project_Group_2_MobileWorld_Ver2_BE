
package com.example.be_datn.controller.giao_ca;

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

@RestController
@RequestMapping("/api/giao-ca")
@CrossOrigin(origins = {"http://localhost:5173"})
public class GiaoCaController {

    @Autowired
    private GiaoCaService giaoCaService;

    @PostMapping("/bat-dau")
    public ResponseEntity<Map<String, Object>> batDauCa(@RequestParam Integer nhanVienId) {
        Map<String, Object> previousShiftData = giaoCaService.startShift(nhanVienId);
        return ResponseEntity.ok(previousShiftData);
    }

    @PostMapping("/ket-thuc")
    public ResponseEntity<Map<String, Object>> ketThucCa(
            @RequestParam Integer nhanVienId,
            @RequestParam BigDecimal tienMatCuoiCa,
            @RequestParam BigDecimal tienMatBanDau) {
        Map<String, Object> reportData = giaoCaService.endShift(nhanVienId, tienMatCuoiCa, tienMatBanDau);
        return ResponseEntity.ok(reportData);
    }

    @PostMapping("/xuat-excel")
    public ResponseEntity<InputStreamResource> xuatExcel(@RequestBody Map<String, Object> reportData) {
        ByteArrayInputStream bis = giaoCaService.generateExcelReport(reportData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=BienBanGiaoCa.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(bis));
    }
}
