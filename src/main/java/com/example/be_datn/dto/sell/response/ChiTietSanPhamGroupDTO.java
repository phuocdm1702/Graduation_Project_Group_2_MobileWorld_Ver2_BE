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
    private Integer idSanPham; // Thêm id để xác định nhóm sản phẩm duy nhất
    private String tenSanPham;
    private String ma;
    private String mauSac;
    private String dungLuongRam;
    private String dungLuongBoNhoTrong;
    private Integer soLuong;
    private BigDecimal giaBan;
}
