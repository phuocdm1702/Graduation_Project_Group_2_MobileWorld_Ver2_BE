package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {
    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham AND c.deleted = :deleted")
    List<ChiTietSanPham> findByIdSanPhamIdAndDeletedFalse(@Param("idSanPham") Integer idSanPham, @Param("deleted") boolean deleted);

    @Query("SELECT COUNT(c) FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham AND c.deleted = :deleted")
    long countByIdSanPhamIdAndDeletedFalse(@Param("idSanPham") Integer idSanPham, @Param("deleted") boolean deleted);
    @Query("SELECT MIN(sp.ma) AS ma, sp.tenSanPham AS tenSanPham , ms.mauSac AS mauSac, r.dungLuongRam AS dungLuongRam, bnt.dungLuongBoNhoTrong AS dungLuongBoNhoTrong, COUNT(DISTINCT c.idImel.imel) AS soLuong, MIN(c.giaBan) AS giaBan, sp.id AS idSanPham " +
            "FROM ChiTietSanPham c " +
            "JOIN c.idSanPham sp " +
            "LEFT JOIN c.idMauSac ms " +
            "LEFT JOIN c.idRam r " +
            "LEFT JOIN c.idBoNhoTrong bnt " +
            "WHERE (:sanPhamId IS NULL OR c.idSanPham.id = :sanPhamId) AND c.deleted = false " +
            "AND NOT EXISTS (SELECT i FROM ImelDaBan i WHERE i.imel = c.idImel.imel AND i.deleted = false) " +
            "GROUP BY sp.id, sp.tenSanPham, ms.mauSac, r.dungLuongRam, bnt.dungLuongBoNhoTrong")
    List<Object[]> findGroupedProductsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT c.idImel.imel FROM ChiTietSanPham c " +
            "WHERE c.idSanPham.id = :sanPhamId AND c.deleted = false " +
            "AND c.idMauSac.mauSac = :mauSac AND c.idRam.dungLuongRam = :dungLuongRam AND c.idBoNhoTrong.dungLuongBoNhoTrong = :dungLuongBoNhoTrong " +
            "AND NOT EXISTS (SELECT i FROM ImelDaBan i WHERE i.imel = c.idImel.imel AND i.deleted = false)")
    List<String> findIMEIsBySanPhamIdAndAttributes(@Param("sanPhamId") Integer sanPhamId,
                                                   @Param("mauSac") String mauSac,
                                                   @Param("dungLuongRam") String dungLuongRam,
                                                   @Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong);

    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :sanPhamId AND c.idMauSac.mauSac = :mauSac AND c.idRam.dungLuongRam = :dungLuongRam AND c.idBoNhoTrong.dungLuongBoNhoTrong = :dungLuongBoNhoTrong AND c.deleted = false")
    Optional<ChiTietSanPham> findByIdSanPhamIdAndAttributes(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSac") String mauSac,
            @Param("dungLuongRam") String dungLuongRam,
            @Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong);

    @Query("SELECT c.idSanPham.tenSanPham AS tenSanPham, c.ma AS maSanPham, c.idImel.imel AS imei, " +
            "c.idMauSac.mauSac AS mauSac, c.idRam.dungLuongRam AS dungLuongRam, " +
            "c.idBoNhoTrong.dungLuongBoNhoTrong AS dungLuongBoNhoTrong, c.giaBan AS donGia, c.deleted AS deleted " +
            "FROM ChiTietSanPham c " +
            "WHERE c.idSanPham.id = :idSanPham")
    List<Object[]> findProductDetailsBySanPhamId(@Param("idSanPham") Integer idSanPham);
}