package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface HoaDonRepository extends JpaRepository<HoaDon, Integer>, HoaDonCustomRepository {
    @Query("SELECT hd FROM HoaDon hd WHERE hd.trangThai = 0")
    List<HoaDon> findAllHDNotConfirm();
}
