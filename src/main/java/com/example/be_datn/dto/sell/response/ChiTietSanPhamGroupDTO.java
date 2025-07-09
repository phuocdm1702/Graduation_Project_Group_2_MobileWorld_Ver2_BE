package com.example.be_datn.dto.sell.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPhamGroupDTO {
    private Integer idSanPham; // ID của chi tiết sản phẩm
    private String tenSanPham; // Tên sản phẩm
    private String ma; // Mã sản phẩm
    private String mauSac; // Màu sắc
    private String dungLuongRam; // Dung lượng RAM
    private String dungLuongBoNhoTrong; // Dung lượng bộ nhớ trong
    private Integer soLuong; // Số lượng sản phẩm khả dụng (tính từ ChiTietSanPham)
    private BigDecimal giaBan; // Giá hiện tại (giá sau giảm nếu có)
    private BigDecimal giaBanGoc; // Giá gốc (giá sau giảm trong đợt giảm giá)
    private BigDecimal giaBanBanDau; // Giá ban đầu từ ChiTietSanPham
}
