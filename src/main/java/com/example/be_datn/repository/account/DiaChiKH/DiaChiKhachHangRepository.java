package com.example.be_datn.repository.account.DiaChiKH;

import com.example.be_datn.entity.account.DiaChiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang,Integer>,DiaChiKhachHangCustomRepository {
    boolean existsByMa(String ma);
}
