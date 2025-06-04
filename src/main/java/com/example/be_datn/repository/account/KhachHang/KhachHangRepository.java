package com.example.be_datn.repository.account.KhachHang;

import com.example.be_datn.entity.account.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhachHangRepository extends JpaRepository<KhachHang,Integer>,KhachHangCustomRepository {
}
