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

    @Query("SELECT p FROM Pin p WHERE p.deleted = false ORDER BY p.id DESC")
    List<Pin> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT p FROM Pin p WHERE p.deleted = false ORDER BY p.id DESC")
    Page<Pin> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    Optional<Pin> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT p FROM Pin p WHERE p.deleted = false AND " +
            "(LOWER(p.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.loaiPin) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY p.id DESC")
    Page<Pin> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByLoaiPinAndDungLuongPinAndDeletedFalse(
            String loaiPin, String dungLuongPin);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pin p " +
            "WHERE p.loaiPin = :loaiPin " +
            "AND p.dungLuongPin = :dungLuongPin " +
            "AND p.deleted = false " +
            "AND p.id != :excludeId")
    boolean existsByLoaiPinAndDungLuongPinAndDeletedFalseAndIdNot(
            @Param("loaiPin") String loaiPin,
            @Param("dungLuongPin") String dungLuongPin,
            @Param("excludeId") Integer excludeId);

    Optional<Pin> findByLoaiPinAndDungLuongPinAndDeletedTrue(
            String loaiPin, String dungLuongPin);
}