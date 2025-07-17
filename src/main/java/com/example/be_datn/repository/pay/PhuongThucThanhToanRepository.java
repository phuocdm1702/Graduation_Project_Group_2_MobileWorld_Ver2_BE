package com.example.be_datn.repository.pay;

import com.example.be_datn.entity.pay.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Integer> {
    List<PhuongThucThanhToan> findAllByKieuThanhToan(String kieuThanhToan);
}
