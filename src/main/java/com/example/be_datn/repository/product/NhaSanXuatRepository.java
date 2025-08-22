package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.NhaSanXuat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhaSanXuatRepository extends JpaRepository<NhaSanXuat, Integer> {

    // Tìm tất cả các nhà sản xuất chưa bị xóa
    List<NhaSanXuat> findByDeletedFalse();

    // Tìm tất cả các nhà sản xuất chưa bị xóa với phân trang
    Page<NhaSanXuat> findByDeletedFalse(Pageable pageable);

    // Tìm nhà sản xuất theo ID và chưa bị xóa
    Optional<NhaSanXuat> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa
    @Query("SELECT n FROM NhaSanXuat n WHERE n.deleted = false AND " +
            "(LOWER(n.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.nhaSanXuat) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NhaSanXuat> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra tên nhà sản xuất đã tồn tại (chưa bị xóa) - tương tự CumCamera
    boolean existsByNhaSanXuatAndDeletedFalse(String nhaSanXuat);

    // Kiểm tra tên nhà sản xuất đã tồn tại (chưa bị xóa) loại trừ ID hiện tại - tương tự CumCamera
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NhaSanXuat n " +
            "WHERE n.nhaSanXuat = :nhaSanXuat " +
            "AND n.deleted = false " +
            "AND n.id != :excludeId")
    boolean existsByNhaSanXuatAndDeletedFalseAndIdNot(
            @Param("nhaSanXuat") String nhaSanXuat,
            @Param("excludeId") Integer excludeId);

    // Tìm nhà sản xuất đã bị xóa theo tên - tương tự CumCamera
    Optional<NhaSanXuat> findByNhaSanXuatAndDeletedTrue(String nhaSanXuat);
}