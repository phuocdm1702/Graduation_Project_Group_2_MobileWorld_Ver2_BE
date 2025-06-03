package com.example.be_datn.controller.account;

import com.example.be_datn.dto.account.response.NhanVienResponse;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.service.account.NhanVienServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:8080"})
@RequestMapping("/nhan-vien")

public class NhanVienController {

    private final NhanVienServices nhanVienServices;

    @Autowired
    public NhanVienController(NhanVienServices nhanVienServices) {
        this.nhanVienServices = nhanVienServices;
    }

    //Lay du lieu
    @GetMapping("/home")
    public List<NhanVien> getallNhanVien() {
        return nhanVienServices.getall();
    }

    //add nhan vien
    @PostMapping("/add")
    public ResponseEntity<?> addNhanVien(@RequestBody NhanVienResponse nhanVienResponse) {
        try {
            NhanVien nhanVien = nhanVienServices.addNhanVien(nhanVienResponse);
            return new ResponseEntity<>(nhanVien, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thêm nhân viên: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
