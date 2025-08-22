package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.CumCamera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CumCameraRepository extends JpaRepository<CumCamera, Integer> {

    List<CumCamera> findByDeletedFalse();

    Page<CumCamera> findByDeletedFalse(Pageable pageable);

    Optional<CumCamera> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT c FROM CumCamera c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.thongSoCameraSau) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.thongSoCameraTruoc) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CumCamera> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByThongSoCameraSauAndThongSoCameraTruocAndDeletedFalse(
            String thongSoCameraSau, String thongSoCameraTruoc);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CumCamera c " +
            "WHERE c.thongSoCameraSau = :thongSoCameraSau " +
            "AND c.thongSoCameraTruoc = :thongSoCameraTruoc " +
            "AND c.deleted = false " +
            "AND c.id != :excludeId")
    boolean existsByThongSoCameraSauAndThongSoCameraTruocAndDeletedFalseAndIdNot(
            @Param("thongSoCameraSau") String thongSoCameraSau,
            @Param("thongSoCameraTruoc") String thongSoCameraTruoc,
            @Param("excludeId") Integer excludeId);

    Optional<CumCamera> findByThongSoCameraSauAndThongSoCameraTruocAndDeletedTrue(
            String thongSoCameraSau, String thongSoCameraTruoc);
}