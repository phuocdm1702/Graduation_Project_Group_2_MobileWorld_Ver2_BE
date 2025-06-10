package com.example.be_datn.repository.account.KhachHang;

import com.example.be_datn.entity.account.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang,Integer>,KhachHangCustomRepository {
    boolean existsByMa(String finalCode);

    Optional<KhachHang> findByMa(String ma);

    @Query("SELECT k FROM KhachHang k WHERE CONCAT(k.ma, k.ten) LIKE %?1% ")
    List<KhachHang> searchFormAdd(String keyword);
}
