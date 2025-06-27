package com.example.be_datn.repository.home;

import com.example.be_datn.entity.order.LichSuHoaDon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrangChuRepository extends JpaRepository<LichSuHoaDon, Integer> {
    @Query(value = """
        SELECT 
            SUM(CASE WHEN CAST(h.ngay_tao AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS today_count,
            SUM(CASE WHEN CAST(h.ngay_tao AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) THEN 1 ELSE 0 END) AS yesterday_count
        FROM hoa_don h
        WHERE h.deleted = 0
    """, nativeQuery = true)
    Object[] getSoLuongHoaDonHomNayVaHomQua();

    @Query(value = """
        SELECT
            COALESCE(SUM(CASE WHEN MONTH(h.ngay_tao) = MONTH(GETDATE())
                     AND YEAR(h.ngay_tao) = YEAR(GETDATE())
                     THEN h.tong_tien_sau_giam ELSE 0 END), 0) AS current_month_revenue,
            COALESCE(SUM(CASE WHEN MONTH(h.ngay_tao) = MONTH(DATEADD(MONTH, -1, GETDATE()))
                     AND YEAR(h.ngay_tao) = YEAR(DATEADD(MONTH, -1, GETDATE()))
                     THEN h.tong_tien_sau_giam ELSE 0 END), 0) AS previous_month_revenue
        FROM hoa_don h
        WHERE h.trang_thai = 3 AND h.deleted = 0
    """, nativeQuery = true)
    Object[] getTongTienThangNayVaThangTruoc();

    @Query(value = """
        SELECT 
            COALESCE(COUNT(k.id), 0) AS total_customers,
            COALESCE(SUM(CASE 
                WHEN MONTH(k.created_at) = MONTH(GETDATE())
                 AND YEAR(k.created_at) = YEAR(GETDATE())
                THEN 1 ELSE 0 END), 0) AS current_month_new_customers,
            COALESCE(SUM(CASE 
                WHEN MONTH(k.created_at) = MONTH(DATEADD(MONTH, -1, GETDATE()))
                 AND YEAR(k.created_at) = YEAR(DATEADD(MONTH, -1, GETDATE()))
                THEN 1 ELSE 0 END), 0) AS previous_month_new_customers
        FROM khach_hang k
        WHERE k.deleted = 0
    """, nativeQuery = true)
    Object[] thongKeKhachHang();

    @Query(value = """
        SELECT 
            COALESCE(COUNT(ctsp.id), 0) AS total_products,
            COALESCE(SUM(CASE 
                WHEN MONTH(ctsp.created_at) = MONTH(GETDATE())
                 AND YEAR(ctsp.created_at) = YEAR(GETDATE())
                THEN 1 ELSE 0 END), 0) AS current_month_new_products,
            COALESCE(SUM(CASE 
                WHEN MONTH(ctsp.created_at) = MONTH(DATEADD(MONTH, -1, GETDATE()))
                 AND YEAR(ctsp.created_at) = YEAR(DATEADD(MONTH, -1, GETDATE()))
                THEN 1 ELSE 0 END), 0) AS previous_month_new_products
        FROM chi_tiet_san_pham ctsp
        WHERE ctsp.deleted = 0
    """, nativeQuery = true)
    Object[] thongKeChiTietSanPham();

    @Query("""
        SELECT lshd FROM LichSuHoaDon lshd WHERE lshd.deleted = false ORDER BY lshd.thoiGian DESC
    """)
    List<LichSuHoaDon> getAllLichSuHoaDon(Pageable pageable);
}