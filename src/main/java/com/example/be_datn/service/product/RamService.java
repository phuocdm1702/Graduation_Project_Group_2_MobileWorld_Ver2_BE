package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.RamRequest;
import com.example.be_datn.dto.product.response.RamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RamService {

    // Lấy tất cả RAM với phân trang
    Page<RamResponse> getAllRam(Pageable pageable);

    // Lấy tất cả RAM dạng list
    List<RamResponse> getAllRamList();

    // Lấy RAM theo ID
    RamResponse getRamById(Integer id);

    // Tạo mới RAM
    RamResponse createRam(RamRequest request);

    // Cập nhật RAM
    RamResponse updateRam(Integer id, RamRequest request);

    // Tìm kiếm RAM
    Page<RamResponse> searchRam(String keyword, Pageable pageable);

}