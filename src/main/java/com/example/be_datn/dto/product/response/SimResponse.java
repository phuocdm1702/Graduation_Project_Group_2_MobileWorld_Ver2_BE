package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimResponse {
    private Integer id;
    private String ma;
    private Integer soLuongSimHoTro;
    private String cacLoaiSimHoTro;
    private Boolean deleted;
}