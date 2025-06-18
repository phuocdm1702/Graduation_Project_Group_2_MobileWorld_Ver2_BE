package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongNgheMangResponse {
    private Integer id;
    private String ma;
    private String tenCongNgheMang;
    private Boolean deleted;
}