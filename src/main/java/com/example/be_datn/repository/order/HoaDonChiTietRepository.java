package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.product.Imel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
}
