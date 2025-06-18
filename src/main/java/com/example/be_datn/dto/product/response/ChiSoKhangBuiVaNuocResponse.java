package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiSoKhangBuiVaNuocResponse {
    private Integer id;
    private String ma;
    private String tenChiSo;
    private Boolean deleted;
}