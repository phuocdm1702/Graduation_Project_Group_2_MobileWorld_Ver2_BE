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

    @Query("SELECT h FROM HeDieuHanh h WHERE h.deleted = false ORDER BY h.id DESC")
    List<HeDieuHanh> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT h FROM HeDieuHanh h WHERE h.deleted = false ORDER BY h.id DESC")
    Page<HeDieuHanh> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    Optional<HeDieuHanh> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT h FROM HeDieuHanh h WHERE h.deleted = false AND " +
            "(LOWER(h.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.heDieuHanh) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.phienBan) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY h.id DESC")
    Page<HeDieuHanh> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByHeDieuHanhAndPhienBanAndDeletedFalse(
            String heDieuHanh, String phienBan);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HeDieuHanh h " +
            "WHERE h.heDieuHanh = :heDieuHanh " +
            "AND h.phienBan = :phienBan " +
            "AND h.deleted = false " +
            "AND h.id != :excludeId")
    boolean existsByHeDieuHanhAndPhienBanAndDeletedFalseAndIdNot(
            @Param("heDieuHanh") String heDieuHanh,
            @Param("phienBan") String phienBan,
            @Param("excludeId") Integer excludeId);

    Optional<HeDieuHanh> findByHeDieuHanhAndPhienBanAndDeletedTrue(
            String heDieuHanh, String phienBan);
}