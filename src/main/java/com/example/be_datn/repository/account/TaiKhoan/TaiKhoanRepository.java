package com.example.be_datn.repository.account.TaiKhoan;

import com.example.be_datn.entity.account.TaiKhoan;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan,Integer>,TaiKhoanCustomRepository {
    Optional<TaiKhoan> findByEmail(String email);

    List<TaiKhoan> findBySoDienThoai(String soDienThoai);

    @Query("SELECT MAX(t.ma) FROM TaiKhoan t WHERE t.ma LIKE 'TK%'")
    String findMaxMaTK();

    boolean existsByMa(String ma);


    // Filter for login
    @Query("SELECT tk FROM TaiKhoan tk WHERE tk.tenDangNhap = :tenDangNhap")
    TaiKhoan findByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);

    @Query("SELECT tk FROM TaiKhoan tk WHERE tk.tenDangNhap = :tenDangNhap AND tk.matKhau = :matKhau")
    TaiKhoan findByTenDangNhapAndMatKhau(@Param("tenDangNhap") String tenDangNhap, @Param("matKhau") String matKhau);

    //checkEmail
    Optional<TaiKhoan> findBytenDangNhap(String tenDangNhap);
    boolean existsBySoDienThoai(String soDienThoai);


    boolean existsByEmail(String email);

}
