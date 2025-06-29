package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSacResponse {
    private Integer id;
    private String ma;
    private String mauSac;
    private String maMau; // Thêm cột mã màu
    private Boolean deleted;
}