package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.RamRequest;
import com.example.be_datn.dto.product.response.RamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RamService {

    Page<RamResponse> getAllRam(Pageable pageable);

    List<RamResponse> getAllRamList();

    RamResponse getRamById(Integer id);

    RamResponse createRam(RamRequest request);

    RamResponse updateRam(Integer id, RamRequest request);

    void deleteRam(Integer id);

    Page<RamResponse> searchRam(String keyword, Pageable pageable);

    Page<RamResponse> filterByDungLuongRam(String dungLuongRam, Pageable pageable);

    List<String> getAllRamCapacities();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByDungLuongRam(String dungLuongRam, Integer excludeId);
}