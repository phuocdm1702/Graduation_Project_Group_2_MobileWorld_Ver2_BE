package com.example.be_datn.repository.inventory;

import com.example.be_datn.entity.inventory.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    Optional<GioHang> findTopByIdKhachHangIdAndDeletedFalseOrderByIdDesc(Integer idKhachHang);
}
