package com.example.be_datn.dto.statistics.respone;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SoLieuDTO {
    private BigDecimal doanhThu;
    private Long sanPhamDaBan;
    private Integer tongSoDonHang;
}
