package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.HoTroCongNgheSacRequest;
import com.example.be_datn.dto.product.response.HoTroCongNgheSacResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HoTroCongNgheSacService {

    Page<HoTroCongNgheSacResponse> getAllHoTroCongNgheSac(Pageable pageable);

    List<HoTroCongNgheSacResponse> getAllHoTroCongNgheSacList();

    HoTroCongNgheSacResponse getHoTroCongNgheSacById(Integer id);

    HoTroCongNgheSacResponse createHoTroCongNgheSac(HoTroCongNgheSacRequest request);

    HoTroCongNgheSacResponse updateHoTroCongNgheSac(Integer id, HoTroCongNgheSacRequest request);

    void deleteHoTroCongNgheSac(Integer id);

    Page<HoTroCongNgheSacResponse> searchHoTroCongNgheSac(String keyword, Pageable pageable);

    Page<HoTroCongNgheSacResponse> filterByCongSac(String congSac, Pageable pageable);

    List<String> getAllCongSacNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByCongSac(String congSac, Integer excludeId);
}