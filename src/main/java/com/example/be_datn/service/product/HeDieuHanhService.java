package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.HeDieuHanhRequest;
import com.example.be_datn.dto.product.response.HeDieuHanhResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HeDieuHanhService {

    Page<HeDieuHanhResponse> getAllHeDieuHanh(Pageable pageable);

    List<HeDieuHanhResponse> getAllHeDieuHanhList();

    HeDieuHanhResponse getHeDieuHanhById(Integer id);

    HeDieuHanhResponse createHeDieuHanh(HeDieuHanhRequest request);

    HeDieuHanhResponse updateHeDieuHanh(Integer id, HeDieuHanhRequest request);

    Page<HeDieuHanhResponse> searchHeDieuHanh(String keyword, Pageable pageable);
}