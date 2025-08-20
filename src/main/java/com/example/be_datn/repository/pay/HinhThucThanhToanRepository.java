package com.example.be_datn.repository.pay;

import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan, Integer> {
    List<HinhThucThanhToan> findByHoaDonId(Integer id);

    void deleteByHoaDon(HoaDon hoaDon);
}
