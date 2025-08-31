package com.example.be_datn.dto.order.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietImeiResponse {
    private Integer id;
    private String ma;
    private BigDecimal gia;
    private Short trangThai;
    private String ghiChu;
    
    // Thông tin sản phẩm
    private Integer sanPhamId;
    private String tenSanPham;
    private String anhSanPham;
    private String thuongHieu;
    
    // Thông tin chi tiết sản phẩm
    private Integer chiTietSanPhamId;
    private String ram;
    private String boNhoTrong;
    private String mauSac;
    private BigDecimal giaBan;
    
    // Thông tin IMEI
    private String imei;
    private Date ngayBan;
    private String ghiChuImei;
    
    // Thông tin hóa đơn
    private Integer hoaDonId;
    private String maHoaDon;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
}
