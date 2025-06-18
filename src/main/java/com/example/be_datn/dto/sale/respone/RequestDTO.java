package com.example.be_datn.dto.sale.respone;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDTO {
    private String keyword;
    private List<Integer> idDSPs;
    private List<Integer> idBoNhoTrongs;
    private List<Integer> mauSac;
    private List<Integer> idHeDieuHanh;
    private List<Integer> idNhaSanXuat;

}
