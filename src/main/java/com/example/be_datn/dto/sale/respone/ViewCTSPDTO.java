package com.example.be_datn.dto.sale.respone;

import com.example.be_datn.entity.discount.ChiTietDotGiamGia;
import com.example.be_datn.entity.product.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    public ViewCTSPDTO(SanPham sp, ChiTietSanPham ctsp, AnhSanPham anh, BoNhoTrong bnt, MauSac ms, int soLuongTrongDotGiamGiaKhac) {
        this.sp = sp;
        this.ctsp = ctsp;
        this.anh = anh;
        this.bnt = bnt;
        this.ms = ms;
        this.soLuongTrongDotGiamGiaKhac = (long) soLuongTrongDotGiamGiaKhac;
    }
}
