package com.example.be_datn.dto.order.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonResponse {
    private Integer id;
    private String ma;
    private String maNhanVien;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private BigDecimal tongTienSauGiam;
    private BigDecimal phiVanChuyen;
    private Date ngayTao;
    private String loaiDon;
    private Short trangThai;
    private Boolean deleted;

}
