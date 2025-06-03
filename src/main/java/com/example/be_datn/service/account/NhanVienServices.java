package com.example.be_datn.service.account;

import com.example.be_datn.dto.account.response.NhanVienResponse;
import com.example.be_datn.entity.account.NhanVien;
import java.util.List;

public interface NhanVienServices {
    List<NhanVien> getall();

    NhanVien addNhanVien(NhanVienResponse nhanVienResponse);
}
