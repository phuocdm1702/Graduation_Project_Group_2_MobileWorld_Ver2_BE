package com.example.be_datn.dto.giao_ca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiaoCaDTO {
    private Integer id;
    private String tenNhanVien;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private BigDecimal tienMatBanDau;
    private BigDecimal tienMatCuoiCa;
    private BigDecimal tongTienMat;
    private BigDecimal tongTienChuyenKhoan;
    private BigDecimal tongDoanhThu;
    private List<HoaDonReportDTO> hoaDons;
}
