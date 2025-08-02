package com.example.be_datn.controller.account;

import com.example.be_datn.dto.account.response.TaiKhoanDTO;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.service.account.KhachHangServices;
import com.example.be_datn.service.account.TaiKhoanService;
import jakarta.security.auth.message.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("tai-khoan")
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;

    private final KhachHangServices khachHangServices;


    public TaiKhoanController(TaiKhoanService taiKhoanService, KhachHangServices khachHangServices) {
        this.taiKhoanService = taiKhoanService;
        this.khachHangServices = khachHangServices;
    }

    @GetMapping("/home")
    public List<TaiKhoan> getall() {
        return taiKhoanService.getall();
    }
    // Lấy thông tin tài khoản theo username
    @GetMapping("/thong-tin/{username}")
    public ResponseEntity<TaiKhoan> getTaiKhoanByUsername(@PathVariable String username) {
        TaiKhoan taiKhoan = taiKhoanService.findByUsername(username);
        if (taiKhoan != null) {
            return ResponseEntity.ok(taiKhoan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    private Map<String, Object> processLogin(String login, String matKhau) {
//        Map<String, Object> response = new HashMap<>();
//        if ((login == null || login.trim().isEmpty()) && (matKhau == null || matKhau.trim().isEmpty())) {
//            response.put("message", "Vui lòng cung cấp tên đăng nhập hoặc email và mật khẩu");
//            return response;
//        }
//
//        try {
//            String taikhoan = taiKhoanService.dangnhapWeb(login, matKhau);
//            Integer customerId = taiKhoanService.getCustomerIdByTaiKhoan(login);
//            if (customerId == null) {
//                response.put("message", "Tài khoản không liên kết với khách hàng");
//                return response;
//            }
//            response.put("message", "Đăng nhập thành công: " + taikhoan);
//            response.put("customerId", customerId);
//            return response;
//        } catch (RuntimeException e) {
//            response.put("message", e.getMessage());
//            return response;
//        } catch (Exception e) {
//            response.put("message", "Lỗi hệ thống: " + e.getMessage());
//            return response;
//        }
//    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangnhap(@RequestBody TaiKhoanDTO userDTO){
        try {
            Map<String, Object> result = taiKhoanService.dangnhap(userDTO.getTenDangNhap(), userDTO.getMatKhau());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e){
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("/dang-nhap-Web")
    public ResponseEntity<?> dangnhapWeb(@RequestBody TaiKhoanDTO taiKhoanDTO) {
        String login = taiKhoanDTO.getTenDangNhap() != null && !taiKhoanDTO.getTenDangNhap().trim().isEmpty()
                ? taiKhoanDTO.getTenDangNhap() : taiKhoanDTO.getEmail();

        Map<String, Object> response = taiKhoanService.dangnhapWeb(login, taiKhoanDTO.getMatKhau());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/thong-tin-khach-hang/{idKhachHang}")
    public ResponseEntity<?> getThongTinKhachHang(@PathVariable Integer idKhachHang) {
        Map<String, Object> response = khachHangServices.layThongTinKhachHang(idKhachHang);
        return ResponseEntity.ok().body(response);
    }
}
