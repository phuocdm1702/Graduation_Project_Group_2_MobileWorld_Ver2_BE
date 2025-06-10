package com.example.be_datn.repository.order;

import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.entity.order.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    @Query("""
            SELECT new com.example.be_datn.dto.order.response.HoaDonResponse(
                h.id, 
                h.ma, 
                h.idNhanVien.ma,
                h.tenKhachHang,
                h.soDienThoaiKhachHang, 
                h.tongTienSauGiam,
                h.phiVanChuyen, 
                h.ngayTao, 
                h.loaiDon, 
                h.trangThai,
                h.deleted
            )
            FROM HoaDon h
            WHERE h.loaiDon = :loaiDon
            ORDER BY h.id DESC
            """)
    Page<HoaDonResponse> getHoaDon(@Param("loaiDon") String loaiDon, Pageable pageable);

    @Query("""
            SELECT new com.example.be_datn.dto.order.response.HoaDonResponse(
                h.id, 
                h.ma, 
                h.idNhanVien.ma,
                h.tenKhachHang, 
                h.soDienThoaiKhachHang, 
                h.tongTienSauGiam,
                h.phiVanChuyen, 
                h.ngayTao, 
                h.loaiDon, 
                h.trangThai,
                h.deleted
            )
            FROM HoaDon h
            WHERE (
                :keyword IS NULL 
                OR h.ma LIKE %:keyword%
                OR h.tenKhachHang LIKE %:keyword%
                OR h.soDienThoaiKhachHang LIKE %:keyword%
            )
            AND (:minAmount IS NULL OR h.tongTienSauGiam >= :minAmount)
            AND (:maxAmount IS NULL OR h.tongTienSauGiam <= :maxAmount)
            AND (:startDate IS NULL OR h.ngayTao >= :startDate)
            AND (:endDate IS NULL OR h.ngayTao <= :endDate)
            AND (:trangThai IS NULL OR h.trangThai = :trangThai)
            AND (:deleted IS NULL OR h.deleted = :deleted)
            ORDER BY h.id DESC
            """)
    Page<HoaDonResponse> getHoaDonAndFilters(
            @Param("keyword") String keyword,
            @Param("minAmount") Long minAmount,
            @Param("maxAmount") Long maxAmount,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("trangThai") Short trangThai,
            @Param("deleted") Boolean deleted,
            Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd WHERE hd.trangThai = 0")
    List<HoaDon> findAllHDNotConfirm();

    @Query("SELECT COUNT(h) > 0 FROM HoaDon h WHERE h.id = :id")
    boolean existsById(Integer id);

    @Query("""
            SELECT hd FROM HoaDon hd 
            LEFT JOIN FETCH hd.idKhachHang
            LEFT JOIN FETCH hd.idNhanVien
            LEFT JOIN FETCH hd.idPhieuGiamGia
            LEFT JOIN FETCH hd.chiTietHoaDon cthd
            LEFT JOIN FETCH cthd.idChiTietSanPham ctsp
            LEFT JOIN FETCH ctsp.idSanPham
            LEFT JOIN FETCH cthd.idImelDaBan
            LEFT JOIN FETCH hd.lichSuHoaDon lshd
            LEFT JOIN FETCH lshd.idNhanVien
            LEFT JOIN FETCH hd.hinhThucThanhToan httt
            LEFT JOIN FETCH httt.idPhuongThucThanhToan
            WHERE hd.id = :id AND hd.deleted = false
            """)
    Optional<HoaDon> findHoaDonDetailById(@Param("id") Integer id);
}
