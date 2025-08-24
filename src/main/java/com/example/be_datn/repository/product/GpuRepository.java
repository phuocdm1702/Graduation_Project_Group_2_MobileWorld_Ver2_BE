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

    // Tìm tất cả các GPU chưa bị xóa
    List<Gpu> findByDeletedFalse();

    // Tìm tất cả các GPU chưa bị xóa với phân trang
    Page<Gpu> findByDeletedFalse(Pageable pageable);

    // Tìm GPU theo ID và chưa bị xóa
    Optional<Gpu> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa
    @Query("SELECT g FROM Gpu g WHERE g.deleted = false AND " +
            "(LOWER(g.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.tenGpu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Gpu> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra tên GPU đã tồn tại (chưa bị xóa) - tương tự NhaSanXuat
    boolean existsByTenGpuAndDeletedFalse(String tenGpu);

    // Kiểm tra tên GPU đã tồn tại (chưa bị xóa) loại trừ ID hiện tại - tương tự NhaSanXuat
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Gpu g " +
            "WHERE g.tenGpu = :tenGpu " +
            "AND g.deleted = false " +
            "AND g.id != :excludeId")
    boolean existsByTenGpuAndDeletedFalseAndIdNot(
            @Param("tenGpu") String tenGpu,
            @Param("excludeId") Integer excludeId);

    // Tìm GPU đã bị xóa theo tên - tương tự NhaSanXuat
    Optional<Gpu> findByTenGpuAndDeletedTrue(String tenGpu);
}