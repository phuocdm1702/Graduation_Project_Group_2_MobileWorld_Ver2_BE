package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.CongNgheMang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CongNgheMangRepository extends JpaRepository<CongNgheMang, Integer> {

    // Tìm tất cả các công nghệ mạng chưa bị xóa (sắp xếp theo ID giảm dần - mới nhất lên đầu)
    @Query("SELECT c FROM CongNgheMang c WHERE c.deleted = false ORDER BY c.id DESC")
    List<CongNgheMang> findByDeletedFalseOrderByIdDesc();

    // Tìm tất cả các công nghệ mạng chưa bị xóa với phân trang (sắp xếp theo ID giảm dần - mới nhất lên đầu)
    @Query("SELECT c FROM CongNgheMang c WHERE c.deleted = false ORDER BY c.id DESC")
    Page<CongNgheMang> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    // Tìm công nghệ mạng theo ID và chưa bị xóa
    Optional<CongNgheMang> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa (sắp xếp theo ID giảm dần - mới nhất lên đầu)
    @Query("SELECT c FROM CongNgheMang c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tenCongNgheMang) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.id DESC")
    Page<CongNgheMang> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra tên công nghệ mạng đã tồn tại (chưa bị xóa)
    boolean existsByTenCongNgheMangAndDeletedFalse(String tenCongNgheMang);

    // Kiểm tra tên công nghệ mạng đã tồn tại (chưa bị xóa) loại trừ ID hiện tại
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CongNgheMang c " +
            "WHERE c.tenCongNgheMang = :tenCongNgheMang " +
            "AND c.deleted = false " +
            "AND c.id != :excludeId")
    boolean existsByTenCongNgheMangAndDeletedFalseAndIdNot(
            @Param("tenCongNgheMang") String tenCongNgheMang,
            @Param("excludeId") Integer excludeId);

    // Tìm công nghệ mạng đã bị xóa theo tên
    Optional<CongNgheMang> findByTenCongNgheMangAndDeletedTrue(String tenCongNgheMang);
}