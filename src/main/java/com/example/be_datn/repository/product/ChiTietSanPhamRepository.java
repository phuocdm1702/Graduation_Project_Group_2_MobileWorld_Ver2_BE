package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham AND c.deleted = :deleted")
    List<ChiTietSanPham> findByIdSanPhamIdAndDeletedFalse(@Param("idSanPham") Integer idSanPham, @Param("deleted") boolean deleted);

    @Query("SELECT COUNT(c) " +
            "FROM ChiTietSanPham c " +
            "WHERE c.idSanPham.id = :idSanPham " +
            "AND c.deleted = false " +
            "AND c.idImel.deleted = false")
    long countByIdSanPhamIdAndDeletedFalse(@Param("idSanPham") Integer idSanPham, @Param("deleted") boolean deleted);

//    @Query("SELECT MIN(sp.ma) AS ma, sp.tenSanPham AS tenSanPham, ms.mauSac AS mauSac, r.dungLuongRam AS dungLuongRam, bnt.dungLuongBoNhoTrong AS dungLuongBoNhoTrong, " +
//            "COUNT(DISTINCT c.idImel.imel) AS soLuong, COALESCE(MIN(ctdgg.giaSauKhiGiam), MIN(c.giaBan)) AS giaBan, sp.id AS idSanPham " +
//            "FROM ChiTietSanPham c " +
//            "JOIN c.idSanPham sp " +
//            "LEFT JOIN c.idMauSac ms " +
//            "LEFT JOIN c.idRam r " +
//            "LEFT JOIN c.idBoNhoTrong bnt " +
//            "LEFT JOIN ChiTietDotGiamGia ctdgg ON ctdgg.idChiTietSanPham.id = c.id " +
//            "AND ctdgg.deleted = false " +
//            "AND ctdgg.idDotGiamGia.trangThai = false " +
//            "AND ctdgg.idDotGiamGia.deleted = false " +
//            "LEFT JOIN ImelDaBan idb ON idb.imel = c.idImel.imel " +
//            "WHERE (:sanPhamId IS NULL OR c.idSanPham.id = :sanPhamId) " +
//            "AND c.deleted = false " +
//            "AND c.idImel.deleted = false " +
//            "AND idb.imel IS NULL " +
//            "GROUP BY sp.id, sp.tenSanPham, ms.mauSac, r.dungLuongRam, bnt.dungLuongBoNhoTrong")
//    List<Object[]> findGroupedProductsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    //Bên trên là code cũ của truy vấn này
    @Query("SELECT MIN(sp.ma) AS ma, sp.tenSanPham AS tenSanPham, ms.mauSac AS mauSac, r.dungLuongRam AS dungLuongRam, bnt.dungLuongBoNhoTrong AS dungLuongBoNhoTrong, " +
            "COUNT(DISTINCT c.idImel.imel) AS soLuong, COALESCE(MIN(ctdgg.giaSauKhiGiam), MIN(c.giaBan)) AS giaBan, sp.id AS idSanPham, " +
            "MIN(asp.duongDan) AS duongDan " +
            "FROM ChiTietSanPham c " +
            "JOIN c.idSanPham sp " +
            "LEFT JOIN c.idMauSac ms " +
            "LEFT JOIN c.idRam r " +
            "LEFT JOIN c.idBoNhoTrong bnt " +
            "LEFT JOIN c.idAnhSanPham asp " +
            "LEFT JOIN ChiTietDotGiamGia ctdgg ON ctdgg.idChiTietSanPham.id = c.id " +
            "AND ctdgg.deleted = false " +
            "AND ctdgg.idDotGiamGia.trangThai = false " +
            "AND ctdgg.idDotGiamGia.deleted = false " +
            "LEFT JOIN ImelDaBan idb ON idb.imel = c.idImel.imel " +
            "WHERE (:sanPhamId IS NULL OR c.idSanPham.id = :sanPhamId) " +
            "AND c.deleted = false " +
            "AND c.idImel.deleted = false " +
            "AND idb.imel IS NULL " +
            "GROUP BY sp.id, sp.tenSanPham, ms.mauSac, r.dungLuongRam, bnt.dungLuongBoNhoTrong")
    List<Object[]> findGroupedProductsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT c.idImel.imel FROM ChiTietSanPham c " +
            "WHERE c.idSanPham.id = :sanPhamId AND c.deleted = false " +
            "AND c.idMauSac.mauSac = :mauSac AND c.idRam.dungLuongRam = :dungLuongRam AND c.idBoNhoTrong.dungLuongBoNhoTrong = :dungLuongBoNhoTrong " +
            "AND c.idImel.deleted = false")
    List<String> findIMEIsBySanPhamIdAndAttributes(@Param("sanPhamId") Integer sanPhamId,
                                                   @Param("mauSac") String mauSac,
                                                   @Param("dungLuongRam") String dungLuongRam,
                                                   @Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong);

    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :sanPhamId AND c.idMauSac.mauSac = :mauSac AND c.idRam.dungLuongRam = :dungLuongRam AND c.idBoNhoTrong.dungLuongBoNhoTrong = :dungLuongBoNhoTrong AND c.deleted = false")
    Optional<ChiTietSanPham> findByIdSanPhamIdAndAttributes();

//    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idImel.imel = :imel AND c.deleted = false AND c.idImel.deleted = false")
//    Optional<ChiTietSanPham> findByImel(@Param("imel") String imel);

    @Query("SELECT c FROM ChiTietSanPham c LEFT JOIN FETCH c.idAnhSanPham WHERE c.idImel.imel = :imel AND c.deleted = false")
    Optional<ChiTietSanPham> findByImel(@Param("imel") String imel);

    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :sanPhamId " +
            "AND c.idMauSac.mauSac = :mauSac " +
            "AND c.idRam.dungLuongRam = :dungLuongRam " +
            "AND c.idBoNhoTrong.dungLuongBoNhoTrong = :dungLuongBoNhoTrong " +
            "AND c.deleted = false")
    List<ChiTietSanPham> findByIdSanPhamIdAndAttributes(
            @Param("sanPhamId") Integer idSanPham,
            @Param("mauSac") String mauSac,
            @Param("dungLuongRam") String dungLuongRam,
            @Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong);

    @Query("SELECT c.idSanPham.tenSanPham AS tenSanPham, c.ma AS maSanPham, c.idImel.imel AS imei, " +
            "c.idMauSac.mauSac AS mauSac, c.idRam.dungLuongRam AS dungLuongRam, " +
            "c.idBoNhoTrong.dungLuongBoNhoTrong AS dungLuongBoNhoTrong, c.giaBan AS donGia, " +
            "c.deleted AS deleted, c.idAnhSanPham.duongDan AS imageUrl " +
            "FROM ChiTietSanPham c " +
            "WHERE c.idSanPham.id = :idSanPham")
    List<Object[]> findProductDetailsBySanPhamId(@Param("idSanPham") Integer idSanPham);

    @Query("SELECT c.idImel.imel FROM ChiTietSanPham c WHERE c.idSanPham.id = :sanPhamId " +
            "AND c.idMauSac.mauSac = :mauSac " +
            "AND c.idRam.dungLuongRam = :dungLuongRam " +
            "AND c.idBoNhoTrong.dungLuongBoNhoTrong = :dungLuongBoNhoTrong " +
            "AND c.deleted = false AND c.idImel.deleted = false")
    List<String> findAvailableIMEIsBySanPhamIdAndAttributes(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSac") String mauSac,
            @Param("dungLuongRam") String dungLuongRam,
            @Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong);

    @Query(value = """
    SELECT 
        sp.id AS sp_id,
        sp.ten_san_pham,
        sp.ma AS sp_ma,
        sp.created_at AS sp_created_at,
        nsx.nha_san_xuat AS ten_nha_san_xuat,
        cpu.ten_cpu,
        gpu.ten_gpu,
        cc.thong_so_camera_sau,
        cc.thong_so_camera_truoc,
        ctsp.id AS ctsp_id,
        ctsp.gia_ban,
        ctsp.ma AS ctsp_ma,
        ctsp.id_imel,
        ms.mau_sac,
        ram.dung_luong_ram AS ram_dung_luong,
        bnt.dung_luong_bo_nho_trong AS bo_nho_trong_dung_luong,
        COALESCE(asp.duong_dan, '/assets/images/placeholder.jpg') AS anh_san_pham_url,
        ctsp.ghi_chu,
        COALESCE(
            CASE 
                WHEN dgg.trang_thai = 0 AND dgg.deleted = 0 THEN ctdgg.gia_sau_khi_giam 
                ELSE ctsp.gia_ban 
            END, 
            ctsp.gia_ban
        ) AS gia_sau_khi_giam,
        ctdgg.gia_ban_dau AS gia_ban_dau,
        CASE WHEN ctdgg.id IS NOT NULL AND dgg.trang_thai = 0 AND dgg.deleted = 0 THEN 1 ELSE 0 END AS has_discount,
        dgg.gia_tri_giam_gia AS giam_phan_tram,
        dgg.so_tien_giam_toi_da AS giam_toi_da,
        COALESCE(dgg.loai_giam_gia_ap_dung, 'NONE') AS loai_giam_gia_ap_dung,
        cskbn.ten_chi_so AS chi_so_khang_bui_nuoc,
        cnm.ten_cong_nghe_mang,
        hd.he_dieu_hanh,
        hd.phien_ban,
        htbnn.ho_tro_bo_nho_ngoai,
        p.dung_luong_pin,
        s.cac_loai_sim_ho_tro,
        tk.chat_lieu_khung,
        tk.chat_lieu_mat_lung,
        htcs.cong_nghe_ho_tro,
        cnmh.cong_nghe_man_hinh,
        cnmh.chuan_man_hinh,
        cnmh.kich_thuoc,
        cnmh.do_phan_giai,
        cnmh.do_sang_toi_da,
        cnmh.tan_so_quet,
        cnmh.kieu_man_hinh,
        i.imel AS imel_value
    FROM 
        san_pham sp
    LEFT JOIN nha_san_xuat nsx ON sp.id_nha_san_xuat = nsx.id
    LEFT JOIN cpu ON sp.id_cpu = cpu.id
    LEFT JOIN gpu ON sp.id_gpu = gpu.id
    LEFT JOIN cum_camera cc ON sp.id_cum_camera = cc.id
    LEFT JOIN chi_so_khang_bui_va_nuoc cskbn ON sp.id_chi_so_khang_bui_va_nuoc = cskbn.id
    LEFT JOIN cong_nghe_mang cnm ON sp.id_cong_nghe_mang = cnm.id
    LEFT JOIN he_dieu_hanh hd ON sp.id_he_dieu_hanh = hd.id
    LEFT JOIN ho_tro_bo_nho_ngoai htbnn ON sp.id_ho_tro_bo_nho_ngoai = htbnn.id
    LEFT JOIN pin p ON sp.id_pin = p.id
    LEFT JOIN sim s ON sp.id_sim = s.id
    LEFT JOIN thiet_ke tk ON sp.id_thiet_ke = tk.id
    LEFT JOIN ho_tro_cong_nghe_sac htcs ON sp.ho_tro_cong_nghe_sac_id = htcs.id
    LEFT JOIN cong_nghe_man_hinh cnmh ON sp.cong_nghe_man_hinh_id = cnmh.id
    OUTER APPLY (
        SELECT 
            id,
            gia_ban,
            ma,
            id_imel,
            id_mau_sac,
            id_ram,
            id_bo_nho_trong,
            id_anh_san_pham,
            ghi_chu,
            created_at
        FROM (
            SELECT 
                ct.id,
                ct.gia_ban,
                ct.ma,
                ct.id_imel,
                ct.id_mau_sac,
                ct.id_ram,
                ct.id_bo_nho_trong,
                ct.id_anh_san_pham,
                ct.ghi_chu,
                ct.created_at,
                ROW_NUMBER() OVER (
                    PARTITION BY ct.id_mau_sac, ct.id_bo_nho_trong 
                    ORDER BY ct.created_at DESC
                ) AS rn
            FROM chi_tiet_san_pham ct
            WHERE 
                ct.id_san_pham = :sanPhamId 
                AND ct.deleted = 0
        ) ct
        WHERE rn = 1
    ) ctsp
    LEFT JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id
    LEFT JOIN ram ON ctsp.id_ram = ram.id
    LEFT JOIN bo_nho_trong bnt ON ctsp.id_bo_nho_trong = bnt.id
    LEFT JOIN anh_san_pham asp ON ctsp.id_anh_san_pham = asp.id
    LEFT JOIN chi_tiet_dot_giam_gia ctdgg ON ctsp.id = ctdgg.id_chi_tiet_san_pham AND ctdgg.deleted = 0
    LEFT JOIN dot_giam_gia dgg ON ctdgg.id_dot_giam_gia = dgg.id AND dgg.trang_thai = 0 AND dgg.deleted = 0
    LEFT JOIN imel i ON ctsp.id_imel = i.id AND i.deleted = 0
    WHERE 
        sp.id = :sanPhamId 
        AND sp.deleted = 0
        AND NOT EXISTS (
            SELECT 1 
            FROM imel_da_ban idb 
            WHERE idb.imel = i.imel
        )
""", nativeQuery = true)
    List<Object[]> findChiTietSanPhamBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT MIN(ctsp.giaBan) FROM ChiTietSanPham ctsp WHERE ctsp.deleted = false")
    Double findMinPrice();

    @Query("SELECT MAX(ctsp.giaBan) FROM ChiTietSanPham ctsp WHERE ctsp.deleted = false")
    Double findMaxPrice();

    @Query("SELECT DISTINCT ctsp.idMauSac.mauSac FROM ChiTietSanPham ctsp WHERE ctsp.deleted = false")
    List<String> findDistinctColors();

    @Query("SELECT c.ma, c.idSanPham.tenSanPham, c.idMauSac.mauSac, c.idRam.dungLuongRam, c.idBoNhoTrong.dungLuongBoNhoTrong, COUNT(c.id), c.giaBan, c.id " +
            "FROM ChiTietSanPham c " +
            "WHERE c.deleted = false AND (:keyword IS NULL OR c.idSanPham.tenSanPham LIKE %:keyword% OR c.ma LIKE %:keyword%) " +
            "GROUP BY c.ma, c.idSanPham.tenSanPham, c.idMauSac.mauSac, c.idRam.dungLuongRam, c.idBoNhoTrong.dungLuongBoNhoTrong, c.giaBan, c.id")
    List<Object[]> findGroupedProductsBySanPhamIdAndKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(c) FROM ChiTietSanPham c WHERE c.id = :id AND c.deleted = false")
    int countAvailableById(@Param("id") Integer chiTietSanPhamId);

    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.idImel.id = :idImel")
    Optional<ChiTietSanPham> findByIdImelId(@Param("idImel") Integer idImel);


    @Query(value = """
    SELECT 
        sp.id AS sp_id,
        sp.ten_san_pham,
        sp.ma AS sp_ma,
        sp.created_at AS sp_created_at,
        nsx.nha_san_xuat AS ten_nha_san_xuat,
        cpu.ten_cpu,
        gpu.ten_gpu,
        cc.thong_so_camera_sau,
        cc.thong_so_camera_truoc,
        ctsp.id AS ctsp_id,
        CASE 
            WHEN (
                NOT EXISTS (
                    SELECT 1 
                    FROM chi_tiet_san_pham ct 
                    WHERE ct.id_san_pham = sp.id 
                    AND ct.deleted = 0
                )
                OR (
                    SELECT COUNT(*) 
                    FROM chi_tiet_san_pham ct 
                    JOIN imel i ON ct.id_imel = i.id 
                    WHERE ct.id_san_pham = sp.id 
                    AND ct.deleted = 0
                    AND NOT EXISTS (
                        SELECT 1 
                        FROM imel_da_ban idb 
                        WHERE idb.imel = i.imel
                    )
                ) = 0
            ) THEN NULL 
            ELSE ctsp.gia_ban 
        END AS gia_ban,
        ctsp.ma AS ctsp_ma,
        ctsp.id_imel,
        ms.mau_sac,
        ram.dung_luong_ram AS ram_dung_luong,
        bnt.dung_luong_bo_nho_trong AS bo_nho_trong_dung_luong,
        COALESCE(
            asp.duong_dan, 
            (SELECT TOP 1 asp2.duong_dan 
             FROM chi_tiet_san_pham ct2 
             JOIN anh_san_pham asp2 ON ct2.id_anh_san_pham = asp2.id 
             WHERE ct2.id_san_pham = sp.id), 
            '/assets/images/placeholder.jpg'
        ) AS anh_san_pham_url,
        ctsp.ghi_chu,
        CASE 
            WHEN (
                NOT EXISTS (
                    SELECT 1 
                    FROM chi_tiet_san_pham ct 
                    WHERE ct.id_san_pham = sp.id 
                    AND ct.deleted = 0
                )
                OR (
                    SELECT COUNT(*) 
                    FROM chi_tiet_san_pham ct 
                    JOIN imel i ON ct.id_imel = i.id 
                    WHERE ct.id_san_pham = sp.id 
                    AND ct.deleted = 0
                    AND NOT EXISTS (
                        SELECT 1 
                        FROM imel_da_ban idb 
                        WHERE idb.imel = i.imel
                    )
                ) = 0
            ) THEN NULL 
            ELSE COALESCE(ctdgg.gia_sau_khi_giam, ctsp.gia_ban) 
        END AS gia_sau_khi_giam,
        ctsp.gia_ban AS gia_ban_dau,
        CASE WHEN ctdgg.id IS NOT NULL AND dgg.trang_thai = 0 AND dgg.deleted = 0 THEN 1 ELSE 0 END AS has_discount,
        dgg.gia_tri_giam_gia AS giam_phan_tram,
        dgg.so_tien_giam_toi_da AS giam_toi_da,
        COALESCE(dgg.loai_giam_gia_ap_dung, 'NONE') AS loai_giam_gia_ap_dung,
        cskbn.ten_chi_so AS chi_so_khang_bui_nuoc,
        cnm.ten_cong_nghe_mang,
        hd.he_dieu_hanh,
        hd.phien_ban,
        htbnn.ho_tro_bo_nho_ngoai,
        p.dung_luong_pin,
        s.cac_loai_sim_ho_tro,
        tk.chat_lieu_khung,
        tk.chat_lieu_mat_lung,
        htcs.cong_nghe_ho_tro,
        cnmh.cong_nghe_man_hinh,
        cnmh.chuan_man_hinh,
        cnmh.kich_thuoc,
        cnmh.do_phan_giai,
        cnmh.do_sang_toi_da,
        cnmh.tan_so_quet,
        cnmh.kieu_man_hinh,
        i.imel AS imel_value
    FROM 
        san_pham sp
    LEFT JOIN nha_san_xuat nsx ON sp.id_nha_san_xuat = nsx.id
    LEFT JOIN cpu ON sp.id_cpu = cpu.id
    LEFT JOIN gpu ON sp.id_gpu = gpu.id
    LEFT JOIN cum_camera cc ON sp.id_cum_camera = cc.id
    LEFT JOIN chi_so_khang_bui_va_nuoc cskbn ON sp.id_chi_so_khang_bui_va_nuoc = cskbn.id
    LEFT JOIN cong_nghe_mang cnm ON sp.id_cong_nghe_mang = cnm.id
    LEFT JOIN he_dieu_hanh hd ON sp.id_he_dieu_hanh = hd.id
    LEFT JOIN ho_tro_bo_nho_ngoai htbnn ON sp.id_ho_tro_bo_nho_ngoai = htbnn.id
    LEFT JOIN pin p ON sp.id_pin = p.id
    LEFT JOIN sim s ON sp.id_sim = s.id
    LEFT JOIN thiet_ke tk ON sp.id_thiet_ke = tk.id
    LEFT JOIN ho_tro_cong_nghe_sac htcs ON sp.ho_tro_cong_nghe_sac_id = htcs.id
    LEFT JOIN cong_nghe_man_hinh cnmh ON sp.cong_nghe_man_hinh_id = cnmh.id
    LEFT JOIN chi_tiet_san_pham ctsp ON ctsp.id_san_pham = sp.id
    LEFT JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id
    LEFT JOIN ram ON ctsp.id_ram = ram.id
    LEFT JOIN bo_nho_trong bnt ON ctsp.id_bo_nho_trong = bnt.id
    LEFT JOIN anh_san_pham asp ON ctsp.id_anh_san_pham = asp.id
    LEFT JOIN chi_tiet_dot_giam_gia ctdgg ON ctsp.id = ctdgg.id_chi_tiet_san_pham AND ctdgg.deleted = 0
    LEFT JOIN dot_giam_gia dgg ON ctdgg.id_dot_giam_gia = dgg.id AND dgg.trang_thai = 0 AND dgg.deleted = 0
    LEFT JOIN imel i ON ctsp.id_imel = i.id
    WHERE 
        sp.id = :sanPhamId 
        AND sp.deleted = 0
    ORDER BY ctsp.created_at DESC, ctsp.deleted ASC
    """, nativeQuery = true)
    List<Object[]> findChiTietSanPhamBySanPhamIdModification(@Param("sanPhamId") Integer sanPhamId);


    @Query(value = """
        SELECT COUNT(*) 
        FROM chi_tiet_san_pham ct 
        JOIN imel i ON ct.id_imel = i.id 
        JOIN mau_sac ms ON ct.id_mau_sac = ms.id
        JOIN ram ON ct.id_ram = ram.id
        JOIN bo_nho_trong bnt ON ct.id_bo_nho_trong = bnt.id
        WHERE ct.id_san_pham = :sanPhamId 
        AND ms.mau_sac = :mauSac 
        AND bnt.dung_luong_bo_nho_trong = :dungLuongBoNhoTrong 
        AND ram.dung_luong_ram = :dungLuongRam 
        AND ct.deleted = 0
        AND NOT EXISTS (
            SELECT 1 
            FROM imel_da_ban idb 
            WHERE idb.imel = i.imel
        )
        """, nativeQuery = true)
    Long countSoLuongTonKho(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSac") String mauSac,
            @Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong,
            @Param("dungLuongRam") String dungLuongRam
    );
}