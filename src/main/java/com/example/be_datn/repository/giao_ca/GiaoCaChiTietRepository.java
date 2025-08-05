package com.example.be_datn.repository.giao_ca;

import com.example.be_datn.entity.giao_ca.GiaoCaChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiaoCaChiTietRepository extends JpaRepository<GiaoCaChiTiet, Integer> {
}