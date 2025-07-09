package com.example.be_datn.service.product;

import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamDetailResponse;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChiTietSanPhamService {
    ChiTietSanPhamResponse createChiTietSanPham(ChiTietSanPhamRequest request, List<MultipartFile> images, List<String> existingImageUrls);

    ChiTietSanPhamResponse updateChiTietSanPham(Integer id, ChiTietSanPhamRequest request, List<MultipartFile> images, List<String> existingImageUrls);

    List<ChiTietSanPhamDetailResponse> getProductDetailsBySanPhamId(Integer idSanPham);
}