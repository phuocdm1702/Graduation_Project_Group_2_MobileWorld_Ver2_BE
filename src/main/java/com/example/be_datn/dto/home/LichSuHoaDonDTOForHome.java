package com.example.be_datn.dto.home;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LichSuHoaDonDTOForHome {
    private Integer id;
    private String maHoaDon;
    private String moTa;
    private Instant thoiGian;
    private Short trangThai;
}
