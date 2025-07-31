package com.example.be_datn.repository.account.NhanVien;

import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.account.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien,Integer>,NhanVienCustomRepository {
    boolean existsByMa(String finalCode);

    Optional<NhanVien> findByMa(String ma);

    NhanVien findByIdTaiKhoan(TaiKhoan taiKhoan);

    Optional<NhanVien> findByIdTaiKhoan_TenDangNhap(String username);
}
