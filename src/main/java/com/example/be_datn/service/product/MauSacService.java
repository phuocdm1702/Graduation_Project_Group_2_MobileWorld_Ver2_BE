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

    Page<MauSacResponse> searchMauSac(String keyword, Pageable pageable);
}