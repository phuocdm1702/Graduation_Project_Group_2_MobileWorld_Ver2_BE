package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.CongNgheMangRequest;
import com.example.be_datn.dto.product.response.CongNgheMangResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CongNgheMangService {

    Page<CongNgheMangResponse> getAllCongNgheMang(Pageable pageable);

    List<CongNgheMangResponse> getAllCongNgheMangList();

    CongNgheMangResponse getCongNgheMangById(Integer id);

    CongNgheMangResponse createCongNgheMang(CongNgheMangRequest request);

    CongNgheMangResponse updateCongNgheMang(Integer id, CongNgheMangRequest request);

    void deleteCongNgheMang(Integer id);

    Page<CongNgheMangResponse> searchCongNgheMang(String keyword, Pageable pageable);

    Page<CongNgheMangResponse> filterByTenCongNgheMang(String tenCongNgheMang, Pageable pageable);

    List<String> getAllTenCongNgheMangNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByTenCongNgheMang(String tenCongNgheMang, Integer excludeId);
}