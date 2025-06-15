package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Cpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CpuRepository extends JpaRepository<Cpu, Integer> {

    List<Cpu> findByDeletedFalse();

    Page<Cpu> findByDeletedFalse(Pageable pageable);

    Optional<Cpu> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(c) > 0 FROM Cpu c WHERE c.ma = :ma AND c.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(c) > 0 FROM Cpu c WHERE c.tenCpu = :tenCpu AND c.deleted = false")
    boolean existsByTenCpuAndDeletedFalse(@Param("tenCpu") String tenCpu);

    @Query("SELECT COUNT(c) > 0 FROM Cpu c WHERE c.ma = :ma AND c.deleted = false AND c.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(c) > 0 FROM Cpu c WHERE c.tenCpu = :tenCpu AND c.deleted = false AND c.id != :excludeId")
    boolean existsByTenCpuAndDeletedFalse(@Param("tenCpu") String tenCpu, @Param("excludeId") Integer excludeId);

    @Query("SELECT c FROM Cpu c WHERE c.ma = :ma AND c.deleted = true")
    Optional<Cpu> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT c FROM Cpu c WHERE c.tenCpu = :tenCpu AND c.deleted = true")
    Optional<Cpu> findByTenCpuAndDeletedTrue(@Param("tenCpu") String tenCpu);

    @Query("SELECT c FROM Cpu c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tenCpu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Cpu> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Cpu c WHERE c.deleted = false AND " +
            "LOWER(c.tenCpu) = LOWER(:tenCpu)")
    Page<Cpu> findByTenCpuIgnoreCase(@Param("tenCpu") String tenCpu, Pageable pageable);
}