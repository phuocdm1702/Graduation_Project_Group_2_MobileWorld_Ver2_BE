package com.example.be_datn.dto.sale.respone;

import com.example.be_datn.entity.product.HeDieuHanh;
import com.example.be_datn.entity.product.NhaSanXuat;
import com.example.be_datn.entity.product.SanPham;


public class ViewSanPhamDTO {
    SanPham sp;
    NhaSanXuat nsx;
    HeDieuHanh hdh;
    long soLuongCTSP;

    public SanPham getSp() {
        return sp;
    }

    public void setSp(SanPham sp) {
        this.sp = sp;
    }

    public NhaSanXuat getNsx() {
        return nsx;
    }

    public void setNsx(NhaSanXuat nsx) {
        this.nsx = nsx;
    }

    public HeDieuHanh getHdh() {
        return hdh;
    }

    public void setHdh(HeDieuHanh hdh) {
        this.hdh = hdh;
    }

    public long getSoLuongCTSP() {
        return soLuongCTSP;
    }

    public void setSoLuongCTSP(long soLuongCTSP) {
        this.soLuongCTSP = soLuongCTSP;
    }

    public ViewSanPhamDTO() {
    }

    public ViewSanPhamDTO(SanPham sp, NhaSanXuat nsx, HeDieuHanh hdh, long soLuongCTSP) {
        this.sp = sp;
        this.nsx = nsx;
        this.hdh = hdh;
        this.soLuongCTSP = soLuongCTSP;
    }
}
