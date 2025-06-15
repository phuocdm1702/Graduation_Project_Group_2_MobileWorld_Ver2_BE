package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Pin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PinRepository extends JpaRepository<Pin, Integer> {

    List<Pin> findByDeletedFalse();

    Page<Pin> findByDeletedFalse(Pageable pageable);

    Optional<Pin> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(p) > 0 FROM Pin p WHERE p.ma = :ma AND p.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(p) > 0 FROM Pin p WHERE p.loaiPin = :loaiPin AND p.deleted = false")
    boolean existsByLoaiPinAndDeletedFalse(@Param("loaiPin") String loaiPin);

    @Query("SELECT COUNT(p) > 0 FROM Pin p WHERE p.ma = :ma AND p.deleted = false AND p.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(p) > 0 FROM Pin p WHERE p.loaiPin = :loaiPin AND p.deleted = false AND p.id != :excludeId")
    boolean existsByLoaiPinAndDeletedFalse(@Param("loaiPin") String loaiPin, @Param("excludeId") Integer excludeId);

    @Query("SELECT p FROM Pin p WHERE p.ma = :ma AND p.deleted = true")
    Optional<Pin> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT p FROM Pin p WHERE p.loaiPin = :loaiPin AND p.deleted = true")
    Optional<Pin> findByLoaiPinAndDeletedTrue(@Param("loaiPin") String loaiPin);

    @Query("SELECT p FROM Pin p WHERE p.deleted = false AND " +
            "(LOWER(p.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.loaiPin) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Pin> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Pin p WHERE p.deleted = false AND " +
            "LOWER(p.loaiPin) = LOWER(:loaiPin)")
    Page<Pin> findByLoaiPinIgnoreCase(@Param("loaiPin") String loaiPin, Pageable pageable);
}