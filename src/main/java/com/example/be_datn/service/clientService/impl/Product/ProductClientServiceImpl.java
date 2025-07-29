package com.example.be_datn.service.clientService.impl.Product;

import com.example.be_datn.entity.product.SanPham;
import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.repository.product.SanPhamRepository;
import com.example.be_datn.service.clientService.ProductService.ProductClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductClientServiceImpl implements ProductClientService {
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Override
    public List<Object[]> findProductsWithLatestVariant(Integer idNhaSanXuat) {
        return sanPhamRepository.findProductsWithLatestVariant(idNhaSanXuat);
    }

    @Override
    public List<Object[]> showNewProduct(Integer idNhaSanXuat) {
        return sanPhamRepository.showNewProduct(idNhaSanXuat);
    }

    @Override
    public List<Object[]> showBestProduct(String sortBy) {
        return sanPhamRepository.showBestProduct(sortBy);
    }

    @Override
    public List<Object[]> suggestProductTop6() {
        return sanPhamRepository.suggestProductTop6();
    }

    @Override
    public Page<Object[]> showAllProduct(Pageable pageable, String sortBy, String useCases, String colors, String brands, double minPrice, double maxPrice) {
        return sanPhamRepository.showAllProduct(pageable, sortBy,useCases, colors, brands, minPrice, maxPrice);
    }

    @Override
    public List<Object[]> getProductForCompare() {
        return sanPhamRepository.getProductForCompare();
    }

    @Override
    public Optional<SanPham> findSanPhamWithDetailsById(Integer id) {
        return sanPhamRepository.findSanPhamWithDetailsById(id);
    }
}
