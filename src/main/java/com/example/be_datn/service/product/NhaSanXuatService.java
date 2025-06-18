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

    // Xóa mềm nhà sản xuất
    void deleteNhaSanXuat(Integer id);

    // Tìm kiếm nhà sản xuất
    Page<NhaSanXuatResponse> searchNhaSanXuat(String keyword, Pageable pageable);

    // Lọc theo tên nhà sản xuất
    Page<NhaSanXuatResponse> filterByNhaSanXuat(String nhaSanXuat, Pageable pageable);

    // Lấy danh sách tên nhà sản xuất
    List<String> getAllManufacturerNames();

    // Kiểm tra mã nhà sản xuất đã tồn tại
    boolean existsByMa(String ma, Integer excludeId);

    // Kiểm tra tên nhà sản xuất đã tồn tại
    boolean existsByNhaSanXuat(String nhaSanXuat, Integer excludeId);
}