package com.example.be_datn.dto.sale.respone;

import com.example.be_datn.entity.product.HeDieuHanh;
import com.example.be_datn.entity.product.NhaSanXuat;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinedResponse {
    private List<ViewSanPhamDTO> spList;
    private List<ViewCTSPDTO> ctspList;
    private int totalPages;
    private int currentPageDSP;
    private long totalElements;
    private int totalPagesCTSP;
    private int currentPageCTSP;
    private long totalElementsCTSP;

    private List<HeDieuHanh> heDieuHanhList;
    private List<NhaSanXuat> nhaSanXuatList;
}
