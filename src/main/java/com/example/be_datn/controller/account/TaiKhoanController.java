package com.example.be_datn.controller.account;

import com.example.be_datn.dto.account.response.TaiKhoanDTO;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.service.account.TaiKhoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:3000"})
@RequestMapping("tai-khoan")

public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;

    public TaiKhoanController(TaiKhoanService taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    @GetMapping("/home")
    public List<TaiKhoan> getall(){
        return taiKhoanService.getall();
    }


    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangnhap(@RequestBody TaiKhoanDTO taiKhoanDTO) {
        try {
            if ((taiKhoanDTO.getTenDangNhap() == null || taiKhoanDTO.getTenDangNhap().trim().isEmpty()) &&
                    (taiKhoanDTO.getEmail() == null || taiKhoanDTO.getEmail().trim().isEmpty())) {
                return ResponseEntity.status(400).body("Vui lòng cung cấp tên đăng nhập hoặc email");
            }
            String login = taiKhoanDTO.getTenDangNhap() != null && !taiKhoanDTO.getTenDangNhap().trim().isEmpty()
                    ? taiKhoanDTO.getTenDangNhap() : taiKhoanDTO.getEmail();
            String taikhoan = taiKhoanService.dangnhap(login, taiKhoanDTO.getMatKhau());
            return ResponseEntity.ok().body("Đăng nhập thành công: " + taikhoan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    @PostMapping("/dang-nhap-Web")
    public ResponseEntity<?> dangnhapWeb(@RequestBody TaiKhoanDTO taiKhoanDTO) {
        try {
            if ((taiKhoanDTO.getTenDangNhap() == null || taiKhoanDTO.getTenDangNhap().trim().isEmpty()) &&
                    (taiKhoanDTO.getEmail() == null || taiKhoanDTO.getEmail().trim().isEmpty())) {
                return ResponseEntity.status(400).body("Vui lòng cung cấp tên đăng nhập hoặc email");
            }
            String login = taiKhoanDTO.getTenDangNhap() != null && !taiKhoanDTO.getTenDangNhap().trim().isEmpty()
                    ? taiKhoanDTO.getTenDangNhap() : taiKhoanDTO.getEmail();
            String taikhoan = taiKhoanService.dangnhapWeb(login, taiKhoanDTO.getMatKhau());
            return ResponseEntity.ok().body("Đăng nhập thành công: " + taikhoan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}
