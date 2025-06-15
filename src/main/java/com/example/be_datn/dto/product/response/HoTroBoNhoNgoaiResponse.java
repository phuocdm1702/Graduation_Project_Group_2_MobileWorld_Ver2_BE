package com.example.be_datn.dto.product.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoTroBoNhoNgoaiResponse {
    private Integer id;
    private String ma;
    private String hoTroBoNhoNgoai;
    private Boolean deleted;
}