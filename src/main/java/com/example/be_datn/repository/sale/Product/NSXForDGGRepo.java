package com.example.be_datn.repository.sale.Product;

import com.example.be_datn.entity.product.HeDieuHanh;
import com.example.be_datn.entity.product.NhaSanXuat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NSXForDGGRepo extends JpaRepository<NhaSanXuat, Integer> {
    List<NhaSanXuat> findAllByDeletedFalse();
}
