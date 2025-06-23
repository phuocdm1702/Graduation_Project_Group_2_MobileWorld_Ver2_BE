package com.example.be_datn.dto.product.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamResponse {
    private Integer id;
    private String tenSanPham;
    private Integer idNhaSanXuat;
    private Integer idPin;
    private Integer idCongNgheManHinh;
    private Integer idHoTroBoNhoNgoai;
    private Integer idCpu;
    private Integer idGpu;
    private Integer idCumCamera;
    private Integer idHeDieuHanh;
    private Integer idChiSoKhangBuiVaNuoc;
    private Integer idThietKe;
    private Integer idSim;
    private Integer idHoTroCongNgheSac;
    private Integer idCongNgheMang;
    private String ghiChu;
    private List<VariantResponse> variants;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantResponse {
        private Integer id;
        private Integer idMauSac;
        private Integer idRam;
        private Integer idBoNhoTrong;
        private BigDecimal donGia;
        private List<String> imeiList;
        private String imageUrl;
    }
}