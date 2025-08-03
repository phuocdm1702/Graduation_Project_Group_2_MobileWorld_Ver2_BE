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
import java.time.LocalDateTime;
import java.util.Date;
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
            AND (:loaiDon IS NULL OR h.loaiDon = :loaiDon)
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
            @Param("loaiDon") String loaiDon,
            Pageable pageable);


    @Query("SELECT hd FROM HoaDon hd WHERE hd.deleted = true")
    List<HoaDon> findAllHDNotConfirm();

    @Query("SELECT COUNT(h) > 0 FROM HoaDon h WHERE h.id = :id")
    boolean existsById(Integer id);

    //Detail HDCT...
    @Query("""
            SELECT hd FROM HoaDon hd 
            LEFT JOIN hd.idKhachHang
            LEFT JOIN hd.idNhanVien
            LEFT JOIN hd.idPhieuGiamGia
            LEFT JOIN hd.chiTietHoaDon cthd
            LEFT JOIN cthd.idChiTietSanPham ctsp
            LEFT JOIN ctsp.idSanPham
            LEFT JOIN cthd.idImelDaBan
            LEFT JOIN hd.lichSuHoaDon lshd
            LEFT JOIN lshd.idNhanVien
            LEFT JOIN hd.hinhThucThanhToan httt
            LEFT JOIN httt.idPhuongThucThanhToan
            WHERE hd.id = :id AND hd.deleted = false
            """)
    Optional<HoaDon> findHoaDonDetailById(@Param("id") Integer id);

    // Tìm hóa đơn theo mã
    @Query("SELECT hd FROM HoaDon hd WHERE hd.ma = :ma")
    Optional<HoaDonResponse> findByMa(@Param("ma") String ma);

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.idKhachHang.id = :khachHangId")
    Long countByKhachHangId(@Param("khachHangId") Integer khachHangId);

    // Tìm hóa đơn gần nhất của khách hàng theo ngày tạo, giới hạn 1 kết quả
    @Query("SELECT h FROM HoaDon h WHERE h.idKhachHang.id = :khachHangId ORDER BY h.ngayTao DESC FETCH FIRST 1 ROW ONLY")
    Optional<HoaDon> findTopByKhachHangIdOrderByNgayTaoDesc(@Param("khachHangId") Integer khachHangId);

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangThai = :trangThai AND h.deleted = :deleted")
    long countByTrangThaiAndDeleted(@Param("trangThai") Short trangThai, @Param("deleted") Boolean deleted);

    List<HoaDon> findByTrangThaiInAndNgayTaoBetween(List<Short> trangThai, LocalDateTime startDate, LocalDateTime endDate);

    List<HoaDon> findByGiaoCa_Id(Integer giaoCaId);

    List<HoaDon> findByGiaoCa_IdAndDeletedFalseAndTrangThai(Integer giaoCaId, Short trangThai);

}
