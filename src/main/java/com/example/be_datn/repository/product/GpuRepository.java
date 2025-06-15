package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Gpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GpuRepository extends JpaRepository<Gpu, Integer> {

    List<Gpu> findByDeletedFalse();

    Page<Gpu> findByDeletedFalse(Pageable pageable);

    Optional<Gpu> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(g) > 0 FROM Gpu g WHERE g.ma = :ma AND g.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(g) > 0 FROM Gpu g WHERE g.tenGpu = :tenGpu AND g.deleted = false")
    boolean existsByTenGpuAndDeletedFalse(@Param("tenGpu") String tenGpu);

    @Query("SELECT COUNT(g) > 0 FROM Gpu g WHERE g.ma = :ma AND g.deleted = false AND g.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(g) > 0 FROM Gpu g WHERE g.tenGpu = :tenGpu AND g.deleted = false AND g.id != :excludeId")
    boolean existsByTenGpuAndDeletedFalse(@Param("tenGpu") String tenGpu, @Param("excludeId") Integer excludeId);

    @Query("SELECT g FROM Gpu g WHERE g.ma = :ma AND g.deleted = true")
    Optional<Gpu> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT g FROM Gpu g WHERE g.tenGpu = :tenGpu AND g.deleted = true")
    Optional<Gpu> findByTenGpuAndDeletedTrue(@Param("tenGpu") String tenGpu);

    @Query("SELECT g FROM Gpu g WHERE g.deleted = false AND " +
            "(LOWER(g.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.tenGpu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Gpu> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT g FROM Gpu g WHERE g.deleted = false AND " +
            "LOWER(g.tenGpu) = LOWER(:tenGpu)")
    Page<Gpu> findByTenGpuIgnoreCase(@Param("tenGpu") String tenGpu, Pageable pageable);
}