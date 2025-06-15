package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.ChiSoKhangBuiVaNuocRequest;
import com.example.be_datn.dto.product.response.ChiSoKhangBuiVaNuocResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChiSoKhangBuiVaNuocService {

    Page<ChiSoKhangBuiVaNuocResponse> getAllChiSoKhangBuiVaNuoc(Pageable pageable);

    List<ChiSoKhangBuiVaNuocResponse> getAllChiSoKhangBuiVaNuocList();

    ChiSoKhangBuiVaNuocResponse getChiSoKhangBuiVaNuocById(Integer id);

    ChiSoKhangBuiVaNuocResponse createChiSoKhangBuiVaNuoc(ChiSoKhangBuiVaNuocRequest request);

    ChiSoKhangBuiVaNuocResponse updateChiSoKhangBuiVaNuoc(Integer id, ChiSoKhangBuiVaNuocRequest request);

    void deleteChiSoKhangBuiVaNuoc(Integer id);

    Page<ChiSoKhangBuiVaNuocResponse> searchChiSoKhangBuiVaNuoc(String keyword, Pageable pageable);

    Page<ChiSoKhangBuiVaNuocResponse> filterByTenChiSo(String tenChiSo, Pageable pageable);

    List<String> getAllTenChiSoNames();

    boolean existsByMa(String ma, Integer excludeId);

    boolean existsByTenChiSo(String tenChiSo, Integer excludeId);
}