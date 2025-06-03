package com.example.be_datn.repository.inventory;

import com.example.be_datn.entity.inventory.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<ChiTietGioHang, Integer> {
    List<ChiTietGioHang> findByIdGioHangIdAndDeletedFalse(Integer idGioHang);
}
