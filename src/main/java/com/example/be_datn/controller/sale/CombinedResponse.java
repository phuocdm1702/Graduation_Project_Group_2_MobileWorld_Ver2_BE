package com.example.be_datn.controller.sale;

import com.example.be_datn.dto.sale.respone.ViewCTSPDTO;
import com.example.be_datn.dto.sale.respone.ViewSanPhamDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CombinedResponse {
    private List<ViewSanPhamDTO> spList;
    private List<ViewCTSPDTO> ctspList;
    private int totalPages;
    private int currentPageDSP;
    private long totalElements;
    private int totalPagesCTSP;
    private int currentPageCTSP;
    private long totalElementsCTSP;
}
