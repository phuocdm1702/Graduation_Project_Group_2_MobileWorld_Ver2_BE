package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoTroCongNgheSacResponse {
    private Integer id;
    private String ma;
    private String congSac;
    private String congNgheHoTro;
    private Boolean deleted;
}