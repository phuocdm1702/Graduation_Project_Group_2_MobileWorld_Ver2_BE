package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeDieuHanhResponse {
    private Integer id;
    private String ma;
    private String heDieuHanh;
    private String phienBan;
    private Boolean deleted;
}