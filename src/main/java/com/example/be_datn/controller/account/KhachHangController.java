package com.example.be_datn.controller.account;

import com.example.be_datn.dto.account.response.KhachHangResponse;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.service.account.KhachHangServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/khach-hang")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})

public class KhachHangController {
    private final KhachHangServices khachHangServices;

    @Autowired
    public KhachHangController(KhachHangServices khachHangServices) {
        this.khachHangServices = khachHangServices;
    }

    //hien thi du lieu
    @GetMapping("/home")
    public List<KhachHang> getall(){
        return khachHangServices.getall();
    }

    //add du lieu
    @PostMapping("/add")
    public ResponseEntity<?> addKhachHang(@RequestBody KhachHangResponse khachHangResponse) {
        try {
            KhachHang newKhachHang = khachHangServices.addKhachHang(khachHangResponse);
            return ResponseEntity.ok("Chúc mừng  "+ newKhachHang.getTen()+  "  đã trở thành khách hàng mới!");
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Lỗi khi thêm nhân viên: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
