package com.example.be_datn.repository.statistics;

import com.example.be_datn.entity.product.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CTSPForThongKe extends JpaRepository<ChiTietSanPham,Integer> {
}
