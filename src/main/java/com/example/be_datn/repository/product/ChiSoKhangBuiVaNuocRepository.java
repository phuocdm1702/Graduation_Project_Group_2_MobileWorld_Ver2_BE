package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ChiSoKhangBuiVaNuoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiSoKhangBuiVaNuocRepository extends JpaRepository<ChiSoKhangBuiVaNuoc, Integer> {

    List<ChiSoKhangBuiVaNuoc> findByDeletedFalse();

    Page<ChiSoKhangBuiVaNuoc> findByDeletedFalse(Pageable pageable);

    Optional<ChiSoKhangBuiVaNuoc> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(c) > 0 FROM ChiSoKhangBuiVaNuoc c WHERE c.ma = :ma AND c.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(c) > 0 FROM ChiSoKhangBuiVaNuoc c WHERE c.tenChiSo = :tenChiSo AND c.deleted = false")
    boolean existsByTenChiSoAndDeletedFalse(@Param("tenChiSo") String tenChiSo);

    @Query("SELECT COUNT(c) > 0 FROM ChiSoKhangBuiVaNuoc c WHERE c.ma = :ma AND c.deleted = false AND c.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(c) > 0 FROM ChiSoKhangBuiVaNuoc c WHERE c.tenChiSo = :tenChiSo AND c.deleted = false AND c.id != :excludeId")
    boolean existsByTenChiSoAndDeletedFalse(@Param("tenChiSo") String tenChiSo, @Param("excludeId") Integer excludeId);

    @Query("SELECT c FROM ChiSoKhangBuiVaNuoc c WHERE c.ma = :ma AND c.deleted = true")
    Optional<ChiSoKhangBuiVaNuoc> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT c FROM ChiSoKhangBuiVaNuoc c WHERE c.tenChiSo = :tenChiSo AND c.deleted = true")
    Optional<ChiSoKhangBuiVaNuoc> findByTenChiSoAndDeletedTrue(@Param("tenChiSo") String tenChiSo);

    @Query("SELECT c FROM ChiSoKhangBuiVaNuoc c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tenChiSo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ChiSoKhangBuiVaNuoc> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM ChiSoKhangBuiVaNuoc c WHERE c.deleted = false AND " +
            "LOWER(c.tenChiSo) = LOWER(:tenChiSo)")
    Page<ChiSoKhangBuiVaNuoc> findByTenChiSoIgnoreCase(@Param("tenChiSo") String tenChiSo, Pageable pageable);
}