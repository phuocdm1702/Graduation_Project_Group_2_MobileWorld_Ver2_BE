package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.request.ChiTietSanPhamUpdateRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamDetailResponse;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import com.example.be_datn.repository.order.HoaDonChiTietRepository;
import com.example.be_datn.service.product.ChiTietSanPhamService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger logger = LoggerFactory.getLogger(ChiTietSanPhamController.class);
    private final ChiTietSanPhamService chiTietSanPhamService;

    public ChiTietSanPhamController(ChiTietSanPhamService chiTietSanPhamService) {
        this.chiTietSanPhamService = chiTietSanPhamService;
    }

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @GetMapping("/{chiTietSanPhamId}/id-san-pham")
    public ResponseEntity<Integer> getIdSanPhamByChiTietSanPhamId(@PathVariable Integer chiTietSanPhamId) {
        Integer idSanPham = hoaDonChiTietRepository.findIdSanPhamByChiTietSanPhamId(chiTietSanPhamId);
        if (idSanPham == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(idSanPham);
    }

    @GetMapping("/find-id-imel-by-imei")
    public ResponseEntity<Map<String, Integer>> findIdImelByImei(@RequestParam String imei) {
        logger.info("Finding IdImel for IMEI: {}", imei);
        Integer idImel = chiTietSanPhamService.findIdImelByImei(imei);
        Map<String, Integer> response = new HashMap<>();
        response.put("idImel", idImel);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ChiTietSanPhamResponse> createChiTietSanPham(
            @Valid @ModelAttribute ChiTietSanPhamRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "existingImageUrls", required = false) List<String> existingImageUrls) {
        logger.info("Creating new ChiTietSanPham with request: {}", request);
        ChiTietSanPhamResponse response = chiTietSanPhamService.createChiTietSanPham(request, images, existingImageUrls);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        logger.warn("Validation errors: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        logger.warn("Illegal argument error: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{idSanPham}")
    public ResponseEntity<List<ChiTietSanPhamDetailResponse>> getProductDetailsBySanPhamId(
            @PathVariable Integer idSanPham) {
        logger.info("Fetching product details for idSanPham: {}", idSanPham);
        List<ChiTietSanPhamDetailResponse> responses = chiTietSanPhamService.getProductDetailsBySanPhamId(idSanPham);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/find-by-imei")
    public ResponseEntity<Map<String, Integer>> findChiTietSanPhamIdByImei(@RequestParam String imei) {
        logger.info("Finding ChiTietSanPham ID for IMEI: {}", imei);
        Integer id = chiTietSanPhamService.findChiTietSanPhamIdByImei(imei);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietSanPhamResponse> updateChiTietSanPham(
            @PathVariable Integer id,
            @Valid @ModelAttribute ChiTietSanPhamUpdateRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "existingImageUrls", required = false) List<String> existingImageUrls) {
        logger.info("Updating ChiTietSanPham with ID: {} and request: {}", id, request);
        ChiTietSanPhamResponse response = chiTietSanPhamService.updateChiTietSanPham(id, request, images, existingImageUrls);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}