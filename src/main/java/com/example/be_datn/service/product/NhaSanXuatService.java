package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.NhaSanXuatRequest;
import com.example.be_datn.dto.product.response.NhaSanXuatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NhaSanXuatService {

    // Lấy tất cả nhà sản xuất với phân trang
    Page<NhaSanXuatResponse> getAllNhaSanXuat(Pageable pageable);

    // Lấy tất cả nhà sản xuất dạng list
    List<NhaSanXuatResponse> getAllNhaSanXuatList();

    // Lấy nhà sản xuất theo ID
    NhaSanXuatResponse getNhaSanXuatById(Integer id);

    // Tạo mới nhà sản xuất
    NhaSanXuatResponse createNhaSanXuat(NhaSanXuatRequest request);

    // Cập nhật nhà sản xuất
    NhaSanXuatResponse updateNhaSanXuat(Integer id, NhaSanXuatRequest request);

    // Tìm kiếm nhà sản xuất
    Page<NhaSanXuatResponse> searchNhaSanXuat(String keyword, Pageable pageable);

}