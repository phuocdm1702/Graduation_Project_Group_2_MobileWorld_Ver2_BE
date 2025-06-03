package com.example.be_datn.dto.discount.request;

import java.util.Date;

public class PhieuGiamGiaCaNhanRequest {
    private Integer id;
    private String ma;
    private String ten;
    private Date ngaySinh;

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

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
}
