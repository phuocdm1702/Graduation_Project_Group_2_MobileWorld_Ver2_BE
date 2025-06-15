package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.ThietKeRequest;
import com.example.be_datn.dto.product.response.ThietKeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ThietKeService {

    Page<ThietKeResponse> getAllThietKe(Pageable pageable);

    List<ThietKeResponse> getAllThietKeList();

    ThietKeResponse getThietKeById(Integer id);

    ThietKeResponse createThietKe(ThietKeRequest request);

    ThietKeResponse updateThietKe(Integer id, ThietKeRequest request);

    void deleteThietKe(Integer id);

    Page<ThietKeResponse> searchThietKe(String keyword, Pageable pageable);

    Page<ThietKeResponse> filterByChatLieuKhung(String chatLieuKhung, Pageable pageable);

    List<String> getAllFrameMaterials();

    boolean existsByMa(String ma, Integer excludeId);
}