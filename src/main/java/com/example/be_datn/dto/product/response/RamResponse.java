package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamResponse {
    private Integer id;
    private String ma;
    private String dungLuongRam;
    private Boolean deleted;
}