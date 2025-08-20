package com.example.be_datn.repository.discount;

import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, Integer> {

    @Modifying
    @Query("DELETE FROM PhieuGiamGiaCaNhan p WHERE p.idPhieuGiamGia.id = :phieuGiamGiaId")
    void deleteByIdPhieuGiamGia(@Param("phieuGiamGiaId") Integer phieuGiamGiaId);

    List<PhieuGiamGiaCaNhan> findByIdPhieuGiamGia(PhieuGiamGia phieuGiamGia);

    List<PhieuGiamGiaCaNhan> findByIdPhieuGiamGia_Id(Integer pggId);

    List<PhieuGiamGiaCaNhan> findByIdKhachHangId(Integer idKhachHang);

    Optional<PhieuGiamGiaCaNhan> findByMa(String ma);

    PhieuGiamGiaCaNhan findByIdKhachHangAndIdPhieuGiamGia(KhachHang khachHang, PhieuGiamGia phieuGiamGia);

    @Query("SELECT pc FROM PhieuGiamGiaCaNhan pc JOIN pc.idPhieuGiamGia p " +
            "WHERE pc.idKhachHang.id = :idKhachHang " +
            "AND p.riengTu = true AND p.trangThai = true AND p.deleted = false " +
            "AND pc.trangThai = true AND pc.deleted = false " +
            "AND (p.ngayKetThuc IS NULL OR p.ngayKetThuc >= :currentDate) " +
            "AND (p.soLuongDung IS NULL OR p.soLuongDung > 0) " +
            "AND (p.hoaDonToiThieu IS NULL OR :tongTien >= p.hoaDonToiThieu)")
    List<PhieuGiamGiaCaNhan> findValidPrivateVouchersByKhachHang(
            @Param("idKhachHang") Integer idKhachHang,
            @Param("tongTien") Double tongTien,
            @Param("currentDate") Date currentDate);


    @Query("SELECT pc FROM PhieuGiamGiaCaNhan pc JOIN pc.idPhieuGiamGia p " +
            "WHERE p.id = :idPhieuGiamGia AND pc.idKhachHang.id = :idKhachHang " +
            "AND p.riengTu = true AND p.trangThai = true AND p.deleted = false " +
            "AND pc.trangThai = true AND pc.deleted = false " +
            "AND (p.ngayKetThuc IS NULL OR p.ngayKetThuc >= :currentDate) " +
            "AND (p.soLuongDung IS NULL OR p.soLuongDung > 0)")
    Optional<PhieuGiamGiaCaNhan> findValidPrivateVoucherByIdPhieuGiamGiaAndIdKhachHang(
            @Param("idPhieuGiamGia") Integer idPhieuGiamGia,
            @Param("idKhachHang") Integer idKhachHang,
            @Param("currentDate") Date currentDate);

    @Query("SELECT pc FROM PhieuGiamGiaCaNhan pc JOIN pc.idPhieuGiamGia p " +
            "WHERE pc.ma = :ma AND pc.idKhachHang.id = :idKhachHang " +
            "AND p.riengTu = true AND p.trangThai = true AND p.deleted = false " +
            "AND pc.trangThai = true AND pc.deleted = false " +
            "AND (p.ngayKetThuc IS NULL OR p.ngayKetThuc >= :currentDate) " +
            "AND (p.soLuongDung IS NULL OR p.soLuongDung > 0)")
    Optional<PhieuGiamGiaCaNhan> findValidPrivateVoucherByMaAndKhachHang(
            @Param("ma") String ma,
            @Param("idKhachHang") Integer idKhachHang,
            @Param("currentDate") Date currentDate);
}
