package com.example.be_datn.repository.discount;

import com.example.be_datn.entity.discount.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    Page<PhieuGiamGia> findByNgayKetThucGreaterThanEqual(Date ngayKetThuc, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE (p.ma LIKE %:keyword% OR p.tenPhieuGiamGia LIKE %:keyword%) " +
            "AND p.ngayKetThuc >= :currentDate")
    Page<PhieuGiamGia> search(String keyword, Date currentDate, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE :loaiPhieu IS NULL OR p.loaiPhieuGiamGia = :loaiPhieu AND p.ngayKetThuc >= :currentDate")
    Page<PhieuGiamGia> filterByLoaiPhieu(@Param("loaiPhieu") String loaiPhieu, Date currentDate, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE :trangThai IS NULL OR p.trangThai = :trangThai AND p.ngayKetThuc >= :currentDate")
    Page<PhieuGiamGia> filterByTrangThai(@Param("trangThai") Boolean trangThai, Date currentDate, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE " +
            "(:ngayBatDau IS NULL OR p.ngayBatDau >= :ngayBatDau) AND " +
            "(:ngayKetThuc IS NULL OR p.ngayKetThuc <= :ngayKetThuc) AND " +
            "(:currentDate IS NULL OR p.ngayKetThuc >= :currentDate)")
    Page<PhieuGiamGia> filterByDateRange(@Param("ngayBatDau") Date ngayBatDau,
                                         @Param("ngayKetThuc") Date ngayKetThuc,
                                         @Param("currentDate") Date currentDate,
                                         Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE :minOrder IS NULL OR p.hoaDonToiThieu >= :minOrder AND p.ngayKetThuc >= :currentDate")
    Page<PhieuGiamGia> filterByMinOrder(@Param("minOrder") Double minOrder, Date currentDate, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE :valueFilter IS NULL OR p.soTienGiamToiDa >= :valueFilter AND p.ngayKetThuc >= :currentDate")
    Page<PhieuGiamGia> filterByValue(@Param("valueFilter") Double valueFilter, Date currentDate, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE " +
            "(:loaiPhieu IS NULL OR p.loaiPhieuGiamGia = :loaiPhieu) AND " +
            "(:trangThai IS NULL OR p.trangThai = :trangThai) AND " +
            "(:ngayBatDau IS NULL OR p.ngayBatDau >= :ngayBatDau) AND " +
            "(:ngayKetThuc IS NULL OR p.ngayKetThuc <= :ngayKetThuc) AND " +
            "(:minOrder IS NULL OR p.hoaDonToiThieu >= :minOrder) AND " +
            "(:valueFilter IS NULL OR p.soTienGiamToiDa >= :valueFilter) AND " +
            "(:currentDate IS NULL OR p.ngayKetThuc >= :currentDate)")
    Page<PhieuGiamGia> filterPhieuGiamGia(
            @Param("loaiPhieu") String loaiPhieu,
            @Param("trangThai") Boolean trangThai,
            @Param("ngayBatDau") Date ngayBatDau,
            @Param("ngayKetThuc") Date ngayKetThuc,
            @Param("minOrder") Double minOrder,
            @Param("valueFilter") Double valueFilter,
            @Param("currentDate") Date currentDate,
            Pageable pageable
    );

    Optional<PhieuGiamGia> findByma(String ma);

    @Query("SELECT p FROM PhieuGiamGia p WHERE p.riengTu = false " +
            "AND p.trangThai = true AND p.deleted = false " +
            "AND (p.ngayKetThuc IS NULL OR p.ngayKetThuc >= :currentDate) " +
            "AND (p.soLuongDung IS NULL OR p.soLuongDung > 0) " +
            "AND (p.hoaDonToiThieu IS NULL OR :tongTien >= p.hoaDonToiThieu)")
    List<PhieuGiamGia> findValidPublicVouchers(@Param("tongTien") Double tongTien, @Param("currentDate") Date currentDate);

    @Query("SELECT p FROM PhieuGiamGia p WHERE p.ma = :ma " +
            "AND p.riengTu = false AND p.trangThai = true AND p.deleted = false " +
            "AND (p.ngayKetThuc IS NULL OR p.ngayKetThuc >= :currentDate) " +
            "AND (p.soLuongDung IS NULL OR p.soLuongDung > 0)")
    Optional<PhieuGiamGia> findValidPublicVoucherByMa(@Param("ma") String ma, @Param("currentDate") Date currentDate);
}
