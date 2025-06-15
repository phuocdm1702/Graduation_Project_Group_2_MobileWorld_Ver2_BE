package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.CongNgheManHinhRequest;
import com.example.be_datn.dto.product.response.CongNgheManHinhResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CongNgheManHinhService {

    Page<CongNgheManHinhResponse> getAllCongNgheManHinh(Pageable pageable);

    List<CongNgheManHinhResponse> getAllCongNgheManHinhList();

    CongNgheManHinhResponse getCongNgheManHinhById(Integer id);

    CongNgheManHinhResponse createCongNgheManHinh(CongNgheManHinhRequest request);

    CongNgheManHinhResponse updateCongNgheManHinh(Integer id, CongNgheManHinhRequest request);

    void deleteCongNgheManHinh(Integer id);

    Page<CongNgheManHinhResponse> searchCongNgheManHinh(String keyword, Pageable pageable);

    Page<CongNgheManHinhResponse> filterByCongNgheManHinh(String congNgheManHinh, Pageable pageable);

    List<String> getAllCongNgheManHinhNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByCongNgheManHinh(String congNgheManHinh, Integer excludeId);
}