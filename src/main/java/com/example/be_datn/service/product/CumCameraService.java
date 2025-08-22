package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.CumCameraRequest;
import com.example.be_datn.dto.product.response.CumCameraResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CumCameraService {

    Page<CumCameraResponse> getAllCumCamera(Pageable pageable);

    List<CumCameraResponse> getAllCumCameraList();

    CumCameraResponse getCumCameraById(Integer id);

    CumCameraResponse createCumCamera(CumCameraRequest request);

    CumCameraResponse updateCumCamera(Integer id, CumCameraRequest request);

    Page<CumCameraResponse> searchCumCamera(String keyword, Pageable pageable);
}