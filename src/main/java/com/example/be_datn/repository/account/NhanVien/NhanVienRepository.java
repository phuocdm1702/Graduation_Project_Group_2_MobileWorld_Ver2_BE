package com.example.be_datn.repository.account.NhanVien;

import com.example.be_datn.entity.account.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVien,Integer>,NhanVienCustomRepository {
    boolean existsByMa(String finalCode);
}
