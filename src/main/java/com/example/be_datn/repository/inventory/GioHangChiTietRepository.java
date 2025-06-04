package com.example.be_datn.repository.inventory;

import com.example.be_datn.entity.inventory.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GioHangChiTietRepository extends JpaRepository<ChiTietGioHang, Integer> {
    List<ChiTietGioHang> findByIdGioHangIdAndDeletedFalse(Integer idGioHang);
}
