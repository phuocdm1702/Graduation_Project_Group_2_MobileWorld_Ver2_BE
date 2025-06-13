package com.example.be_datn.dto.sale.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DotGiamGiaDTO {
    private Integer id;
    private String ma;
    private String tenDotGiamGia;
    private String loaiGiamGiaApDung;
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Boolean trangThai = false;
    private Boolean deleted = false;
}
