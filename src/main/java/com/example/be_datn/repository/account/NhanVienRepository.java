package com.example.be_datn.repository.account;

import com.example.be_datn.entity.account.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
}
