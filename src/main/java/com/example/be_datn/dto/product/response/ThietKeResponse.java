package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThietKeResponse {
    private Integer id;
    private String ma;
    private String chatLieuKhung;
    private String chatLieuMatLung;
    private Boolean deleted;
}