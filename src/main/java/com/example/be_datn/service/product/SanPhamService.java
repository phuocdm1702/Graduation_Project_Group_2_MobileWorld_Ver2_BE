package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.response.SanPhamResponse;
import com.example.be_datn.dto.product.request.SanPhamRequest;
import com.example.be_datn.entity.product.SanPham;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface SanPhamService {
    Page<SanPhamResponse> getAllSanPham(int page, int size);

    List<SanPhamResponse> getAllSanPhamList();

    Page<SanPhamResponse> searchSanPham(
            String keyword,
            Integer idNhaSanXuat,
            Integer idHeDieuHanh,
            String heDieuHanh,
            String phienBan,
            Integer idCongNgheManHinh,
            String congNgheManHinh,
            String chuanManHinh,
            Integer idPin,
            String loaiPin,
            String dungLuongPin,
            Integer idCpu,
            Integer idGpu,
            Integer idCumCamera,
            Integer idThietKe,
            Integer idSim,
            Integer idHoTroCongNgheSac,
            Integer idCongNgheMang,
            Boolean inStock,
            int page,
            int size);

    Optional<SanPham> getSanPhamById(Integer id);

    SanPham createSanPham(SanPhamRequest requestDto);

    SanPham updateSanPham(Integer id, SanPhamRequest requestDto);

    SanPhamResponse mapToDTO(SanPham sanPham);

    Long countChiTietSanPhamBySanPhamId(Integer sanPhamId);
}