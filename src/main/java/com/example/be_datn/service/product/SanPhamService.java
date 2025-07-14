// SanPhamService.java
package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.response.SanPhamResponse;
import com.example.be_datn.dto.product.request.SanPhamRequest;
import com.example.be_datn.entity.product.SanPham;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    public List<Object[]> findProductsWithLatestVariant(@Param("idNhaSanXuat") Integer idNhaSanXuat);

    public List<Object[]> showNewProduct(@Param("idNhaSanXuat") Integer idNhaSanXuat);

    public List<Object[]> showBestProduct(@Param("sortBy") String sortBy);

    public Page<Object[]> showAllProduct(Pageable pageable);

    public List<Object[]> suggestProductTop6();

    public Page<Object[]> showAllProduct(Pageable pageable,  String sortBy,String useCases, String colors, String brands, double minPrice, double maxPrice);

    public List<Object[]> getProductForCompare();

    public Optional<SanPham> findSanPhamWithDetailsById(Integer id);
}