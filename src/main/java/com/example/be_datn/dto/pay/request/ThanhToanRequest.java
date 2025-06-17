package com.example.be_datn.dto.pay.request;

import java.math.BigDecimal;

public class ThanhToanRequest {
    private Integer phuongThucThanhToanId;
    private BigDecimal tienMat;
    private BigDecimal tienChuyenKhoan;

    public ThanhToanRequest() {
    }

    public Integer getPhuongThucThanhToanId() {
        return phuongThucThanhToanId;
    }

    public void setPhuongThucThanhToanId(Integer phuongThucThanhToanId) {
        this.phuongThucThanhToanId = phuongThucThanhToanId;
    }

    public BigDecimal getTienMat() {
        return tienMat;
    }

    public void setTienMat(BigDecimal tienMat) {
        this.tienMat = tienMat;
    }

    public BigDecimal getTienChuyenKhoan() {
        return tienChuyenKhoan;
    }

    public void setTienChuyenKhoan(BigDecimal tienChuyenKhoan) {
        this.tienChuyenKhoan = tienChuyenKhoan;
    }
}
