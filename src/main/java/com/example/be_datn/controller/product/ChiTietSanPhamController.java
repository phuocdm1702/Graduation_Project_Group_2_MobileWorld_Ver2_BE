package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import com.example.be_datn.service.product.ChiTietSanPhamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chi-tiet-san-pham")
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    public ChiTietSanPhamController(ChiTietSanPhamService chiTietSanPhamService) {
        this.chiTietSanPhamService = chiTietSanPhamService;
    }

    /**
     * Tạo mới chi tiết sản phẩm với thông tin và danh sách ảnh
     */
    @PostMapping
    public ResponseEntity<ChiTietSanPhamResponse> createChiTietSanPham(
            @RequestPart("dto") ChiTietSanPhamRequest dto,
            @RequestPart("images") List<MultipartFile> images,
            HttpServletRequest request) {
        try {
            System.out.println("Request Content-Type: " + request.getContentType());
            System.out.println("DTO: " + dto);
            System.out.println("Images count: " + (images != null ? images.size() : "null"));

            ChiTietSanPhamResponse response = chiTietSanPhamService.createChiTietSanPham(dto, images);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (MultipartException e) {
            System.err.println("Multipart error: " + e.getMessage());
            return new ResponseEntity<>(new ChiTietSanPhamResponse(null, null, null) {
                @Override
                public String toString() {
                    return "Lỗi: Request không đúng định dạng multipart/form-data. Kiểm tra Content-Type và dữ liệu gửi.";
                }
            }, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return new ResponseEntity<>(new ChiTietSanPhamResponse(null, null, null) {
                @Override
                public String toString() {
                    return "Lỗi dữ liệu đầu vào: " + e.getMessage();
                }
            }, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            System.err.println("State error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Cập nhật chi tiết sản phẩm theo ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateChiTietSanPham(
            @PathVariable Integer id,
            @RequestBody ChiTietSanPhamRequest dto) {
        try {
            chiTietSanPhamService.updateChiTietSanPham(id, dto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Cập nhật giá cho sản phẩm theo ID sản phẩm
     */
    @PatchMapping("/{id}/price")
    public ResponseEntity<Void> updatePrice(
            @PathVariable Integer id,
            @RequestParam BigDecimal newPrice) {
        try {
            chiTietSanPhamService.updatePrice(id, newPrice);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Lấy danh sách chi tiết sản phẩm theo ID sản phẩm
     */
    @GetMapping("/san-pham/{Id}")
    public ResponseEntity<List<ChiTietSanPhamResponse>> getChiTietSanPhamBySanPhamId(
            @PathVariable Integer sanPhamId) {
        try {
            List<ChiTietSanPhamResponse> responses = chiTietSanPhamService.getChiTietSanPhamBySanPhamId(sanPhamId);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Lấy danh sách chi tiết sản phẩm với phân trang và lọc
     */
    @GetMapping("/details/{Id}")
    public ResponseEntity<Page<ChiTietSanPhamResponse>> getChiTietSanPhamDetails(
            @PathVariable Integer sanPhamId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer idMauSac,
            @RequestParam(required = false) Integer idBoNhoTrong,
            @RequestParam(required = false) Integer idRam,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ChiTietSanPhamResponse> responses = chiTietSanPhamService.getChiTietSanPhamDetails(
                    sanPhamId, keyword, status, idMauSac, idBoNhoTrong, idRam, minPrice, maxPrice, page, size);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Lấy khoảng giá của sản phẩm theo ID sản phẩm
     */
    @GetMapping("/{Id}/price-range")
    public ResponseEntity<Map<String, BigDecimal>> getPriceRange(
            @PathVariable Integer sanPhamId) {
        try {
            Map<String, BigDecimal> priceRange = chiTietSanPhamService.getPriceRange(sanPhamId);
            return new ResponseEntity<>(priceRange, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}