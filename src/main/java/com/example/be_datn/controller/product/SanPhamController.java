package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.response.SanPhamResponseDto;
import com.example.be_datn.dto.product.request.SanPhamRequestDto;
import com.example.be_datn.entity.product.SanPham;
import com.example.be_datn.service.product.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@Controller
@RequestMapping("/san-pham")
public class SanPhamController {
    private final SanPhamService sanPhamService;

    @Autowired
    public SanPhamController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public ResponseEntity<Page<SanPhamResponseDto>> getAllSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<SanPhamResponseDto> sanPhams = sanPhamService.getAllSanPham(page, size);
        return ResponseEntity.ok(sanPhams);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SanPhamResponseDto>> searchSanPham(
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

        Page<SanPhamResponseDto> sanPhams = sanPhamService.searchSanPham(
                keyword, idNhaSanXuat, idHeDieuHanh, heDieuHanh, phienBan,
                idCongNgheManHinh, congNgheManHinh, chuanManHinh,
                idPin, loaiPin, dungLuongPin,
                idCpu, idGpu, idCumCamera, idThietKe, idSim,
                idHoTroCongNgheSac, idCongNgheMang, inStock, page, size
        );
        return ResponseEntity.ok(sanPhams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamResponseDto> getSanPhamById(@PathVariable Integer id) {
        Optional<SanPham> sanPham = sanPhamService.getSanPhamById(id);
        return sanPham.map(sp -> ResponseEntity.ok(sanPhamService.mapToDTO(sp)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SanPhamResponseDto> createSanPham(@Valid @RequestBody SanPhamRequestDto requestDto) {
        SanPham sanPham = sanPhamService.createSanPham(requestDto);
        return ResponseEntity.ok(sanPhamService.mapToDTO(sanPham));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSanPham(@PathVariable Integer id, @Valid @RequestBody SanPhamRequestDto requestDto) {
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
}