package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ChiSoKhangBuiVaNuoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiSoKhangBuiVaNuocRepository extends JpaRepository<ChiSoKhangBuiVaNuoc, Integer> {

    // Tìm tất cả các chỉ số kháng bụi và nước chưa bị xóa (sắp xếp theo ID giảm dần - mới nhất lên đầu)
    @Query("SELECT c FROM ChiSoKhangBuiVaNuoc c WHERE c.deleted = false ORDER BY c.id DESC")
    List<ChiSoKhangBuiVaNuoc> findByDeletedFalseOrderByIdDesc();

    // Tìm tất cả các chỉ số kháng bụi và nước chưa bị xóa với phân trang (sắp xếp theo ID giảm dần - mới nhất lên đầu)
    @Query("SELECT c FROM ChiSoKhangBuiVaNuoc c WHERE c.deleted = false ORDER BY c.id DESC")
    Page<ChiSoKhangBuiVaNuoc> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    // Tìm chỉ số kháng bụi và nước theo ID và chưa bị xóa
    Optional<ChiSoKhangBuiVaNuoc> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa (sắp xếp theo ID giảm dần - mới nhất lên đầu)
    @Query("SELECT c FROM ChiSoKhangBuiVaNuoc c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tenChiSo) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.id DESC")
    Page<ChiSoKhangBuiVaNuoc> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra tên chỉ số đã tồn tại (chưa bị xóa) - tương tự NhaSanXuat
    boolean existsByTenChiSoAndDeletedFalse(String tenChiSo);

    // Kiểm tra tên chỉ số đã tồn tại (chưa bị xóa) loại trừ ID hiện tại - tương tự NhaSanXuat
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ChiSoKhangBuiVaNuoc c " +
            "WHERE c.tenChiSo = :tenChiSo " +
            "AND c.deleted = false " +
            "AND c.id != :excludeId")
    boolean existsByTenChiSoAndDeletedFalseAndIdNot(
            @Param("tenChiSo") String tenChiSo,
            @Param("excludeId") Integer excludeId);

    // Tìm chỉ số đã bị xóa theo tên - tương tự NhaSanXuat
    Optional<ChiSoKhangBuiVaNuoc> findByTenChiSoAndDeletedTrue(String tenChiSo);
}