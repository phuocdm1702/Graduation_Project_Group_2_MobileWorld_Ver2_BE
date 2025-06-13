package com.example.be_datn.repository.sale.Product;

import com.example.be_datn.entity.product.HeDieuHanh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HDHForDGGRepo extends JpaRepository<HeDieuHanh, Integer> {
    List<HeDieuHanh> findAllByDeletedFalse();
}
