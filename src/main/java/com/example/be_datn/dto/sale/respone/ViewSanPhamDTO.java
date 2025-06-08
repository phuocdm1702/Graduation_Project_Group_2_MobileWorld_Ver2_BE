package com.example.be_datn.dto.sale.respone;

import com.example.be_datn.entity.product.HeDieuHanh;
import com.example.be_datn.entity.product.NhaSanXuat;
import com.example.be_datn.entity.product.SanPham;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewSanPhamDTO {
    SanPham sp;
    NhaSanXuat nsx;
    HeDieuHanh hdh;
    long soLuongCTSP;
}
