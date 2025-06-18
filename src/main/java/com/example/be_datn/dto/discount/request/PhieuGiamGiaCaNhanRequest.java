package com.example.be_datn.dto.discount.request;

import java.util.Date;

public class PhieuGiamGiaCaNhanRequest {

    private Integer id;
    private String ma;
    private String ten;
    private Date ngaySinh;
    private String anhKhachHang;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    private String cccd;
    private String email;
    private String soDienThoai;
    private String userName;
    private Boolean gioiTinh;
    private Date createdAt;
    private Boolean macDinh;

    public PhieuGiamGiaCaNhanRequest() {
    }

    public PhieuGiamGiaCaNhanRequest(Integer id, String ma, String ten, Date ngaySinh) {
        this.id = id;
        this.ma = ma;
        this.ten = ten;
        this.ngaySinh = ngaySinh;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
