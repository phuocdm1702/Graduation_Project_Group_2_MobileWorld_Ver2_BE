package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.PinRequest;
import com.example.be_datn.dto.product.response.PinResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PinService {

    Page<PinResponse> getAllPin(Pageable pageable);

    List<PinResponse> getAllPinList();

    PinResponse getPinById(Integer id);

    PinResponse createPin(PinRequest request);

    PinResponse updatePin(Integer id, PinRequest request);

    void deletePin(Integer id);

    Page<PinResponse> searchPin(String keyword, Pageable pageable);

    Page<PinResponse> filterByLoaiPin(String loaiPin, Pageable pageable);

    List<String> getAllLoaiPinNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByLoaiPin(String loaiPin, Integer excludeId);
}