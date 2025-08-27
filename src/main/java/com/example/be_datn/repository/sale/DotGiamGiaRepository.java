package com.example.be_datn.repository.sale;

import com.example.be_datn.dto.sale.respone.ViewCTSPDTO;
import com.example.be_datn.dto.sale.respone.ViewSanPhamDTO;
import com.example.be_datn.entity.discount.ChiTietDotGiamGia;
import com.example.be_datn.entity.discount.DotGiamGia;
import com.example.be_datn.entity.product.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Integer> {
    @Query("SELECT dgg FROM DotGiamGia dgg WHERE dgg.deleted = false")
    public Page<DotGiamGia> hienThi(Pageable pageable);

    @Query("Select dgg From DotGiamGia dgg")
    public List<DotGiamGia> ForExcel();

    @Query("SELECT dgg FROM DotGiamGia dgg WHERE dgg.deleted = true")
    public Page<DotGiamGia> hienThiFinish(Pageable pageable);

    @Query("SELECT new com.example.be_datn.dto.sale.respone.ViewSanPhamDTO(sp, nsx, hdh, " +
            "(SELECT COUNT(ctsp) FROM ChiTietSanPham ctsp WHERE ctsp.idSanPham.id = sp.id AND ctsp.deleted = false)) " +
            "FROM SanPham sp " +
            "INNER JOIN sp.idHeDieuHanh hdh " +
            "INNER JOIN sp.idNhaSanXuat nsx " +
            "WHERE (:timKiem IS NULL OR :timKiem = '' OR sp.ma LIKE CONCAT('%', :timKiem, '%') OR sp.tenSanPham LIKE CONCAT('%', :timKiem, '%')) " +
            "AND (:idHeDieuHanh IS NULL OR hdh.id IN :idHeDieuHanh) " +
            "AND (:idNhaSanXuat IS NULL OR nsx.id IN :idNhaSanXuat) " +
            "AND sp.deleted = false")
    List<ViewSanPhamDTO> getAllSanPham(@Param("timKiem") String timKiem,
                                       @Param("idHeDieuHanh") List<Integer> idHeDieuHanh,
                                       @Param("idNhaSanXuat") List<Integer> idNhaSanXuat);



    @Query("SELECT new com.example.be_datn.dto.sale.respone.ViewCTSPDTO(sp, ctsp, anh, bnt, ms, " +
            "(SELECT COUNT(DISTINCT ctdg.idDotGiamGia.id) " +
            " FROM ChiTietDotGiamGia ctdg " +
            " WHERE ctdg.idChiTietSanPham.idSanPham.id = sp.id " +
            " AND ctdg.idChiTietSanPham.idBoNhoTrong.id = bnt.id " +
            " AND ctdg.idChiTietSanPham.idMauSac.id = ms.id " +
            " AND ctdg.idDotGiamGia.deleted = false " +
            " AND (:excludeDotGiamGiaId IS NULL OR ctdg.idDotGiamGia.id != :excludeDotGiamGiaId))" +  // <-- đây là dấu đóng `)` đúng chỗ
            ") " +
            "FROM SanPham sp " +
            "INNER JOIN ChiTietSanPham ctsp ON ctsp.idSanPham.id = sp.id " +
            "INNER JOIN AnhSanPham anh ON ctsp.idAnhSanPham.id = anh.id " +
            "INNER JOIN BoNhoTrong bnt ON ctsp.idBoNhoTrong.id = bnt.id " +
            "INNER JOIN MauSac ms ON ctsp.idMauSac.id = ms.id " +
            "WHERE sp.id IN :ids " +
            "AND (:idBoNhoTrongs IS NULL OR bnt.id IN :idBoNhoTrongs) " +
            "AND (:idMauSacs IS NULL OR ms.id IN :idMauSacs) " +
            "AND ctsp.deleted = false " +
            "AND ctsp.id IN (" +
            "    SELECT MIN(ctsp2.id) " +
            "    FROM ChiTietSanPham ctsp2 " +
            "    WHERE ctsp2.idSanPham.id = sp.id " +
            "    AND ctsp2.idMauSac.id = ms.id " +
            "    AND ctsp2.idBoNhoTrong.id = bnt.id " +
            "    AND ctsp2.deleted = false " +
            "    GROUP BY ctsp2.idSanPham.id, ctsp2.idMauSac.id, ctsp2.idBoNhoTrong.id)")
    List<ViewCTSPDTO> getAllCTSP(
            @Param("ids") List<Integer> ids,
            @Param("idBoNhoTrongs") List<Integer> idBoNhoTrongs,
            @Param("idMauSacs") List<Integer> idMauSacs,
            @Param("excludeDotGiamGiaId") Integer excludeDotGiamGiaId
    );

    @Modifying
    @Transactional
    @Query("UPDATE DotGiamGia d SET d.deleted = true WHERE d.id = :id")
    public void updateDotGiamGiaDeleted(@Param("id") Integer id);


    @Query("SELECT DISTINCT ctsp.idSanPham FROM ChiTietSanPham ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "JOIN ChiTietDotGiamGia ctdgg ON ctdgg.idChiTietSanPham.id = ctsp.id " +
            "WHERE ctdgg.idDotGiamGia.id = :id")
    List<SanPham> getThatDongSanPham(@Param("id") Integer id);

    @Query("SELECT COUNT(dgg) > 0 FROM DotGiamGia dgg WHERE dgg.ma = :ma")
    boolean existsByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT d FROM DotGiamGia d WHERE "
            + "((:maDGG IS NULL AND :tenDGG IS NULL) "
            + " OR (:maDGG IS NOT NULL AND d.ma LIKE CONCAT('%', :maDGG, '%')) "
            + " OR (:tenDGG IS NOT NULL AND d.tenDotGiamGia LIKE CONCAT('%', :tenDGG, '%'))) AND "
            + "(:loaiGiamGiaApDung IS NULL OR LOWER(d.loaiGiamGiaApDung) = LOWER(:loaiGiamGiaApDung)) AND "
            + "(:giaTriGiamGia IS NULL OR d.giaTriGiamGia <= :giaTriGiamGia) AND "
            + "(:soTienGiamToiDa IS NULL OR d.soTienGiamToiDa <= :soTienGiamToiDa) AND "
            + "(:ngayBatDau IS NULL OR d.ngayBatDau >= :ngayBatDau) AND "
            + "(:ngayKetThuc IS NULL OR d.ngayKetThuc <= :ngayKetThuc) AND "
            + "(:trangThai IS NULL OR d.trangThai = :trangThai) AND "
            + "((:deleted IS NULL AND d.deleted = false) OR (:deleted IS NOT NULL AND d.deleted = :deleted)) "
            + "ORDER BY d.id DESC")
    Page<DotGiamGia> timKiem(
            Pageable pageable,
            @Param("maDGG") String maDGG,
            @Param("tenDGG") String tenDGG,
            @Param("loaiGiamGiaApDung") String loaiGiamGiaApDung,
            @Param("giaTriGiamGia") BigDecimal giaTriGiamGia,
            @Param("soTienGiamToiDa") BigDecimal soTienGiamToiDa,
            @Param("ngayBatDau") Date ngayBatDau,
            @Param("ngayKetThuc") Date ngayKetThuc,
            @Param("trangThai") Boolean trangThai,
            @Param("deleted") Boolean deleted
    );

    @Query("SELECT MAX(d.giaTriGiamGia) FROM DotGiamGia d " +
            "WHERE (:trangThai IS NULL OR d.trangThai = :trangThai) " +
            "AND (:deleted IS NULL OR d.deleted = :deleted)")
    BigDecimal maxGiaTriGiamGia(@Param("trangThai") Boolean trangThai, @Param("deleted") Boolean deleted);

    @Query("SELECT MAX(d.soTienGiamToiDa) FROM DotGiamGia d " +
            "WHERE (:trangThai IS NULL OR d.trangThai = :trangThai) " +
            "AND (:deleted IS NULL OR d.deleted = :deleted)")
    BigDecimal maxSoTienGiamToiDa(@Param("trangThai") Boolean trangThai, @Param("deleted") Boolean deleted);

    @Modifying
    @Transactional
    @Query("UPDATE DotGiamGia e SET e.trangThai = false WHERE e.ngayBatDau <= :today AND e.trangThai = true")
    void updateStatusIfStartDatePassed(@Param("today") Date today);

    @Modifying
    @Transactional
    @Query("""
                UPDATE DotGiamGia e 
                SET e.deleted = true 
                WHERE e.ngayKetThuc <= :today AND e.deleted = false
            """)
    int updateDeletedIfEndDatePassed(@Param("today") Date today);


}