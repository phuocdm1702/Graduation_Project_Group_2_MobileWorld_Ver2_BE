package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.response.SanPhamResponse;
import com.example.be_datn.dto.product.request.SanPhamRequest;
import com.example.be_datn.entity.product.SanPham;
import com.example.be_datn.service.product.SanPhamService;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/san-pham")
@Slf4j
public class SanPhamController {
    private final SanPhamService sanPhamService;

    @Autowired
    public SanPhamController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public ResponseEntity<Page<SanPhamResponse>> getAllSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<SanPhamResponse> sanPhams = sanPhamService.getAllSanPham(page, size);
        return ResponseEntity.ok(sanPhams);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SanPhamResponse>> getAllSanPhamList() {
        List<SanPhamResponse> sanPhams = sanPhamService.getAllSanPhamList();
        return ResponseEntity.ok(sanPhams);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SanPhamResponse>> searchSanPham(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer idNhaSanXuat,
            @RequestParam(required = false) Integer idHeDieuHanh,
            @RequestParam(required = false) String heDieuHanh,
            @RequestParam(required = false) String phienBan,
            @RequestParam(required = false) Integer idCongNgheManHinh,
            @RequestParam(required = false) String congNgheManHinh,
            @RequestParam(required = false) String chuanManHinh,
            @RequestParam(required = false) Integer idPin,
            @RequestParam(required = false) String loaiPin,
            @RequestParam(required = false) String dungLuongPin,
            @RequestParam(required = false) Integer idCpu,
            @RequestParam(required = false) Integer idGpu,
            @RequestParam(required = false) Integer idCumCamera,
            @RequestParam(required = false) Integer idThietKe,
            @RequestParam(required = false) Integer idSim,
            @RequestParam(required = false) Integer idHoTroCongNgheSac,
            @RequestParam(required = false) Integer idCongNgheMang,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<SanPhamResponse> sanPhams = sanPhamService.searchSanPham(
                keyword, idNhaSanXuat, idHeDieuHanh, heDieuHanh, phienBan,
                idCongNgheManHinh, congNgheManHinh, chuanManHinh,
                idPin, loaiPin, dungLuongPin,
                idCpu, idGpu, idCumCamera, idThietKe, idSim,
                idHoTroCongNgheSac, idCongNgheMang, inStock, page, size
        );
        return ResponseEntity.ok(sanPhams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamResponse> getSanPhamById(@PathVariable Integer id) {
        Optional<SanPham> sanPham = sanPhamService.getSanPhamById(id);
        return sanPham.map(sp -> ResponseEntity.ok(sanPhamService.mapToDTO(sp)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SanPhamResponse> createSanPham(@Valid @RequestBody SanPhamRequest requestDto) {
        SanPham sanPham = sanPhamService.createSanPham(requestDto);
        return ResponseEntity.ok(sanPhamService.mapToDTO(sanPham));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSanPham(@PathVariable Integer id, @Valid @RequestBody SanPhamRequest requestDto) {
        Optional<SanPham> existingSanPham = sanPhamService.getSanPhamById(id);
        if (existingSanPham.isPresent()) {
            try {
                SanPham updatedSanPham = sanPhamService.updateSanPham(id, requestDto);
                return ResponseEntity.ok(sanPhamService.mapToDTO(updatedSanPham));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Cập nhật sản phẩm thất bại: " + e.getMessage());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}