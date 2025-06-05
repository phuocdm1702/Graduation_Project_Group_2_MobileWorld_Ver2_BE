package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    //Phân trang hóa đơn
    @Query("SELECT h FROM HoaDon h ORDER BY h.id DESC")
    Page<HoaDon> getHoaDon(Pageable pageable);

    //Phân trang bộ lọc hóa đơn
    @Query("""
            SELECT hd FROM HoaDon hd
            WHERE (
            :keyword IS NULL OR hd.ma LIKE %:keyword%
            OR hd.idNhanVien.tenNhanVien LIKE %:keyword%
            OR hd.tenKhachHang LIKE %:keyword%
            OR hd.soDienThoaiKhachHang LIKE %:keyword%
            )
            AND (:minAmount IS NULL OR hd.tongTienSauGiam >= :minAmount)
            AND (:maxAmount IS NULL OR hd.tongTienSauGiam <= :maxAmount)
            AND (:startDate IS NULL OR hd.loaiDon >= :startDate)
            AND (:endDate IS NULL OR hd.loaiDon <= :endDate)
            AND (:trangThai IS NULL OR hd.loaiDon = :trangThai)
            ORDER BY hd.id DESC
            """)
    Page<HoaDon> getHoaDonAndFilters(
            @Param("keyword") String keyword,
            @Param("minAmount") Long minAmount,
            @Param("maxAmount") Long maxAmount,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("trangThai") Short trangThai,
            Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd WHERE hd.trangThai = 0")
    List<HoaDon> findAllHDNotConfirm();

    @Query("SELECT COUNT(h) > 0 FROM HoaDon h WHERE h.id = :id")
    boolean existsById(Integer id);
}
