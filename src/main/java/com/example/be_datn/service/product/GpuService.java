package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.GpuRequest;
import com.example.be_datn.dto.product.response.GpuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GpuService {

    // Lấy tất cả GPU với phân trang
    Page<GpuResponse> getAllGpu(Pageable pageable);

    // Lấy tất cả GPU dạng list
    List<GpuResponse> getAllGpuList();

    // Lấy GPU theo ID
    GpuResponse getGpuById(Integer id);

    // Tạo mới GPU
    GpuResponse createGpu(GpuRequest request);

    // Cập nhật GPU
    GpuResponse updateGpu(Integer id, GpuRequest request);

    // Tìm kiếm GPU
    Page<GpuResponse> searchGpu(String keyword, Pageable pageable);

}