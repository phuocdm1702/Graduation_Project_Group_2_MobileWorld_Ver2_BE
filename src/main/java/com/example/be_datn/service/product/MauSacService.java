package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.MauSacRequest;
import com.example.be_datn.dto.product.response.MauSacResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MauSacService {

    Page<MauSacResponse> getAllMauSac(Pageable pageable);

    List<MauSacResponse> getAllMauSacList();

    MauSacResponse getMauSacById(Integer id);

    MauSacResponse createMauSac(MauSacRequest request);

    MauSacResponse updateMauSac(Integer id, MauSacRequest request);

    void deleteMauSac(Integer id);

    Page<MauSacResponse> searchMauSac(String keyword, Pageable pageable);

    Page<MauSacResponse> filterByMauSac(String mauSac, Pageable pageable);

    List<String> getAllColorNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByMauSac(String mauSac, Integer excludeId);
}