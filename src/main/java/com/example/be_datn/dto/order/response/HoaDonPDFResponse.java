package com.example.be_datn.dto.order.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonPDFResponse {
    private Integer id;
    private String ma;
    private String tenNhanVien;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private String diaChiKhachHang;
    private BigDecimal tongTienSauGiam;
    private BigDecimal phiVanChuyen;
    private Date ngayTao;
    private String loaiDon;
    private Short trangThai;
    private Boolean deleted;
}
