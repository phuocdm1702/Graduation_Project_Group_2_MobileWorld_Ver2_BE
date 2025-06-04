package com.example.be_datn.service.account;

import com.example.be_datn.dto.account.response.KhachHangResponse;
import com.example.be_datn.entity.account.KhachHang;

import java.util.List;

public interface KhachHangServices {
    List<KhachHang> getall();

    KhachHang addKhachHang(KhachHangResponse khachHangResponse);
}
