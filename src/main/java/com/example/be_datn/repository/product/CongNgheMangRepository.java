package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.CongNgheMang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CongNgheMangRepository extends JpaRepository<CongNgheMang, Integer> {

    List<CongNgheMang> findByDeletedFalse();

    Page<CongNgheMang> findByDeletedFalse(Pageable pageable);

    Optional<CongNgheMang> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheMang c WHERE c.ma = :ma AND c.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheMang c WHERE c.tenCongNgheMang = :tenCongNgheMang AND c.deleted = false")
    boolean existsByTenCongNgheMangAndDeletedFalse(@Param("tenCongNgheMang") String tenCongNgheMang);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheMang c WHERE c.ma = :ma AND c.deleted = false AND c.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheMang c WHERE c.tenCongNgheMang = :tenCongNgheMang AND c.deleted = false AND c.id != :excludeId")
    boolean existsByTenCongNgheMangAndDeletedFalse(@Param("tenCongNgheMang") String tenCongNgheMang, @Param("excludeId") Integer excludeId);

    @Query("SELECT c FROM CongNgheMang c WHERE c.ma = :ma AND c.deleted = true")
    Optional<CongNgheMang> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT c FROM CongNgheMang c WHERE c.tenCongNgheMang = :tenCongNgheMang AND c.deleted = true")
    Optional<CongNgheMang> findByTenCongNgheMangAndDeletedTrue(@Param("tenCongNgheMang") String tenCongNgheMang);

    @Query("SELECT c FROM CongNgheMang c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tenCongNgheMang) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CongNgheMang> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM CongNgheMang c WHERE c.deleted = false AND " +
            "LOWER(c.tenCongNgheMang) = LOWER(:tenCongNgheMang)")
    Page<CongNgheMang> findByTenCongNgheMangIgnoreCase(@Param("tenCongNgheMang") String tenCongNgheMang, Pageable pageable);
}