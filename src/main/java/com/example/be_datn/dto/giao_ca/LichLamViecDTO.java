package com.example.be_datn.dto.giao_ca;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LichLamViecDTO {
    private Integer id;
    private Integer idNhanVien;
    private String caLam;
    private LocalDate ngayLam;
    private Boolean deleted;
}
