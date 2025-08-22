package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Ram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RamRepository extends JpaRepository<Ram, Integer> {

    // Tìm tất cả các RAM chưa bị xóa
    List<Ram> findByDeletedFalse();

    // Tìm tất cả các RAM chưa bị xóa với phân trang
    Page<Ram> findByDeletedFalse(Pageable pageable);

    // Tìm RAM theo ID và chưa bị xóa
    Optional<Ram> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa
    @Query("SELECT r FROM Ram r WHERE r.deleted = false AND " +
            "(LOWER(r.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.dungLuongRam) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Ram> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra dung lượng RAM đã tồn tại (chưa bị xóa) - tương tự NhaSanXuat
    boolean existsByDungLuongRamAndDeletedFalse(String dungLuongRam);

    // Kiểm tra dung lượng RAM đã tồn tại (chưa bị xóa) loại trừ ID hiện tại - tương tự NhaSanXuat
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Ram r " +
            "WHERE r.dungLuongRam = :dungLuongRam " +
            "AND r.deleted = false " +
            "AND r.id != :excludeId")
    boolean existsByDungLuongRamAndDeletedFalseAndIdNot(
            @Param("dungLuongRam") String dungLuongRam,
            @Param("excludeId") Integer excludeId);

    // Tìm RAM đã bị xóa theo dung lượng - tương tự NhaSanXuat
    Optional<Ram> findByDungLuongRamAndDeletedTrue(String dungLuongRam);
}