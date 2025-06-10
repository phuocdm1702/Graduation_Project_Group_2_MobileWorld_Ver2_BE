package com.example.be_datn.repository.discount;

import com.example.be_datn.entity.discount.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    Page<PhieuGiamGia> findByNgayKetThucGreaterThanEqual(Date ngayKetThuc, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE (p.ma LIKE %:keyword% OR p.tenPhieuGiamGia LIKE %:keyword%) " +
            "AND p.ngayKetThuc >= :currentDate")
    Page<PhieuGiamGia> search(String keyword, Date currentDate, Pageable pageable);

    @Query("SELECT p FROM PhieuGiamGia p WHERE " +
            "(:loaiPhieu IS NULL OR p.loaiPhieuGiamGia = :loaiPhieu) AND " +
            "(:trangThai IS NULL OR p.trangThai = :trangThai) AND " +
            "(:ngayBatDau IS NULL OR p.ngayBatDau >= :ngayBatDau) AND " +
            "(:ngayKetThuc IS NULL OR p.ngayKetThuc <= :ngayKetThuc) AND " +
            "(:minOrder IS NULL OR p.hoaDonToiThieu >= :minOrder) AND " +
            "(:valueFilter IS NULL OR p.soTienGiamToiDa >= :valueFilter) AND " +
            "(:currentDate IS NULL OR p.ngayKetThuc >= :currentDate)")
    Page<PhieuGiamGia> filterPhieuGiamGia(
            String loaiPhieu,
            Boolean trangThai,
            Date ngayBatDau,
            Date ngayKetThuc,
            Double minOrder,
            Double valueFilter,
            Date currentDate,
            Pageable pageable
    );

    Optional<PhieuGiamGia> findByma(String ma);

}
