package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.HoTroBoNhoNgoaiRequest;
import com.example.be_datn.dto.product.response.HoTroBoNhoNgoaiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HoTroBoNhoNgoaiService {

    // Lấy tất cả hỗ trợ bộ nhớ ngoài với phân trang
    Page<HoTroBoNhoNgoaiResponse> getAllHoTroBoNhoNgoai(Pageable pageable);

    // Lấy tất cả hỗ trợ bộ nhớ ngoài dạng list
    List<HoTroBoNhoNgoaiResponse> getAllHoTroBoNhoNgoaiList();

    // Lấy hỗ trợ bộ nhớ ngoài theo ID
    HoTroBoNhoNgoaiResponse getHoTroBoNhoNgoaiById(Integer id);

    // Tạo mới hỗ trợ bộ nhớ ngoài
    HoTroBoNhoNgoaiResponse createHoTroBoNhoNgoai(HoTroBoNhoNgoaiRequest request);

    // Cập nhật hỗ trợ bộ nhớ ngoài
    HoTroBoNhoNgoaiResponse updateHoTroBoNhoNgoai(Integer id, HoTroBoNhoNgoaiRequest request);

    // Tìm kiếm hỗ trợ bộ nhớ ngoài
    Page<HoTroBoNhoNgoaiResponse> searchHoTroBoNhoNgoai(String keyword, Pageable pageable);

}