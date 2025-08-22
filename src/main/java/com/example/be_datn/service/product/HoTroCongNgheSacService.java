package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.HoTroCongNgheSacRequest;
import com.example.be_datn.dto.product.response.HoTroCongNgheSacResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HoTroCongNgheSacService {

    // Lấy tất cả hỗ trợ công nghệ sạc với phân trang
    Page<HoTroCongNgheSacResponse> getAllHoTroCongNgheSac(Pageable pageable);

    // Lấy tất cả hỗ trợ công nghệ sạc dạng list
    List<HoTroCongNgheSacResponse> getAllHoTroCongNgheSacList();

    // Lấy hỗ trợ công nghệ sạc theo ID
    HoTroCongNgheSacResponse getHoTroCongNgheSacById(Integer id);

    // Tạo mới hỗ trợ công nghệ sạc
    HoTroCongNgheSacResponse createHoTroCongNgheSac(HoTroCongNgheSacRequest request);

    // Cập nhật hỗ trợ công nghệ sạc
    HoTroCongNgheSacResponse updateHoTroCongNgheSac(Integer id, HoTroCongNgheSacRequest request);

    // Tìm kiếm hỗ trợ công nghệ sạc
    Page<HoTroCongNgheSacResponse> searchHoTroCongNgheSac(String keyword, Pageable pageable);

}