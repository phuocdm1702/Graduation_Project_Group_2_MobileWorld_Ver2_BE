package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    List<MauSac> findByDeletedFalse();

    Page<MauSac> findByDeletedFalse(Pageable pageable);

    Optional<MauSac> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(m) > 0 FROM MauSac m WHERE m.ma = :ma AND m.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(m) > 0 FROM MauSac m WHERE m.mauSac = :mauSac AND m.deleted = false")
    boolean existsByMauSacAndDeletedFalse(@Param("mauSac") String mauSac);

    @Query("SELECT COUNT(m) > 0 FROM MauSac m WHERE m.ma = :ma AND m.deleted = false AND m.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(m) > 0 FROM MauSac m WHERE m.mauSac = :mauSac AND m.deleted = false AND m.id != :excludeId")
    boolean existsByMauSacAndDeletedFalse(@Param("mauSac") String mauSac, @Param("excludeId") Integer excludeId);

    @Query("SELECT m FROM MauSac m WHERE m.ma = :ma AND m.deleted = true")
    Optional<MauSac> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT m FROM MauSac m WHERE m.mauSac = :mauSac AND m.deleted = true")
    Optional<MauSac> findByMauSacAndDeletedTrue(@Param("mauSac") String mauSac);

    @Query("SELECT m FROM MauSac m WHERE m.deleted = false AND " +
            "(LOWER(m.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.mauSac) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MauSac> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM MauSac m WHERE m.deleted = false AND " +
            "LOWER(m.mauSac) = LOWER(:mauSac)")
    Page<MauSac> findByMauSacIgnoreCase(@Param("mauSac") String mauSac, Pageable pageable);
}