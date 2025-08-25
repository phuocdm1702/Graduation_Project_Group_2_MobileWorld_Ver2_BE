package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.CongNgheMangRequest;
import com.example.be_datn.dto.product.response.CongNgheMangResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CongNgheMangService {

    // Lấy tất cả công nghệ mạng với phân trang
    Page<CongNgheMangResponse> getAllCongNgheMang(Pageable pageable);

    // Lấy tất cả công nghệ mạng dạng list
    List<CongNgheMangResponse> getAllCongNgheMangList();

    // Lấy công nghệ mạng theo ID
    CongNgheMangResponse getCongNgheMangById(Integer id);

    // Tạo mới công nghệ mạng
    CongNgheMangResponse createCongNgheMang(CongNgheMangRequest request);

    // Cập nhật công nghệ mạng
    CongNgheMangResponse updateCongNgheMang(Integer id, CongNgheMangRequest request);

    // Tìm kiếm công nghệ mạng
    Page<CongNgheMangResponse> searchCongNgheMang(String keyword, Pageable pageable);
}