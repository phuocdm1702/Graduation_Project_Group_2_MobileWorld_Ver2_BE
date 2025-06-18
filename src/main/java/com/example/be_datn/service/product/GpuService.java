package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.GpuRequest;
import com.example.be_datn.dto.product.response.GpuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GpuService {

    Page<GpuResponse> getAllGpu(Pageable pageable);

    List<GpuResponse> getAllGpuList();

    GpuResponse getGpuById(Integer id);

    GpuResponse createGpu(GpuRequest request);

    GpuResponse updateGpu(Integer id, GpuRequest request);

    void deleteGpu(Integer id);

    Page<GpuResponse> searchGpu(String keyword, Pageable pageable);

    Page<GpuResponse> filterByTenGpu(String tenGpu, Pageable pageable);

    List<String> getAllTenGpuNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByTenGpu(String tenGpu, Integer excludeId);
}