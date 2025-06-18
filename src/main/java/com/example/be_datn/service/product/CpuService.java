package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.CpuRequest;
import com.example.be_datn.dto.product.response.CpuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CpuService {

    Page<CpuResponse> getAllCpu(Pageable pageable);

    List<CpuResponse> getAllCpuList();

    CpuResponse getCpuById(Integer id);

    CpuResponse createCpu(CpuRequest request);

    CpuResponse updateCpu(Integer id, CpuRequest request);

    void deleteCpu(Integer id);

    Page<CpuResponse> searchCpu(String keyword, Pageable pageable);

    Page<CpuResponse> filterByTenCpu(String tenCpu, Pageable pageable);

    List<String> getAllTenCpuNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByTenCpu(String tenCpu, Integer excludeId);
}