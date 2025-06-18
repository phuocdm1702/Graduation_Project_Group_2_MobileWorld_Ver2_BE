package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinResponse {
    private Integer id;
    private String ma;
    private String loaiPin;
    private String dungLuongPin;
    private Boolean deleted;
}