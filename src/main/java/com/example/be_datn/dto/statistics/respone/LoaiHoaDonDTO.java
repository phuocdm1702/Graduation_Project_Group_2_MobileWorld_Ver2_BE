package com.example.be_datn.dto.statistics.respone;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LoaiHoaDonDTO {
    private String loaiDon;
    private Long soLuong;
}
