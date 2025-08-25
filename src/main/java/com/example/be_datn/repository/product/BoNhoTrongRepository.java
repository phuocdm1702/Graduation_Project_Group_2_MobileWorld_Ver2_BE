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

    @Query("SELECT b FROM BoNhoTrong b WHERE b.deleted = false ORDER BY b.id DESC")
    List<BoNhoTrong> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT b FROM BoNhoTrong b WHERE b.deleted = false ORDER BY b.id DESC")
    Page<BoNhoTrong> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    // Tìm bộ nhớ trong theo ID và chưa bị xóa
    Optional<BoNhoTrong> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa
    @Query("SELECT b FROM BoNhoTrong b WHERE b.deleted = false AND " +
            "(LOWER(b.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.dungLuongBoNhoTrong) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY b.id DESC")
    Page<BoNhoTrong> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra dung lượng bộ nhớ trong đã tồn tại (chưa bị xóa)
    boolean existsByDungLuongBoNhoTrongAndDeletedFalse(String dungLuongBoNhoTrong);

    // Kiểm tra dung lượng bộ nhớ trong đã tồn tại (chưa bị xóa) loại trừ ID hiện tại
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BoNhoTrong b " +
            "WHERE b.dungLuongBoNhoTrong = :dungLuongBoNhoTrong " +
            "AND b.deleted = false " +
            "AND b.id != :excludeId")
    boolean existsByDungLuongBoNhoTrongAndDeletedFalseAndIdNot(
            @Param("dungLuongBoNhoTrong") String dungLuongBoNhoTrong,
            @Param("excludeId") Integer excludeId);

    // Tìm bộ nhớ trong đã bị xóa theo dung lượng
    Optional<BoNhoTrong> findByDungLuongBoNhoTrongAndDeletedTrue(String dungLuongBoNhoTrong);
}