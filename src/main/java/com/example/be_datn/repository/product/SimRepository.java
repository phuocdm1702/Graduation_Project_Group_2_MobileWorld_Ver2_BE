package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Sim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimRepository extends JpaRepository<Sim, Integer> {

    @Query("SELECT s FROM Sim s WHERE s.deleted = false ORDER BY s.id DESC")
    List<Sim> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT s FROM Sim s WHERE s.deleted = false ORDER BY s.id DESC")
    Page<Sim> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    Optional<Sim> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT s FROM Sim s WHERE s.deleted = false AND " +
            "(LOWER(s.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(s.soLuongSimHoTro AS string) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.cacLoaiSimHoTro) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY s.id DESC")
    Page<Sim> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    boolean existsBySoLuongSimHoTroAndCacLoaiSimHoTroAndDeletedFalse(
            Integer soLuongSimHoTro, String cacLoaiSimHoTro);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sim s " +
            "WHERE s.soLuongSimHoTro = :soLuongSimHoTro " +
            "AND s.cacLoaiSimHoTro = :cacLoaiSimHoTro " +
            "AND s.deleted = false " +
            "AND s.id != :excludeId")
    boolean existsBySoLuongSimHoTroAndCacLoaiSimHoTroAndDeletedFalseAndIdNot(
            @Param("soLuongSimHoTro") Integer soLuongSimHoTro,
            @Param("cacLoaiSimHoTro") String cacLoaiSimHoTro,
            @Param("excludeId") Integer excludeId);

    Optional<Sim> findBySoLuongSimHoTroAndCacLoaiSimHoTroAndDeletedTrue(
            Integer soLuongSimHoTro, String cacLoaiSimHoTro);
}