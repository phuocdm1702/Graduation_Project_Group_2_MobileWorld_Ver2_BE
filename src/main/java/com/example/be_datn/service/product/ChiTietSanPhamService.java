package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ChiTietSanPhamService {
    ChiTietSanPhamResponse createChiTietSanPham(ChiTietSanPhamRequest dto, List<MultipartFile> images) throws IOException;

    void updateChiTietSanPham(Integer id, ChiTietSanPhamRequest dto);

    void updatePrice(Integer id, BigDecimal newPrice);

    List<ChiTietSanPhamResponse> getChiTietSanPhamBySanPhamId(Integer sanPhamId);

    Page<ChiTietSanPhamResponse> getChiTietSanPhamDetails(Integer sanPhamId, String keyword, String status,
                                                          Integer idMauSac, Integer idBoNhoTrong, Integer idRam,
                                                          BigDecimal minPrice, BigDecimal maxPrice, int page, int size);

    Map<String, BigDecimal> getPriceRange(Integer sanPhamId);
}