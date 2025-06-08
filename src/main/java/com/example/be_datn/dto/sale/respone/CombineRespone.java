package com.example.be_datn.dto.sale.respone;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombineRespone {
    private List<ViewSanPhamDTO> spList;
    private List<ViewCTSPDTO> ctspList;
    private int totalPages;
    private int currentPageDSP;
    private long totalElements;
    private int totalPagesCTSP;
    private int currentPageCTSP;
    private long totalElementsCTSP;
}
