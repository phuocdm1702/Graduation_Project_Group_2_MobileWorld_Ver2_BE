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

    // Kiểm tra mã nhà sản xuất đã tồn tại (chưa bị xóa)
    @Query("SELECT COUNT(n) > 0 FROM NhaSanXuat n WHERE n.ma = :ma AND n.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    // Kiểm tra tên nhà sản xuất đã tồn tại (chưa bị xóa)
    @Query("SELECT COUNT(n) > 0 FROM NhaSanXuat n WHERE n.nhaSanXuat = :nhaSanXuat AND n.deleted = false")
    boolean existsByNhaSanXuatAndDeletedFalse(@Param("nhaSanXuat") String nhaSanXuat);

    // Kiểm tra mã nhà sản xuất đã tồn tại (chưa bị xóa) loại trừ ID hiện tại
    @Query("SELECT COUNT(n) > 0 FROM NhaSanXuat n WHERE n.ma = :ma AND n.deleted = false AND n.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    // Kiểm tra tên nhà sản xuất đã tồn tại (chưa bị xóa) loại trừ ID hiện tại
    @Query("SELECT COUNT(n) > 0 FROM NhaSanXuat n WHERE n.nhaSanXuat = :nhaSanXuat AND n.deleted = false AND n.id != :excludeId")
    boolean existsByNhaSanXuatAndDeletedFalse(@Param("nhaSanXuat") String nhaSanXuat, @Param("excludeId") Integer excludeId);

    // Tìm nhà sản xuất đã bị xóa theo mã
    @Query("SELECT n FROM NhaSanXuat n WHERE n.ma = :ma AND n.deleted = true")
    Optional<NhaSanXuat> findByMaAndDeletedTrue(@Param("ma") String ma);

    // Tìm nhà sản xuất đã bị xóa theo tên
    @Query("SELECT n FROM NhaSanXuat n WHERE n.nhaSanXuat = :nhaSanXuat AND n.deleted = true")
    Optional<NhaSanXuat> findByNhaSanXuatAndDeletedTrue(@Param("nhaSanXuat") String nhaSanXuat);

    // Tìm kiếm theo từ khóa
    @Query("SELECT n FROM NhaSanXuat n WHERE n.deleted = false AND " +
            "(LOWER(n.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.nhaSanXuat) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NhaSanXuat> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Lọc theo tên nhà sản xuất
    @Query("SELECT n FROM NhaSanXuat n WHERE n.deleted = false AND " +
            "LOWER(n.nhaSanXuat) = LOWER(:nhaSanXuat)")
    Page<NhaSanXuat> findByNhaSanXuatIgnoreCase(@Param("nhaSanXuat") String nhaSanXuat, Pageable pageable);
}