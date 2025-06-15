package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.HoTroBoNhoNgoai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoTroBoNhoNgoaiRepository extends JpaRepository<HoTroBoNhoNgoai, Integer> {

    List<HoTroBoNhoNgoai> findByDeletedFalse();

    Page<HoTroBoNhoNgoai> findByDeletedFalse(Pageable pageable);

    Optional<HoTroBoNhoNgoai> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(h) > 0 FROM HoTroBoNhoNgoai h WHERE h.ma = :ma AND h.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(h) > 0 FROM HoTroBoNhoNgoai h WHERE h.hoTroBoNhoNgoai = :hoTroBoNhoNgoai AND h.deleted = false")
    boolean existsByHoTroBoNhoNgoaiAndDeletedFalse(@Param("hoTroBoNhoNgoai") String hoTroBoNhoNgoai);

    @Query("SELECT COUNT(h) > 0 FROM HoTroBoNhoNgoai h WHERE h.ma = :ma AND h.deleted = false AND h.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(h) > 0 FROM HoTroBoNhoNgoai h WHERE h.hoTroBoNhoNgoai = :hoTroBoNhoNgoai AND h.deleted = false AND h.id != :excludeId")
    boolean existsByHoTroBoNhoNgoaiAndDeletedFalse(@Param("hoTroBoNhoNgoai") String hoTroBoNhoNgoai, @Param("excludeId") Integer excludeId);

    @Query("SELECT h FROM HoTroBoNhoNgoai h WHERE h.ma = :ma AND h.deleted = true")
    Optional<HoTroBoNhoNgoai> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT h FROM HoTroBoNhoNgoai h WHERE h.hoTroBoNhoNgoai = :hoTroBoNhoNgoai AND h.deleted = true")
    Optional<HoTroBoNhoNgoai> findByHoTroBoNhoNgoaiAndDeletedTrue(@Param("hoTroBoNhoNgoai") String hoTroBoNhoNgoai);

    @Query("SELECT h FROM HoTroBoNhoNgoai h WHERE h.deleted = false AND " +
            "(LOWER(h.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.hoTroBoNhoNgoai) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<HoTroBoNhoNgoai> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT h FROM HoTroBoNhoNgoai h WHERE h.deleted = false AND " +
            "LOWER(h.hoTroBoNhoNgoai) = LOWER(:hoTroBoNhoNgoai)")
    Page<HoTroBoNhoNgoai> findByHoTroBoNhoNgoaiIgnoreCase(@Param("hoTroBoNhoNgoai") String hoTroBoNhoNgoai, Pageable pageable);
}