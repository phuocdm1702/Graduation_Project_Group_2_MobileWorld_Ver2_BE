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

    // Fixed query: Convert soNhan to String for comparison, remove LOWER() from Integer field
    @Query("SELECT c FROM Cpu c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tenCpu) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(c.soNhan AS string) LIKE CONCAT('%', :keyword, '%'))")
    Page<Cpu> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByTenCpuAndSoNhanAndDeletedFalse(String tenCpu, Integer soNhan);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cpu c " +
            "WHERE c.tenCpu = :tenCpu " +
            "AND c.soNhan = :soNhan " +
            "AND c.deleted = false " +
            "AND c.id != :excludeId")
    boolean existsByTenCpuAndSoNhanAndDeletedFalseAndIdNot(
            @Param("tenCpu") String tenCpu,
            @Param("soNhan") Integer soNhan,
            @Param("excludeId") Integer excludeId);

    Optional<Cpu> findByTenCpuAndSoNhanAndDeletedTrue(String tenCpu, Integer soNhan);
}