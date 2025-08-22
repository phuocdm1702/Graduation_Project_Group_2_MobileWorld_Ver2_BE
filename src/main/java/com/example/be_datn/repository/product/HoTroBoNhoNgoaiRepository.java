package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.HoTroBoNhoNgoai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoTroBoNhoNgoaiRepository extends JpaRepository<HoTroBoNhoNgoai, Integer> {

    // Tìm tất cả các hỗ trợ bộ nhớ ngoài chưa bị xóa
    List<HoTroBoNhoNgoai> findByDeletedFalse();

    // Tìm tất cả các hỗ trợ bộ nhớ ngoài chưa bị xóa với phân trang
    Page<HoTroBoNhoNgoai> findByDeletedFalse(Pageable pageable);

    // Tìm hỗ trợ bộ nhớ ngoài theo ID và chưa bị xóa
    Optional<HoTroBoNhoNgoai> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa
    @Query("SELECT h FROM HoTroBoNhoNgoai h WHERE h.deleted = false AND " +
            "(LOWER(h.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.hoTroBoNhoNgoai) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<HoTroBoNhoNgoai> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra tên hỗ trợ bộ nhớ ngoài đã tồn tại (chưa bị xóa)
    boolean existsByHoTroBoNhoNgoaiAndDeletedFalse(String hoTroBoNhoNgoai);

    // Kiểm tra tên hỗ trợ bộ nhớ ngoài đã tồn tại (chưa bị xóa) loại trừ ID hiện tại
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HoTroBoNhoNgoai h " +
            "WHERE h.hoTroBoNhoNgoai = :hoTroBoNhoNgoai " +
            "AND h.deleted = false " +
            "AND h.id != :excludeId")
    boolean existsByHoTroBoNhoNgoaiAndDeletedFalseAndIdNot(
            @Param("hoTroBoNhoNgoai") String hoTroBoNhoNgoai,
            @Param("excludeId") Integer excludeId);

    // Tìm hỗ trợ bộ nhớ ngoài đã bị xóa theo tên
    Optional<HoTroBoNhoNgoai> findByHoTroBoNhoNgoaiAndDeletedTrue(String hoTroBoNhoNgoai);
}