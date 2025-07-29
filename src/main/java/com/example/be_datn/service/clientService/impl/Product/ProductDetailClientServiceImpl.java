package com.example.be_datn.service.clientService.impl.Product;

import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.service.clientService.ProductService.ProductDetailClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDetailClientServiceImpl implements ProductDetailClientService {

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Override
    public List<Object[]> findChiTietSanPhamBySanPhamId(Integer sanPhamId) {
        return chiTietSanPhamRepository.findChiTietSanPhamBySanPhamId(sanPhamId);
    }

    @Override
    public Double findMinPrice() {
        return chiTietSanPhamRepository.findMinPrice();
    }

    @Override
    public Double findMaxPrice() {
        return chiTietSanPhamRepository.findMaxPrice();
    }

    @Override
    public List<String> findDistinctColors() {
        return chiTietSanPhamRepository.findDistinctColors();
    }
}
