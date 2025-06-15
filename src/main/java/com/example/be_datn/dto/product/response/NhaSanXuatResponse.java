package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhaSanXuatResponse {
    private Integer id;
    private String ma;
    private String nhaSanXuat;
    private Boolean deleted;
}
