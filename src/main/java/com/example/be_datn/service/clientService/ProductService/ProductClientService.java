package com.example.be_datn.service.clientService.ProductService;

import com.example.be_datn.entity.product.SanPham;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductClientService {
    public List<Object[]> findProductsWithLatestVariant(@Param("idNhaSanXuat") Integer idNhaSanXuat);

    public List<Object[]> showNewProduct(@Param("idNhaSanXuat") Integer idNhaSanXuat);

    public List<Object[]> showBestProduct(@Param("sortBy") String sortBy);

    public List<Object[]> suggestProductTop6();

    public Page<Object[]> showAllProduct(Pageable pageable,  String sortBy,String useCases, String colors, String brands, double minPrice, double maxPrice);

    public List<Object[]> getProductForCompare();

    public Optional<SanPham> findSanPhamWithDetailsById(Integer id);
}
