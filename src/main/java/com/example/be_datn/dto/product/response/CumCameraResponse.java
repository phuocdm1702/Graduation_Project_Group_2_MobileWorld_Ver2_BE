package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CumCameraResponse {
    private Integer id;
    private String ma;
    private String thongSoCameraSau;
    private String thongSoCameraTruoc;
    private Boolean deleted;
}