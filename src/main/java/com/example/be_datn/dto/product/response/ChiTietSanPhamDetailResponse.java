package com.example.be_datn.dto.product.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamDetailResponse {
    private String tenSanPham;
    private String maSanPham;
    private String imei;
    private String mauSac;
    private String dungLuongRam;
    private String dungLuongBoNhoTrong;
    private BigDecimal donGia;
    private Boolean deleted;
    private String imageUrl; // Thêm trường imageUrl
}