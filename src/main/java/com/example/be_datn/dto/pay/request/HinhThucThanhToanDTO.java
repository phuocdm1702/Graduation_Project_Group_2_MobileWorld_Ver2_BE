package com.example.be_datn.dto.pay.request;

import java.math.BigDecimal;

public class HinhThucThanhToanDTO {
    private Integer phuongThucThanhToanId; // ID của PhuongThucThanhToan
    private BigDecimal tienMat;
    private BigDecimal tienChuyenKhoan;

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
