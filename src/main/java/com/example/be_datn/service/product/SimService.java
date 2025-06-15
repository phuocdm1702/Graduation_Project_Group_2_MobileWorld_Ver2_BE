package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.SimRequest;
import com.example.be_datn.dto.product.response.SimResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SimService {

    Page<SimResponse> getAllSim(Pageable pageable);

    List<SimResponse> getAllSimList();

    SimResponse getSimById(Integer id);

    SimResponse createSim(SimRequest request);

    SimResponse updateSim(Integer id, SimRequest request);

    void deleteSim(Integer id);

    Page<SimResponse> searchSim(String keyword, Pageable pageable);

    Page<SimResponse> filterBySoLuongSimHoTro(Integer soLuongSimHoTro, Pageable pageable);

    List<String> getAllSimTypes();

    boolean existsByMa(String ma, Integer excludeId);
}