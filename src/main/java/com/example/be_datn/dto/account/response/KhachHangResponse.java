package com.example.be_datn.dto.account.response;

import java.util.Date;

public class KhachHangResponse {

    private Integer id;
    private String ma;
    private String tenKH;
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

    public KhachHangResponse() {
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

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getAnhKhachHang() {
        return anhKhachHang;
    }

    public void setAnhKhachHang(String anhKhachHang) {
        this.anhKhachHang = anhKhachHang;
    }

    public String getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }

    public String getQuan() {
        return quan;
    }

    public void setQuan(String quan) {
        this.quan = quan;
    }

    public String getPhuong() {
        return phuong;
    }

    public void setPhuong(String phuong) {
        this.phuong = phuong;
    }

    public String getDiaChiCuThe() {
        return diaChiCuThe;
    }

    public void setDiaChiCuThe(String diaChiCuThe) {
        this.diaChiCuThe = diaChiCuThe;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(Boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getMacDinh() {
        return macDinh;
    }

    public void setMacDinh(Boolean macDinh) {
        this.macDinh = macDinh;
    }
}