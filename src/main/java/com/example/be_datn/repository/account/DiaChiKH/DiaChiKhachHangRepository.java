package com.example.be_datn.repository.account.DiaChiKH;

import com.example.be_datn.entity.account.DiaChiKhachHang;
import com.example.be_datn.entity.account.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang,Integer>,DiaChiKhachHangCustomRepository {
    boolean existsByMa(String ma);

    List<DiaChiKhachHang> findByIdKhachHang(KhachHang existing);
}
