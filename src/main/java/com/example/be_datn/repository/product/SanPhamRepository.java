package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.SanPham;
import org.springframework.data.domain.Page;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    Page<SanPham> findAll(Specification<SanPham> spec, Pageable pageable);
    Page<SanPham> findByDeletedFalse(Pageable pageable);
    List<SanPham> findAllByDeletedFalse();
    SanPham findByIdAndDeletedFalse(Integer id);
    Optional<SanPham> findByTenSanPhamAndDeletedFalse(String tenSanPham);
}