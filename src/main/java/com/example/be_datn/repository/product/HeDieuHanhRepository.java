package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.HeDieuHanh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeDieuHanhRepository extends JpaRepository<HeDieuHanh, Integer> {

    List<HeDieuHanh> findByDeletedFalse();

    Page<HeDieuHanh> findByDeletedFalse(Pageable pageable);

    Optional<HeDieuHanh> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(h) > 0 FROM HeDieuHanh h WHERE h.ma = :ma AND h.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(h) > 0 FROM HeDieuHanh h WHERE h.heDieuHanh = :heDieuHanh AND h.deleted = false")
    boolean existsByHeDieuHanhAndDeletedFalse(@Param("heDieuHanh") String heDieuHanh);

    @Query("SELECT COUNT(h) > 0 FROM HeDieuHanh h WHERE h.ma = :ma AND h.deleted = false AND h.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(h) > 0 FROM HeDieuHanh h WHERE h.heDieuHanh = :heDieuHanh AND h.deleted = false AND h.id != :excludeId")
    boolean existsByHeDieuHanhAndDeletedFalse(@Param("heDieuHanh") String heDieuHanh, @Param("excludeId") Integer excludeId);

    @Query("SELECT h FROM HeDieuHanh h WHERE h.ma = :ma AND h.deleted = true")
    Optional<HeDieuHanh> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT h FROM HeDieuHanh h WHERE h.heDieuHanh = :heDieuHanh AND h.deleted = true")
    Optional<HeDieuHanh> findByHeDieuHanhAndDeletedTrue(@Param("heDieuHanh") String heDieuHanh);

    @Query("SELECT h FROM HeDieuHanh h WHERE h.deleted = false AND " +
            "(LOWER(h.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.heDieuHanh) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<HeDieuHanh> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT h FROM HeDieuHanh h WHERE h.deleted = false AND " +
            "LOWER(h.heDieuHanh) = LOWER(:heDieuHanh)")
    Page<HeDieuHanh> findByHeDieuHanhIgnoreCase(@Param("heDieuHanh") String heDieuHanh, Pageable pageable);
}