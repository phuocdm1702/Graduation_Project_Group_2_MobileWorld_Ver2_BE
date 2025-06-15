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
            "WHERE CAST(hd.ngay_thanh_toan AS DATE) >= CAST(:ngayHienTai AS DATE) " +
            "AND hd.deleted = 0",
            nativeQuery = true)
    Map<String, Object> thongKeTheoNgay(@Param("ngayHienTai") Date ngayHienTai);

    // Tuần hiện tại
    @Query(value = "SELECT " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE CAST(hd.ngay_thanh_toan AS DATE) >= CAST(:startOfWeek AS DATE) " +
            "AND CAST(hd.ngay_thanh_toan AS DATE) <= CAST(:endOfWeek AS DATE) " +
            "AND hd.deleted = 0",
            nativeQuery = true)
    Map<String, Object> thongKeTheoTuan(
            @Param("startOfWeek") Date startOfWeek,
            @Param("endOfWeek") Date endOfWeek
    );

    // Tháng hiện tại
    @Query(value = "SELECT " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(MONTH, hd.ngay_thanh_toan) = :thang " +
            "AND DATEPART(YEAR, hd.ngay_thanh_toan) = :nam " +
            "AND hd.deleted = 0",
            nativeQuery = true)
    Map<String, Object> thongKeTheoThang(
            @Param("thang") int thang,
            @Param("nam") int nam
    );
    // Năm hiện tại
    @Query(value = "SELECT " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu, " +
            "COUNT(hdct.id_chi_tiet_san_pham) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(YEAR, hd.ngay_thanh_toan) = :nam " +
            "AND hd.deleted = 0",
            nativeQuery = true)
    Map<String, Object> thongKeTheoNam(@Param("nam") int nam);

    @Query(value = "SELECT " +
            "CASE " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 6 THEN '0-6h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 9 THEN '6-9h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 12 THEN '9-12h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 15 THEN '12-15h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 18 THEN '15-18h' " +
            "ELSE '18-24h' END as khungGio, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE CAST(hd.ngay_thanh_toan AS DATE) = CAST(:ngayHienTai AS DATE) " +
            "AND hd.deleted = 0 " +
            "GROUP BY " +
            "CASE " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 6 THEN '0-6h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 9 THEN '6-9h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 12 THEN '9-12h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 15 THEN '12-15h' " +
            "WHEN DATEPART(HOUR, hd.ngay_thanh_toan) < 18 THEN '15-18h' " +
            "ELSE '18-24h' END " +
            "ORDER BY MIN(hd.ngay_thanh_toan)",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoKhungGio(@Param("ngayHienTai") Date ngayHienTai);

    @Query(value = "SELECT " +
            "CASE " +
            "WHEN DATEPART(dw, hd.ngay_thanh_toan) = 2 THEN 'T2' " +
            "WHEN DATEPART(dw, hd.ngay_thanh_toan) = 3 THEN 'T3' " +
            "WHEN DATEPART(dw, hd.ngay_thanh_toan) = 4 THEN 'T4' " +
            "WHEN DATEPART(dw, hd.ngay_thanh_toan) = 5 THEN 'T5' " +
            "WHEN DATEPART(dw, hd.ngay_thanh_toan) = 6 THEN 'T6' " +
            "ELSE 'Khác' END as ngayTrongTuan, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE CAST(hd.ngay_thanh_toan AS DATE) >= CAST(:startOfWeek AS DATE) " +
            "AND CAST(hd.ngay_thanh_toan AS DATE) <= CAST(:endOfWeek AS DATE) " +
            "AND hd.deleted = 0 " +
            "GROUP BY DATEPART(dw, hd.ngay_thanh_toan) " +
            "ORDER BY DATEPART(dw, hd.ngay_thanh_toan)",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoNgayTrongTuan(
            @Param("startOfWeek") Date startOfWeek,
            @Param("endOfWeek") Date endOfWeek);

    @Query(value = "SELECT " +
            "DATEPART(WEEK, hd.ngay_thanh_toan) - DATEPART(WEEK, DATEADD(MONTH, DATEDIFF(MONTH, 0, hd.ngay_thanh_toan), 0)) + 1 as tuan, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE DATEPART(MONTH, hd.ngay_thanh_toan) = :thang " +
            "AND DATEPART(YEAR, hd.ngay_thanh_toan) = :nam " +
            "AND hd.deleted = 0 " +
            "GROUP BY DATEPART(WEEK, hd.ngay_thanh_toan) - DATEPART(WEEK, DATEADD(MONTH, DATEDIFF(MONTH, 0, hd.ngay_thanh_toan), 0)) + 1 " +
            "ORDER BY tuan",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoTuanTrongThang(
            @Param("thang") int thang,
            @Param("nam") int nam);

    @Query(value = "SELECT " +
            "DATEPART(QUARTER, hd.ngay_thanh_toan) as quy, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE DATEPART(YEAR, hd.ngay_thanh_toan) = :nam " +
            "AND hd.deleted = 0 " +
            "GROUP BY DATEPART(QUARTER, hd.ngay_thanh_toan) " +
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
            "CONCAT(DATEPART(MONTH, hd.ngay_thanh_toan), '/', DATEPART(YEAR, hd.ngay_thanh_toan)) as thangNam, " +
            "SUM(hd.tong_tien_sau_giam) as doanhThu " +
            "FROM hoa_don hd " +
            "WHERE CAST(hd.ngay_thanh_toan AS DATE) >= CAST(:startDate AS DATE) " +
            "AND CAST(hd.ngay_thanh_toan AS DATE) <= CAST(:endDate AS DATE) " +
            "AND hd.deleted = 0 " +
            "GROUP BY DATEPART(MONTH, hd.ngay_thanh_toan), DATEPART(YEAR, hd.ngay_thanh_toan) " +
            "ORDER BY DATEPART(YEAR, hd.ngay_thanh_toan), DATEPART(MONTH, hd.ngay_thanh_toan)",
            nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoThangTrongKhoangThoiGian(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    // Sản phẩm bán chạy (phân trang)
    @Query(value = "SELECT c.id_chi_tiet_san_pham, COUNT(c.id) as soLuongBan " +
            "FROM hoa_don_chi_tiet c " +
            "JOIN hoa_don h ON c.id_hoa_don = h.id " +
            "WHERE (:startDate IS NULL OR h.created_at >= CAST(:startDate AS DATE)) " +
            "AND (:endDate IS NULL OR h.created_at <= CAST(:endDate AS DATE)) " +
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
            "WHERE (:startDate IS NULL OR h.created_at >= CAST(:startDate AS DATE)) " +
            "AND (:endDate IS NULL OR h.created_at <= CAST(:endDate AS DATE)) " +
            "GROUP BY c.id_chi_tiet_san_pham",
            nativeQuery = true)
    List<Object[]> findAllTopSellingProducts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Tăng trưởng
    @Query(value = "SELECT " +
            "SUM(hd.tong_tien) as doanhThu, " +
            "COUNT(hdct.id) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(DAY, hd.created_at) = DATEPART(DAY, :ngay) " +
            "AND DATEPART(MONTH, hd.created_at) = DATEPART(MONTH, :ngay) " +
            "AND DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :ngay)",
            nativeQuery = true)
    Map<String, Object> tangTruongTheoNgay(@Param("ngay") Date ngay);

    @Query(value = "SELECT " +
            "SUM(hd.tong_tien) as doanhThu, " +
            "COUNT(hdct.id) as sanPhamDaBan, " +
            "COUNT(DISTINCT hd.id) as tongSoDonHang " +
            "FROM hoa_don hd " +
            "JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don " +
            "WHERE DATEPART(MONTH, hd.created_at) = DATEPART(MONTH, :thang) " +
            "AND DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :thang)",
            nativeQuery = true)
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
            "WHERE (:filterType = 'day' AND DATEPART(DAY, hd.created_at) = DATEPART(DAY, :date) " +
            "AND DATEPART(MONTH, hd.created_at) = DATEPART(MONTH, :date) " +
            "AND DATEPART(YEAR, hd.created_at) = DATEPART(YEAR, :date)) " +
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
}
