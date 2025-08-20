package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.order.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {
    void deleteByHoaDon(HoaDon hoaDon);
}
