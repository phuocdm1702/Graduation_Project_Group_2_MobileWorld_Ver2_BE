package com.example.be_datn.controller.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.service.order.HoaDonService;
import com.example.be_datn.service.clientService.BanHangClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private BanHangClientService banHangClientService;

    /**
     * Gửi email thông báo trạng thái hóa đơn
     */
    @PostMapping("/send-invoice-status/{hoaDonId}")
    public ResponseEntity<Map<String, Object>> sendInvoiceStatusEmail(@PathVariable Integer hoaDonId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Lấy thông tin chi tiết hóa đơn
            HoaDonDetailResponse hoaDonDetail = hoaDonService.getHoaDonDetail(hoaDonId);
            
            // Kiểm tra email khách hàng
            if (hoaDonDetail.getEmail() == null || hoaDonDetail.getEmail().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Không có email khách hàng để gửi thông báo");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Gửi email
            banHangClientService.guiEmailThongTinDonHang(hoaDonDetail, hoaDonDetail.getEmail());
            
            response.put("success", true);
            response.put("message", "Đã gửi email thông báo trạng thái đơn hàng thành công");
            response.put("email", hoaDonDetail.getEmail());
            response.put("hoaDonId", hoaDonId);
            response.put("maHoaDon", hoaDonDetail.getMaHoaDon());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi gửi email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Gửi email thông báo trạng thái với email tùy chỉnh
     */
    @PostMapping("/send-invoice-status/{hoaDonId}/custom-email")
    public ResponseEntity<Map<String, Object>> sendInvoiceStatusEmailCustom(
            @PathVariable Integer hoaDonId,
            @RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate email format
            if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                response.put("success", false);
                response.put("message", "Email không hợp lệ");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Lấy thông tin chi tiết hóa đơn
            HoaDonDetailResponse hoaDonDetail = hoaDonService.getHoaDonDetail(hoaDonId);
            
            // Gửi email với địa chỉ tùy chỉnh
            banHangClientService.guiEmailThongTinDonHang(hoaDonDetail, email.trim());
            
            response.put("success", true);
            response.put("message", "Đã gửi email thông báo trạng thái đơn hàng thành công");
            response.put("email", email.trim());
            response.put("hoaDonId", hoaDonId);
            response.put("maHoaDon", hoaDonDetail.getMaHoaDon());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi gửi email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Kiểm tra email khách hàng có tồn tại không
     */
    @GetMapping("/check-email/{hoaDonId}")
    public ResponseEntity<Map<String, Object>> checkCustomerEmail(@PathVariable Integer hoaDonId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            HoaDonDetailResponse hoaDonDetail = hoaDonService.getHoaDonDetail(hoaDonId);
            
            boolean hasEmail = hoaDonDetail.getEmail() != null && !hoaDonDetail.getEmail().trim().isEmpty();
            
            response.put("success", true);
            response.put("hasEmail", hasEmail);
            response.put("email", hasEmail ? hoaDonDetail.getEmail() : null);
            response.put("hoaDonId", hoaDonId);
            response.put("maHoaDon", hoaDonDetail.getMaHoaDon());
            response.put("tenKhachHang", hoaDonDetail.getTenKhachHang());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
