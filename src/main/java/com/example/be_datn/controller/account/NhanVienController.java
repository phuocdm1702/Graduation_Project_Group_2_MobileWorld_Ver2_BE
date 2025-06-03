package com.example.be_datn.controller.account;

import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.service.account.NhanVienServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/home")
    public List<NhanVien> getallNhanVien() {
        return nhanVienServices.getall();
    }

}
