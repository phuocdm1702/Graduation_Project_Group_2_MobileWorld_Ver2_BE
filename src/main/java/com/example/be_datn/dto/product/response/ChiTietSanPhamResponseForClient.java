package com.example.be_datn.dto.product.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamResponseForClient {
    private Integer id;
    private String ma;
    private BigDecimal giaBan;
    private String ghiChu;
    private Boolean deleted;
    private Date createdAt;
    private Integer createdBy;
    private Date updatedAt;
    private Integer updatedBy;

    private Integer idSanPham;
    private String tenSanPham;
    private String maImel;
    private String mauSac;
    private String ram;
    private String boNhoTrong;
    private String duongDanAnh;
}
