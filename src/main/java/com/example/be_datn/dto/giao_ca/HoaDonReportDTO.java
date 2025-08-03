package com.example.be_datn.dto.giao_ca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonReportDTO {
    private String ma;
    private BigDecimal tongTien;
    private Short trangThai;
}
