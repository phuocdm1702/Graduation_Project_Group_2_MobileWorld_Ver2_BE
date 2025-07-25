package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamDetailResponse;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import com.example.be_datn.service.product.ChiTietSanPhamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chi-tiet-san-pham")
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    public ChiTietSanPhamController(ChiTietSanPhamService chiTietSanPhamService) {
        this.chiTietSanPhamService = chiTietSanPhamService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ChiTietSanPhamResponse> createChiTietSanPham(
            @Valid @ModelAttribute ChiTietSanPhamRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "existingImageUrls", required = false) List<String> existingImageUrls) {
        ChiTietSanPhamResponse response = chiTietSanPhamService.createChiTietSanPham(request, images, existingImageUrls);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{idSanPham}")
    public ResponseEntity<List<ChiTietSanPhamDetailResponse>> getProductDetailsBySanPhamId(
            @PathVariable Integer idSanPham) {
        List<ChiTietSanPhamDetailResponse> responses = chiTietSanPhamService.getProductDetailsBySanPhamId(idSanPham);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietSanPhamResponse> updateChiTietSanPham(
            @PathVariable Integer id,
            @Valid @ModelAttribute ChiTietSanPhamRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "existingImageUrls", required = false) List<String> existingImageUrls) {
        ChiTietSanPhamResponse response = chiTietSanPhamService.updateChiTietSanPham(id, request, images, existingImageUrls);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}