package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.response.SanPhamResponseDto;
import com.example.be_datn.dto.product.request.SanPhamRequestDto;
import com.example.be_datn.entity.product.SanPham;
import org.springframework.data.domain.Page;
import java.util.Optional;

public interface SanPhamService {
    Page<SanPhamResponseDto> getAllSanPham(int page, int size);

    Page<SanPhamResponseDto> searchSanPham(
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

    SanPham createSanPham(SanPhamRequestDto requestDto);

    SanPham updateSanPham(Integer id, SanPhamRequestDto requestDto);

    SanPhamResponseDto mapToDTO(SanPham sanPham);

    Long countChiTietSanPhamBySanPhamId(Integer sanPhamId);
}