package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.ChiSoKhangBuiVaNuocRequest;
import com.example.be_datn.dto.product.response.ChiSoKhangBuiVaNuocResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChiSoKhangBuiVaNuocService {

    // Lấy tất cả chỉ số kháng bụi và nước với phân trang
    Page<ChiSoKhangBuiVaNuocResponse> getAllChiSoKhangBuiVaNuoc(Pageable pageable);

    // Lấy tất cả chỉ số kháng bụi và nước dạng list
    List<ChiSoKhangBuiVaNuocResponse> getAllChiSoKhangBuiVaNuocList();

    // Lấy chỉ số kháng bụi và nước theo ID
    ChiSoKhangBuiVaNuocResponse getChiSoKhangBuiVaNuocById(Integer id);

    // Tạo mới chỉ số kháng bụi và nước
    ChiSoKhangBuiVaNuocResponse createChiSoKhangBuiVaNuoc(ChiSoKhangBuiVaNuocRequest request);

    // Cập nhật chỉ số kháng bụi và nước
    ChiSoKhangBuiVaNuocResponse updateChiSoKhangBuiVaNuoc(Integer id, ChiSoKhangBuiVaNuocRequest request);

    // Tìm kiếm chỉ số kháng bụi và nước
    Page<ChiSoKhangBuiVaNuocResponse> searchChiSoKhangBuiVaNuoc(String keyword, Pageable pageable);

}