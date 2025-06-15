package com.example.be_datn.dto.product.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongNgheManHinhResponse {
    private Integer id;
    private String ma;
    private String congNgheManHinh;
    private String chuanManHinh;
    private String kichThuoc;
    private String doPhanGiai;
    private String doSangToiDa;
    private String tanSoQuet;
    private String kieuManHinh;
    private Boolean deleted;
}