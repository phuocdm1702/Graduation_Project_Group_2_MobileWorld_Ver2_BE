package com.example.be_datn.dto.product.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ChiTietSanPhamRequest {
    private Integer idSanPham;
    private String maSanPham;
    private Integer idNhaSanXuat;
    private Integer idPin;
    private Integer congNgheManHinh;
    private Integer idHoTroBoNhoNgoai;
    private Integer idCpu;
    private Integer idGpu;
    private Integer idCumCamera;
    private Integer idHeDieuHanh;
    private Integer idChiSoKhangBuiVaNuoc;
    private Integer idThietKe;
    private Integer idSim;
    private Integer hoTroCongNgheSac;
    private Integer idCongNgheMang;
    private String tenSanPham;
    private String ghiChu;
    private BigDecimal giaBan;
    private Date createdAt;
    private Integer createdBy;
    private Date updatedAt;
    private Integer updatedBy;
    private List<VariantRequestDTO> variants;

    @Getter
    @Setter
    public static class VariantRequestDTO {
        private Integer idMauSac;
        private Integer idRam;
        private Integer idBoNhoTrong;
        private BigDecimal donGia;
        private Integer imageIndex;
        private List<String> imeiList;

        // Thêm kiểm tra hợp lệ
        public void setDonGia(BigDecimal donGia) {
            if (donGia != null && donGia.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
            }
            this.donGia = donGia;
        }
    }
}