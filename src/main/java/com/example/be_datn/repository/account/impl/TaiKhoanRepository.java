package com.example.be_datn.repository.account.impl;

import com.example.be_datn.entity.account.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan,Integer> {
    Optional<TaiKhoan> findByEmail(String email);

    List<TaiKhoan> findBySoDienThoai(String soDienThoai);

    @Query("SELECT MAX(t.ma) FROM TaiKhoan t WHERE t.ma LIKE 'TK%'")
    String findMaxMaTK();

    boolean existsByMa(String ma);
}
