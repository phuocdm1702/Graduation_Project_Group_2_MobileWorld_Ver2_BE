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

    @Query("SELECT m FROM MauSac m WHERE m.deleted = false ORDER BY m.id DESC")
    List<MauSac> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT m FROM MauSac m WHERE m.deleted = false ORDER BY m.id DESC")
    Page<MauSac> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    Optional<MauSac> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT m FROM MauSac m WHERE m.deleted = false AND " +
            "(LOWER(m.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.mauSac) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY m.id DESC")
    Page<MauSac> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByMauSacAndMaMauAndDeletedFalse(String mauSac, String maMau);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MauSac m " +
            "WHERE m.mauSac = :mauSac " +
            "AND m.maMau = :maMau " +
            "AND m.deleted = false " +
            "AND m.id != :excludeId")
    boolean existsByMauSacAndMaMauAndDeletedFalseAndIdNot(
            @Param("mauSac") String mauSac,
            @Param("maMau") String maMau,
            @Param("excludeId") Integer excludeId);

    Optional<MauSac> findByMauSacAndMaMauAndDeletedTrue(String mauSac, String maMau);
}