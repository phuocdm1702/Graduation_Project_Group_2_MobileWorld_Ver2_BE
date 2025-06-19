package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.BoNhoTrong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoNhoTrongRepository extends JpaRepository<BoNhoTrong, Integer> {

    // Tìm tất cả bộ nhớ trong chưa bị xóa
    List<BoNhoTrong> findByDeletedFalse();

    // Tìm tất cả bộ nhớ trong chưa bị xóa với phân trang
    Page<BoNhoTrong> findByDeletedFalse(Pageable pageable);

    // Tìm bộ nhớ trong theo ID và chưa bị xóa
    Optional<BoNhoTrong> findByIdAndDeletedFalse(Integer id);

    // Kiểm tra mã bộ nhớ trong đã tồn tại (chưa bị xóa)
    @Query("SELECT COUNT(b) > 0 FROM BoNhoTrong b WHERE b.ma = :ma AND b.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    // Kiểm tra dung lượng bộ nhớ trong đã tồn tại (chưa bị xóa)
    @Query("SELECT COUNT(b) > 0 FROM BoNhoTrong b WHERE b.dungLuongBoNhoTrong = :dungLuongBoNhoTrong AND b.deleted = false")
    boolean existsByDungLuongBoNhoTrongAndDeletedFalse(@Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong);

    // Kiểm tra mã bộ nhớ trong đã tồn tại (chưa bị xóa) loại trừ ID hiện tại
    @Query("SELECT COUNT(b) > 0 FROM BoNhoTrong b WHERE b.ma = :ma AND b.deleted = false AND b.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    // Kiểm tra dung lượng bộ nhớ trong đã tồn tại (chưa bị xóa) loại trừ ID hiện tại
    @Query("SELECT COUNT(b) > 0 FROM BoNhoTrong b WHERE b.dungLuongBoNhoTrong = :dungLuongBoNhoTrong AND b.deleted = false AND b.id != :excludeId")
    boolean existsByDungLuongBoNhoTrongAndDeletedFalse(@Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong, @Param("excludeId") Integer excludeId);

    // Tìm bộ nhớ trong đã bị xóa theo mã
    @Query("SELECT b FROM BoNhoTrong b WHERE b.ma = :ma AND b.deleted = true")
    Optional<BoNhoTrong> findByMaAndDeletedTrue(@Param("ma") String ma);

    // Tìm bộ nhớ trong đã bị xóa theo dung lượng
    @Query("SELECT b FROM BoNhoTrong b WHERE b.dungLuongBoNhoTrong = :dungLuongBoNhoTrong AND b.deleted = true")
    Optional<BoNhoTrong> findByDungLuongBoNhoTrongAndDeletedTrue(@Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong);

    // Tìm kiếm theo từ khóa
    @Query("SELECT b FROM BoNhoTrong b WHERE b.deleted = false AND " +
            "(LOWER(b.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.dungLuongBoNhoTrong) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BoNhoTrong> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Lọc theo dung lượng bộ nhớ trong
    @Query("SELECT b FROM BoNhoTrong b WHERE b.deleted = false AND " +
            "LOWER(b.dungLuongBoNhoTrong) = LOWER(:dungLuongBoNhoTrong)")
    Page<BoNhoTrong> findByDungLuongBoNhoTrongIgnoreCase(@Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong, Pageable pageable);
}