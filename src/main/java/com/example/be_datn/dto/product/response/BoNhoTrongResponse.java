package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoNhoTrongResponse {
    private Integer id;
    private String ma;
    private String dungLuongBoNhoTrong;
    private Boolean deleted;
}