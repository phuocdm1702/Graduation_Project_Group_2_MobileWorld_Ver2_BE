package com.example.be_datn.dto.statistics.respone;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SanPhamHetHangDTO {
    private String tenSanPham;
    private Long soLuong;
}
