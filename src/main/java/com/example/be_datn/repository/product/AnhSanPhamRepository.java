package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.AnhSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnhSanPhamRepository extends JpaRepository<AnhSanPham, Integer> {
    @Query("SELECT a FROM AnhSanPham a WHERE a.duongDan = :duongDan AND a.deleted = false")
    Optional<AnhSanPham> findByDuongDan(@Param("duongDan") String duongDan);


}