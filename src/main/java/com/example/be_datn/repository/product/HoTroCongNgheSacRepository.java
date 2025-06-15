package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.HoTroCongNgheSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoTroCongNgheSacRepository extends JpaRepository<HoTroCongNgheSac, Integer> {

    List<HoTroCongNgheSac> findByDeletedFalse();

    Page<HoTroCongNgheSac> findByDeletedFalse(Pageable pageable);

    Optional<HoTroCongNgheSac> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(h) > 0 FROM HoTroCongNgheSac h WHERE h.ma = :ma AND h.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(h) > 0 FROM HoTroCongNgheSac h WHERE h.ma = :ma AND h.deleted = false AND h.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(h) > 0 FROM HoTroCongNgheSac h WHERE h.congSac = :congSac AND h.deleted = false")
    boolean existsByCongSacAndDeletedFalse(@Param("congSac") String congSac);

    @Query("SELECT COUNT(h) > 0 FROM HoTroCongNgheSac h WHERE h.congSac = :congSac AND h.deleted = false AND h.id != :excludeId")
    boolean existsByCongSacAndDeletedFalse(@Param("congSac") String congSac, @Param("excludeId") Integer excludeId);

    @Query("SELECT h FROM HoTroCongNgheSac h WHERE h.ma = :ma AND h.deleted = true")
    Optional<HoTroCongNgheSac> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT h FROM HoTroCongNgheSac h WHERE h.congSac = :congSac AND h.deleted = true")
    Optional<HoTroCongNgheSac> findByCongSacAndDeletedTrue(@Param("congSac") String congSac);

    @Query("SELECT h FROM HoTroCongNgheSac h WHERE h.deleted = false AND " +
            "(LOWER(h.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "(h.congSac IS NOT NULL AND LOWER(h.congSac) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(h.congNgheHoTro IS NOT NULL AND LOWER(h.congNgheHoTro) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<HoTroCongNgheSac> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT h FROM HoTroCongNgheSac h WHERE h.deleted = false AND " +
            "LOWER(h.congSac) = LOWER(:congSac)")
    Page<HoTroCongNgheSac> findByCongSacIgnoreCase(@Param("congSac") String congSac, Pageable pageable);
}