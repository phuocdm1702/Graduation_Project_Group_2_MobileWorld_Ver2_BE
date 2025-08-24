package com.example.be_datn.service.clientService.ProductService;

import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface ProductDetailClientService { ;

    public List<Object[]> findChiTietSanPhamBySanPhamId(Integer sanPhamId);

    public Double findMinPrice();

    public Double findMaxPrice();

    public List<String> findDistinctColors();

    public List<Object[]> findChiTietSanPhamBySanPhamIdModification(Integer sanPhamId);

    Long countSoLuongTonKho(Integer sanPhamId,String idMauSac,String idBoNhoTrong,String idRam);
}
