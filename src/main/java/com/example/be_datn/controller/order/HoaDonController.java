package com.example.be_datn.controller.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.dto.order.response.HoaDonChiTietImeiResponse;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.service.order.HoaDonService;
import com.example.be_datn.service.order.InHoaDonService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
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
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
        return ResponseEntity.ok(hoaDonService.getHoaDon(pageable));
    }

    @GetMapping("/hoa-don-chi-tiet/imel")
    public ResponseEntity<Page<Imel>> getImelHoaDonInHDCT(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = true) Integer idSanPham,
            @RequestParam(required = true) Integer chiTietSanPhamId) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(hoaDonService.getAllImelBySanPhamId(pageable, false, idSanPham, chiTietSanPhamId));
    }

    @GetMapping("/home")
    public ResponseEntity<?> getAllHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,  // Default size nhỏ hơn để phân trang thực
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) Timestamp startDate,
            @RequestParam(required = false) Timestamp endDate,
            @RequestParam(required = false) Short trangThai,
            @RequestParam(required = false) String loaiDon,
            @RequestParam(defaultValue = "id") String sortBy,  // Mới: Field sắp xếp
            @RequestParam(defaultValue = "DESC") String sortDir) {  // Mới: Hướng sắp xếp
        if (loaiDon != null) {
            loaiDon = loaiDon.toLowerCase();
            if (!loaiDon.equals("trực tiếp") && !loaiDon.equals("online")) {
                loaiDon = null;
            }
        }
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            return ResponseEntity.badRequest().body("minAmount phải nhỏ hơn hoặc bằng maxAmount");
        }
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            return ResponseEntity.badRequest().body("startDate phải trước hoặc bằng endDate");
        }
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);  // Xây sort động
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HoaDonResponse> response = hoaDonService.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai, loaiDon, pageable);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/home")
//    public ResponseEntity<?> getAllHoaDon(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size,
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) Long minAmount,
//            @RequestParam(required = false) Long maxAmount,
//            @RequestParam(required = false) Timestamp startDate,
//            @RequestParam(required = false) Timestamp endDate,
//            @RequestParam(required = false) Short trangThai,
//            @RequestParam(required = false) String loaiDon) {
//        if (loaiDon != null) {
//            loaiDon = loaiDon.toLowerCase();
//            if (!loaiDon.equals("trực tiếp") && !loaiDon.equals("online")) {
//                loaiDon = null;
//            }
//        }
//        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
//            return ResponseEntity.badRequest().body("minAmount phải nhỏ hơn hoặc bằng maxAmount");
//        }
//        if (startDate != null && endDate != null && startDate.after(endDate)) {
//            return ResponseEntity.badRequest().body("startDate phải trước hoặc bằng endDate");
//        }
//        Pageable pageable = PageRequest.of(page, size);
//        // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
//        Page<HoaDonResponse> response = hoaDonService.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai, loaiDon, pageable);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam Integer idKhachHang,
            @RequestParam(required = false) Timestamp startDate,
            @RequestParam(required = false) Timestamp endDate,
            @RequestParam(required = false) Short trangThai,
            @RequestParam(required = false) Boolean deleted) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDonResponse> response = hoaDonService.getHoaDonOfCustomerAndFilters(idKhachHang, startDate, endDate, trangThai, deleted, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<HoaDonDetailResponse> getHoaDonDetail(@PathVariable Integer id) {
        // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
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

    @GetMapping("/QR-by-ma/{ma}")
    public ResponseEntity<HoaDonResponse> getHoaDonByMa(@PathVariable String maHD) {
        try {
            // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
            return ResponseEntity.ok(hoaDonService.getHoaDonByMa(maHD));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/export-excel")
    public void exportHoaDonToExcel(HttpServletResponse response) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "DanhSachHoaDon_" + timestamp + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        hoaDonService.exportHoaDonToExcel(response);
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<HoaDonResponse> updateHoaDonStatus(
            @PathVariable Integer id,
            @RequestParam Short trangThai,
            @RequestParam(required = false) Integer idNhanVien) {
        try {
            // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
            HoaDonResponse response = hoaDonService.updateHoaDonStatus(id, trangThai, idNhanVien);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<HoaDonResponse> cancelOrder(@PathVariable Integer id) {
        try {
            HoaDonResponse response = hoaDonService.cancelOrder(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/cancel-client")
    public ResponseEntity<HoaDonResponse> cancelOrderClient(@PathVariable Integer id) {
        try {
            HoaDonResponse response = hoaDonService.cancelOrderClient(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/xac-nhan-imei/{idHD}")
    public ResponseEntity<HoaDonResponse> confirmAndAssignIMEI(
            @PathVariable Integer idHD,
            @RequestBody Map<Integer, String> imelMap) {
        try {
            // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
            HoaDonResponse response = hoaDonService.confirmAndAssignIMEI(idHD, imelMap);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/update-customer-info")
    public ResponseEntity<HoaDonResponse> updateHoaDonCustomerInfo(
            @PathVariable Integer id,
            @RequestParam String tenKhachHang,
            @RequestParam String soDienThoai,
            @RequestParam String diaChi,
            @RequestParam String email) {
        try {
            // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
            HoaDonResponse response = hoaDonService.updateHoaDonKH(id, tenKhachHang, soDienThoai, diaChi, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/update-hoa-don")
    public ResponseEntity<HoaDonResponse> updateHoaDonDetail(
            @PathVariable Integer id,
            @RequestParam String maHD,
            @RequestParam String loaiHD) {
        try {
            // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
            HoaDonResponse response = hoaDonService.updateHoaDon(id, maHD, loaiHD);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/ma/{maHD}")
    public ResponseEntity<HoaDonResponse> getHoaDonByMaHD(@PathVariable String maHD) {
        try {
            return ResponseEntity.ok(hoaDonService.getHoaDonByMaForLookup(maHD));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Lỗi server không xác định: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @PostMapping("/{idHD}/add-product-to-detail")
    public ResponseEntity<HoaDonResponse> addProductToHoaDonChiTiet(
            @PathVariable Integer idHD,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Integer chiTietSanPhamId = (Integer) requestBody.get("chiTietSanPhamId");
            String imei = (String) requestBody.get("imei");
            if (chiTietSanPhamId == null || imei == null || imei.trim().isEmpty()) {
                throw new IllegalArgumentException("chiTietSanPhamId và imei không được để trống");
            }
            HoaDonResponse response = hoaDonService.addProductToHoaDonChiTiet(idHD, chiTietSanPhamId, imei);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{idHD}/delete-product-from-detail/{idHoaDonChiTiet}")
    public ResponseEntity<HoaDonResponse> deleteProductFromHoaDonChiTiet(
            @PathVariable Integer idHD,
            @PathVariable Integer idHoaDonChiTiet) {
        try {
            HoaDonResponse response = hoaDonService.deleteProductFromHoaDonChiTiet(idHD, idHoaDonChiTiet);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{hoaDonId}/imei")
    public ResponseEntity<Page<HoaDonChiTietImeiResponse>> getImeiByHoaDonId(
            @PathVariable Integer hoaDonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<HoaDonChiTietImeiResponse> response = hoaDonService.getImeiByHoaDonId(hoaDonId, pageable);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}