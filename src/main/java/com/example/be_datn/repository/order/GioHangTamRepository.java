package com.example.be_datn.repository.order;

import com.example.be_datn.dto.sale.GioHangTam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GioHangTamRepository extends JpaRepository<GioHangTam, Integer> {
    List<GioHangTam> findByIdHoaDonAndDeletedFalse(Integer idHoaDon);

    @Query("SELECT DISTINCT ght.idPhieuGiamGia FROM GioHangTam ght WHERE ght.idHoaDon = :idHoaDon AND ght.deleted = false")
    List<Integer> findIdPhieuGiamGiaByIdHoaDonAndDeletedFalse(Integer idHoaDon);

    @Modifying
    @Query("UPDATE GioHangTam ght SET ght.deleted = true WHERE ght.idHoaDon = :idHoaDon")
    void markAsDeletedByIdHoaDon(Integer idHoaDon);

    @Modifying
    @Query("UPDATE GioHangTam ght SET ght.deleted = true WHERE ght.idHoaDon = :idHoaDon AND ght.imei = :imei")
    void markAsDeletedByIdHoaDonAndImei(Integer idHoaDon, String imei);
}
