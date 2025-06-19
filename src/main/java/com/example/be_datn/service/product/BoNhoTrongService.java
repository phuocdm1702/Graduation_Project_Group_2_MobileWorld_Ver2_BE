package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.BoNhoTrongRequest;
import com.example.be_datn.dto.product.response.BoNhoTrongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoNhoTrongService {

    // Lấy tất cả bộ nhớ trong với phân trang
    Page<BoNhoTrongResponse> getAllBoNhoTrong(Pageable pageable);

    // Lấy tất cả bộ nhớ trong dạng list
    List<BoNhoTrongResponse> getAllBoNhoTrongList();

    // Lấy bộ nhớ trong theo ID
    BoNhoTrongResponse getBoNhoTrongById(Integer id);

    // Tạo mới bộ nhớ trong
    BoNhoTrongResponse createBoNhoTrong(BoNhoTrongRequest request);

    // Cập nhật bộ nhớ trong
    BoNhoTrongResponse updateBoNhoTrong(Integer id, BoNhoTrongRequest request);

    // Xóa mềm bộ nhớ trong
    void deleteBoNhoTrong(Integer id);

    // Tìm kiếm bộ nhớ trong
    Page<BoNhoTrongResponse> searchBoNhoTrong(String keyword, Pageable pageable);

    // Lọc theo dung lượng bộ nhớ trong
    Page<BoNhoTrongResponse> filterByDungLuongBoNhoTrong(String dungLuongBoNhoTrong, Pageable pageable);

    // Lấy danh sách dung lượng bộ nhớ trong
    List<String> getAllStorageCapacities();

    // Kiểm tra mã bộ nhớ trong đã tồn tại
    boolean existsByMa(String ma, Integer excludeId);

    // Kiểm tra dung lượng bộ nhớ trong đã tồn tại
    boolean existsByDungLuongBoNhoTrong(String dungLuongBoNhoTrong, Integer excludeId);
}