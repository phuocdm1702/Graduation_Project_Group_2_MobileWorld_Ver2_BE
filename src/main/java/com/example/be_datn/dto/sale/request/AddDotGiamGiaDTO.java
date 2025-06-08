package com.example.be_datn.dto.sale.request;

import com.example.be_datn.dto.sale.respone.ViewCTSPDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddDotGiamGiaDTO {
    private DotGiamGiaDTO dotGiamGia;
    private List<Integer> idDSPs;
    private List<ViewCTSPDTO> ctspList;
}
