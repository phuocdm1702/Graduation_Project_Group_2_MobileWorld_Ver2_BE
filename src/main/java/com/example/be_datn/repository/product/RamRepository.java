package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Ram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RamRepository extends JpaRepository<Ram, Integer> {

    List<Ram> findByDeletedFalse();

    Page<Ram> findByDeletedFalse(Pageable pageable);

    Optional<Ram> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(r) > 0 FROM Ram r WHERE r.ma = :ma AND r.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(r) > 0 FROM Ram r WHERE r.dungLuongRam = :dungLuongRam AND r.deleted = false")
    boolean existsByDungLuongRamAndDeletedFalse(@Param("dungLuongRam") String dungLuongRam);

    @Query("SELECT COUNT(r) > 0 FROM Ram r WHERE r.ma = :ma AND r.deleted = false AND r.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(r) > 0 FROM Ram r WHERE r.dungLuongRam = :dungLuongRam AND r.deleted = false AND r.id != :excludeId")
    boolean existsByDungLuongRamAndDeletedFalse(@Param("dungLuongRam") String dungLuongRam, @Param("excludeId") Integer excludeId);

    @Query("SELECT r FROM Ram r WHERE r.ma = :ma AND r.deleted = true")
    Optional<Ram> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT r FROM Ram r WHERE r.dungLuongRam = :dungLuongRam AND r.deleted = true")
    Optional<Ram> findByDungLuongRamAndDeletedTrue(@Param("dungLuongRam") String dungLuongRam);

    @Query("SELECT r FROM Ram r WHERE r.deleted = false AND " +
            "(LOWER(r.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.dungLuongRam) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Ram> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM Ram r WHERE r.deleted = false AND " +
            "LOWER(r.dungLuongRam) = LOWER(:dungLuongRam)")
    Page<Ram> findByDungLuongRamIgnoreCase(@Param("dungLuongRam") String dungLuongRam, Pageable pageable);
}