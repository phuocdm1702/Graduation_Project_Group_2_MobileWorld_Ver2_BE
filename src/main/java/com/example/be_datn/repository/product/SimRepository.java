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

    List<Sim> findByDeletedFalse();

    Page<Sim> findByDeletedFalse(Pageable pageable);

    Optional<Sim> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(s) > 0 FROM Sim s WHERE s.ma = :ma AND s.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(s) > 0 FROM Sim s WHERE s.ma = :ma AND s.deleted = false AND s.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT s FROM Sim s WHERE s.ma = :ma AND s.deleted = true")
    Optional<Sim> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT s FROM Sim s WHERE s.deleted = false AND " +
            "(LOWER(s.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(s.soLuongSimHoTro AS string) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.cacLoaiSimHoTro) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Sim> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Sim s WHERE s.deleted = false AND s.soLuongSimHoTro = :soLuongSimHoTro")
    Page<Sim> findBySoLuongSimHoTro(@Param("soLuongSimHoTro") Integer soLuongSimHoTro, Pageable pageable);
}