package com.example.be_datn.repository.statistics;

import com.example.be_datn.dto.statistics.respone.HangBanChayDTO;
import com.example.be_datn.dto.statistics.respone.LoaiHoaDonDTO;
import com.example.be_datn.dto.statistics.respone.SanPhamHetHangDTO;
import com.example.be_datn.entity.order.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ThongKeRepository extends JpaRepository<HoaDon, Integer> {
    @Query(value = "SELECT " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE CAST(hd.created_at AS DATE) = CAST(:ngayHienTai AS DATE) " +
            "AND hd.deleted = 0 AND hd.trang_thai = 3",
            nativeQuery = true)
    Map<String, Object> thongKeTheoNgay(@Param("ngayHienTai") Date ngayHienTai);

    @Query(value = "SELECT " +
            "COALESCE(SUM(hd.tong_tien_sau_giam), 0) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE CAST(hd.created_at AS DATE) >= CAST(:startOfWeek AS DATE) " +
            "AND CAST(hd.created_at AS DATE) <= CAST(:endOfWeek AS DATE) " +
            "AND hd.deleted = 0 AND hd.trang_thai = 3",
            nativeQuery = true)
    Map<String, Object> thongKeTheoTuan(
            @Param("startOfWeek") Date startOfWeek,
            @Param("endOfWeek") Date endOfWeek
    );

    @Query(value = "SELECT " +
            "COALESCE(SUM(hd.tong_tien_sau_giam), 0) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(MONTH, hd.created_at) = :thang " +
            "AND DATEPART(YEAR, hd.created_at) = :nam " +
            "AND hd.deleted = 0 AND hd.trang_thai = 3",
            nativeQuery = true)
    Map<String, Object> thongKeTheoThang(
            @Param("thang") int thang,
            @Param("nam") int nam
    );

    @Query(value = "SELECT " +
            "COALESCE(SUM(hd.tong_tien_sau_giam), 0) as doanhThu, " +
            "(SELECT COUNT(hdct.id_chi_tiet_san_pham) FROM hoa_don_chi_tiet hdct WHERE hdct.id_hoa_don IN (SELECT hd2.id FROM hoa_don hd2 WHERE DATEPART(YEAR, hd2.created_at) = :nam AND hd2.deleted = 0 AND hd2.trang_thai = 3)) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "WHERE DATEPART(YEAR, hd.created_at) = :nam " +
            "AND hd.deleted = 0",
            nativeQuery = true)
    Map<String, Object> thongKeTheoNam(@Param("nam") int nam);

    @Query(value = "SELECT " +
            "CASE " +
            "WHEN DATEPART(HOUR, hd.created_at) < 6 THEN '0-6h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 9 THEN '6-9h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 12 THEN '9-12h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 15 THEN '12-15h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 18 THEN '15-18h' " +
            "ELSE '18-24h' END as khungGio, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE CAST(hd.created_at AS DATE) = CAST(:ngayHienTai AS DATE) " +
            "AND hd.deleted = 0 AND hd.trang_thai = 3" +
            "GROUP BY " +
            "CASE " +
            "WHEN DATEPART(HOUR, hd.created_at) < 6 THEN '0-6h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 9 THEN '6-9h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 12 THEN '9-12h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 15 THEN '12-15h' " +
            "WHEN DATEPART(HOUR, hd.created_at) < 18 THEN '15-18h' " +
            "ELSE '18-24h' END " +
            "ORDER BY MIN(hd.created_at)",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoKhungGio(@Param("ngayHienTai") Date ngayHienTai);

    @Query(value = "SET DATEFIRST 1; " +
            "WITH AllDays AS ( " +
            "SELECT 1 AS day_num, 'T2' AS ngayTrongTuan UNION ALL " +
            "SELECT 2, 'T3' UNION ALL " +
            "SELECT 3, 'T4' UNION ALL " +
            "SELECT 4, 'T5' UNION ALL " +
            "SELECT 5, 'T6' UNION ALL " +
            "SELECT 6, 'T7' UNION ALL " +
            "SELECT 7, 'CN' " +
            ") " +
            "SELECT " +
            "    ad.ngayTrongTuan, " +
            "    COALESCE(SUM(hd.tong_tien_sau_giam), 0) AS doanhThu " +
            "FROM AllDays ad " +
            "LEFT JOIN hoa_don hd " +
            "    ON DATEPART(dw, hd.created_at) = ad.day_num " +
            "    AND CAST(hd.created_at AS DATE) >= CAST(:startOfWeek AS DATE) " +
            "    AND CAST(hd.created_at AS DATE) <= CAST(:endOfWeek AS DATE) " +
            "    AND hd.deleted = 0 AND hd.trang_thai = 3 " +
            "GROUP BY ad.day_num, ad.ngayTrongTuan " +
            "ORDER BY ad.day_num",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoNgayTrongTuan(
            @Param("startOfWeek") Date startOfWeek,
            @Param("endOfWeek") Date endOfWeek);

    @Query(value = "SELECT " +
            "DATEPART(WEEK, hd.created_at) - DATEPART(WEEK, DATEADD(MONTH, DATEDIFF(MONTH, 0, hd.created_at), 0)) + 1 as tuan, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE DATEPART(MONTH, hd.created_at) = :thang " +
            "AND DATEPART(YEAR, hd.created_at) = :nam " +
            "AND hd.deleted = 0" +
            "GROUP BY DATEPART(WEEK, hd.created_at) - DATEPART(WEEK, DATEADD(MONTH, DATEDIFF(MONTH, 0, hd.created_at), 0)) + 1 " +
            "ORDER BY tuan",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoTuanTrongThang(
            @Param("thang") int thang,
            @Param("nam") int nam);

    @Query(value = "SELECT " +
            "DATEPART(QUARTER, hd.created_at) as quy, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE DATEPART(YEAR, hd.created_at) = :nam " +
            "AND hd.deleted = 0 AND hd.trang_thai = 3" +
            "GROUP BY DATEPART(QUARTER, hd.created_at) " +
            "ORDER BY quy",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoQuy(
            @Param("nam") int nam);

    @Query("SELECT SUM(hd.tongTienSauGiam) FROM HoaDon hd " +
            "WHERE (:startDate IS NULL OR hd.createdAt >= CAST(:startDate AS DATE))" +
            "AND (:endDate IS NULL OR hd.createdAt <= CAST(:endDate AS DATE)) ")
    BigDecimal doanhThuTheoKhoangThoiGian( @Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate);

    @Query(value = "SELECT " +
            "CONCAT(DATEPART(MONTH, hd.created_at), '/', DATEPART(YEAR, hd.created_at)) as thangNam, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE CAST(hd.created_at AS DATE) >= CAST(:startDate AS DATE) " +
            "AND CAST(hd.created_at AS DATE) <= CAST(:endDate AS DATE) " +
            "AND hd.deleted = 0 AND hd.trang_thai = 3" +
            "GROUP BY DATEPART(MONTH, hd.created_at), DATEPART(YEAR, hd.created_at) " +
            "ORDER BY DATEPART(YEAR, hd.created_at), DATEPART(MONTH, hd.created_at)",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoThangTrongKhoangThoiGian(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    // Sản phẩm bán chạy (phân trang)
    @Query(value = "SELECT c.id_chi_tiet_san_pham, COUNT(c.id) as soLuongBan " +
            "FROM hoa_don_chi_tiet c " +
            "JOIN hoa_don h ON c.id_hoa_don = h.id " +
            "WHERE (:startDate IS NULL OR h.created_at >= :startDate) " +
            "AND (:endDate IS NULL OR h.created_at < DATEADD(DAY, 1, :endDate)) " +
            "GROUP BY c.id_chi_tiet_san_pham",
            nativeQuery = true)
    Page<Object[]> findTopSellingProducts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    // Sản phẩm bán chạy (toàn bộ dữ liệu)
    @Query(value = "SELECT c.id_chi_tiet_san_pham, COUNT(c.id) as soLuongBan " +
            "FROM hoa_don_chi_tiet c " +
            "JOIN hoa_don h ON c.id_hoa_don = h.id " +
            "WHERE (:startDate IS NULL OR h.created_at >= :startDate) " +
            "AND (:endDate IS NULL OR h.created_at < DATEADD(DAY, 1, :endDate)) " +
            "GROUP BY c.id_chi_tiet_san_pham",
            nativeQuery = true)
    List<Object[]> findAllTopSellingProducts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Tăng trưởng
    @Query(value = "SELECT " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(DAY, hd.created_at) = DATEPART(DAY, :ngay) " +
            "AND DATEPART(MONTH, hd.created_at) = DATEPART(MONTH, :ngay) " +
            "AND DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :ngay) ",
            nativeQuery = true)
    Map<String, Object> tangTruongTheoNgay(@Param("ngay") Date ngay);

    @Query(value = "SELECT " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(MONTH, hd.created_at) = DATEPART(MONTH, :thang) " +
            "AND DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :thang) ",nativeQuery = true)
    Map<String, Object> tangTruongTheoThang(@Param("thang") Date thang);

    @Query(value = "SELECT " +
            "SUM(hd.tong_tien) as doanhThu, " +
            "COUNT(hdct.id) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :nam)",
            nativeQuery = true)
    Map<String, Object> tangTruongTheoNam(@Param("nam") Date nam);

    // Trạng thái đơn hàng
    @Query(value = "SELECT hd.trang_thai AS trangThai, COUNT(hd.id) AS soLuong " +
            "FROM hoa_don hd " +
            "WHERE hd.deleted = 0 " +
            "AND (:filterType = 'day' AND CAST(hd.created_at AS DATE) = CAST(:date AS DATE)) " +
            "OR (:filterType = 'month' AND DATEPART(MONTH, hd.created_at) = DATEPART(MONTH, :date) " +
            "AND DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :date)) " +
            "OR (:filterType = 'year' AND DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :date)) " +
            "GROUP BY hd.trang_thai",
            nativeQuery = true)
    List<Map<String, Object>> thongKeTrangThaiDonHang(
            @Param("filterType") String filterType,
            @Param("date") Date date
    );

    // Sản phẩm sắp hết hàng (phân trang)
    @Query("SELECT new com.example.be_datn.dto.statistics.respone.SanPhamHetHangDTO(sp.tenSanPham, COUNT(ctsp)) " +
            "FROM ChiTietSanPham ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "WHERE ctsp.deleted = false "+
            "GROUP BY sp.id, sp.tenSanPham " +
            "HAVING COUNT(ctsp) < 10 " +
            "ORDER BY sp.tenSanPham ASC")
    Page<SanPhamHetHangDTO> thongKeSanPhamHetHang(Pageable pageable);

    @Query("SELECT new com.example.be_datn.dto.statistics.respone.SanPhamHetHangDTO(sp.tenSanPham, COUNT(ctsp)) " +
            "FROM ChiTietSanPham ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "WHERE ctsp.deleted = false "+
            "GROUP BY sp.id, sp.tenSanPham " +
            "HAVING COUNT(ctsp) < 10 " +
            "ORDER BY sp.tenSanPham ASC")
    List<SanPhamHetHangDTO> thongKeSanPhamHetHangNoPage();

    // Phân phối đa kênh
    @Query("SELECT new com.example.be_datn.dto.statistics.respone.LoaiHoaDonDTO(hd.loaiDon, COUNT(hd.id)) " +
            "FROM HoaDon hd " +
            "GROUP BY hd.loaiDon")
    List<LoaiHoaDonDTO> thongKeLoaiHoaDon();


    @Query("SELECT new com.example.be_datn.dto.statistics.respone.HangBanChayDTO(nsx.nhaSanXuat, SUM(hdct.gia)) " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.idChiTietSanPham ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "JOIN sp.idNhaSanXuat nsx " +
            "GROUP BY nsx.id, nsx.nhaSanXuat")
    List<HangBanChayDTO> thongKeHangBanChay();

    @Query(value = "SELECT " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE CAST(hd.created_at AS DATE) = DATEADD(DAY, -1, CAST(GETDATE() AS DATE)) " +
            "AND hd.deleted = 0",
            nativeQuery = true)
    Map<String, Object> thongKeTheoNgayHomQua();

    @Query(value = "SELECT " +
            "hd.loai_don as loaiDon, " +
            "COUNT(hd.id) as soLuong " +
            "FROM hoa_don hd " +
            "WHERE CAST(hd.created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) " +
            "AND hd.deleted = 0" +
            "GROUP BY hd.loai_don",
            nativeQuery = true)
    List<LoaiHoaDonDTO> thongKeLoaiHoaDonHomQua();

    @Query(value = "SELECT " +
            "hd.trang_thai as trangThai, " +
            "COUNT(hd.id) as soLuong " +
            "FROM hoa_don hd " +
            "WHERE CAST(hd.created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) " +
            "GROUP BY hd.trang_thai",
            nativeQuery = true)
    List<Map<String, Object>> thongKeTrangThaiDonHangHomQua();
}
