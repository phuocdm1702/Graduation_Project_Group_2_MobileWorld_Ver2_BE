package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpuResponse {
    private Integer id;
    private String ma;
    private String tenGpu;
    private Boolean deleted;
}