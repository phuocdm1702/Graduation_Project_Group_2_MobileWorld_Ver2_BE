package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CpuResponse {
    private Integer id;
    private String ma;
    private String tenCpu;
    private Integer soNhan;
    private Boolean deleted;
}