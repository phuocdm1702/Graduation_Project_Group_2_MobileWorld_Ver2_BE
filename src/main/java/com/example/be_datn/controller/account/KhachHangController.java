package com.example.be_datn.controller.account;

import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.service.account.KhachHangServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/home")
    public List<KhachHang> getall(){
        return khachHangServices.getall();
    }

}
