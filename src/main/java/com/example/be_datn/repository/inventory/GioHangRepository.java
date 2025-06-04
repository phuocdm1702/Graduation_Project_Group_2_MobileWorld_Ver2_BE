package com.example.be_datn.repository.inventory;

import com.example.be_datn.entity.inventory.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang, Integer>, GioHangCustomRepository {
    Optional<GioHang> findTopByIdKhachHangIdAndDeletedFalseOrderByIdDesc(Integer idKhachHang);

    @Modifying
    @Query("DELETE FROM GioHang g WHERE g.idHoaDon.id = :idHoaDon")
    void deleteByIdHoaDon(Integer idHoaDon);
}
