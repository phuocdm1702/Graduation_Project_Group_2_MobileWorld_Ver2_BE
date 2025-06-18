package com.example.be_datn.dto.account.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class NhanVienResponse {
    private String ma;
    private String tenNhanVien;
    private Date ngaySinh;
    private String anhNhanVien;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    private String cccd;
    private String email;
    private String soDienThoai;
    private String matKhau;
    private String tenDangNhap;
    private Boolean gioiTinh;

}
