package com.example.be_datn.service.account;


import com.example.be_datn.entity.account.TaiKhoan;

public interface TaiKhoanService {
    String findById(Integer idTK);

    TaiKhoan trangthaiKH(Integer id);

    TaiKhoan trangthaiNV(Integer id);

    String dangnhap(String tenDangNhap, String matKhau);

    String dangnhapWeb(String login, String matKhau);
}
