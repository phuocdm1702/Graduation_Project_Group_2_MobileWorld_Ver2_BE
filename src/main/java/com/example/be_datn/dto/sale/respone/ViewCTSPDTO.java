package com.example.be_datn.dto.sale.respone;

import com.example.be_datn.entity.discount.ChiTietDotGiamGia;
import com.example.be_datn.entity.product.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


public class ViewCTSPDTO {
    SanPham sp;
    ChiTietSanPham ctsp;
    AnhSanPham anh;
    ChiTietDotGiamGia ctgg;
    BoNhoTrong bnt;
    MauSac ms;
    BigDecimal giaSauKhiGiam;
    private Boolean selected;
    private Long soLuongTrongDotGiamGiaKhac;

    public ViewCTSPDTO() {
    }

    public ViewCTSPDTO(SanPham sp, ChiTietSanPham ctsp, AnhSanPham anh, BoNhoTrong bnt, MauSac ms) {
        this.sp = sp;
        this.ctsp = ctsp;
        this.anh = anh;
        this.bnt = bnt;
        this.ms = ms;
        this.selected = true;
    }

    public ViewCTSPDTO(SanPham sp, ChiTietSanPham ctsp, AnhSanPham anh, BoNhoTrong bnt, MauSac ms, Long soLuongTrongDotGiamGiaKhac) {
        this.sp = sp;
        this.ctsp = ctsp;
        this.anh = anh;
        this.bnt = bnt;
        this.ms = ms;
        this.soLuongTrongDotGiamGiaKhac = soLuongTrongDotGiamGiaKhac;
    }

    public ViewCTSPDTO(SanPham sp, ChiTietSanPham ctsp, AnhSanPham anh, BoNhoTrong bnt, MauSac ms, int soLuongTrongDotGiamGiaKhac) {
        this.sp = sp;
        this.ctsp = ctsp;
        this.anh = anh;
        this.bnt = bnt;
        this.ms = ms;
        this.soLuongTrongDotGiamGiaKhac = (long) soLuongTrongDotGiamGiaKhac;
    }

    public Long getSoLuongTrongDotGiamGiaKhac() {
        return soLuongTrongDotGiamGiaKhac;
    }

    public void setSoLuongTrongDotGiamGiaKhac(Long soLuongTrongDotGiamGiaKhac) {
        this.soLuongTrongDotGiamGiaKhac = soLuongTrongDotGiamGiaKhac;
    }

    public SanPham getSp() {
        return sp;
    }

    public void setSp(SanPham sp) {
        this.sp = sp;
    }

    public ChiTietSanPham getCtsp() {
        return ctsp;
    }

    public void setCtsp(ChiTietSanPham ctsp) {
        this.ctsp = ctsp;
    }

    public AnhSanPham getAnh() {
        return anh;
    }

    public void setAnh(AnhSanPham anh) {
        this.anh = anh;
    }

    public ChiTietDotGiamGia getCtgg() {
        return ctgg;
    }

    public void setCtgg(ChiTietDotGiamGia ctgg) {
        this.ctgg = ctgg;
    }

    public BoNhoTrong getBnt() {
        return bnt;
    }

    public void setBnt(BoNhoTrong bnt) {
        this.bnt = bnt;
    }

    public BigDecimal getGiaSauKhiGiam() {
        return giaSauKhiGiam;
    }

    public void setGiaSauKhiGiam(BigDecimal giaSauKhiGiam) {
        this.giaSauKhiGiam = giaSauKhiGiam;
    }

    public MauSac getMs() {
        return ms;
    }

    public void setMs(MauSac ms) {
        this.ms = ms;
    }

    public Boolean getSelected() {
        return selected;
    }

    // Thêm setter cho selected
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
