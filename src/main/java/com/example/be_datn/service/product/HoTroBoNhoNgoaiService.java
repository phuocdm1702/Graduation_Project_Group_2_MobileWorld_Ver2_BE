package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.HoTroBoNhoNgoaiRequest;
import com.example.be_datn.dto.product.response.HoTroBoNhoNgoaiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HoTroBoNhoNgoaiService {

    Page<HoTroBoNhoNgoaiResponse> getAllHoTroBoNhoNgoai(Pageable pageable);

    List<HoTroBoNhoNgoaiResponse> getAllHoTroBoNhoNgoaiList();

    HoTroBoNhoNgoaiResponse getHoTroBoNhoNgoaiById(Integer id);

    HoTroBoNhoNgoaiResponse createHoTroBoNhoNgoai(HoTroBoNhoNgoaiRequest request);

    HoTroBoNhoNgoaiResponse updateHoTroBoNhoNgoai(Integer id, HoTroBoNhoNgoaiRequest request);

    void deleteHoTroBoNhoNgoai(Integer id);

    Page<HoTroBoNhoNgoaiResponse> searchHoTroBoNhoNgoai(String keyword, Pageable pageable);

    Page<HoTroBoNhoNgoaiResponse> filterByHoTroBoNhoNgoai(String hoTroBoNhoNgoai, Pageable pageable);

    List<String> getAllHoTroBoNhoNgoaiNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByHoTroBoNhoNgoai(String hoTroBoNhoNgoai, Integer excludeId);
}