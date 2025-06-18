package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.CongNgheManHinh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CongNgheManHinhRepository extends JpaRepository<CongNgheManHinh, Integer> {

    List<CongNgheManHinh> findByDeletedFalse();

    Page<CongNgheManHinh> findByDeletedFalse(Pageable pageable);

    Optional<CongNgheManHinh> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheManHinh c WHERE c.ma = :ma AND c.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheManHinh c WHERE c.congNgheManHinh = :congNgheManHinh AND c.deleted = false")
    boolean existsByCongNgheManHinhAndDeletedFalse(@Param("congNgheManHinh") String congNgheManHinh);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheManHinh c WHERE c.ma = :ma AND c.deleted = false AND c.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(c) > 0 FROM CongNgheManHinh c WHERE c.congNgheManHinh = :congNgheManHinh AND c.deleted = false AND c.id != :excludeId")
    boolean existsByCongNgheManHinhAndDeletedFalse(@Param("congNgheManHinh") String congNgheManHinh, @Param("excludeId") Integer excludeId);

    @Query("SELECT c FROM CongNgheManHinh c WHERE c.ma = :ma AND c.deleted = true")
    Optional<CongNgheManHinh> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT c FROM CongNgheManHinh c WHERE c.congNgheManHinh = :congNgheManHinh AND c.deleted = true")
    Optional<CongNgheManHinh> findByCongNgheManHinhAndDeletedTrue(@Param("congNgheManHinh") String congNgheManHinh);

    @Query("SELECT c FROM CongNgheManHinh c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.congNgheManHinh) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CongNgheManHinh> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM CongNgheManHinh c WHERE c.deleted = false AND " +
            "LOWER(c.congNgheManHinh) = LOWER(:congNgheManHinh)")
    Page<CongNgheManHinh> findByCongNgheManHinhIgnoreCase(@Param("congNgheManHinh") String congNgheManHinh, Pageable pageable);
}