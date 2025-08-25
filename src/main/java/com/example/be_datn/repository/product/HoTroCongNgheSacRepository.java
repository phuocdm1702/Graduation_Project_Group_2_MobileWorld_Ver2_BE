package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.HoTroCongNgheSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoTroCongNgheSacRepository extends JpaRepository<HoTroCongNgheSac, Integer> {

    @Query("SELECT h FROM HoTroCongNgheSac h WHERE h.deleted = false ORDER BY h.id DESC")
    List<HoTroCongNgheSac> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT h FROM HoTroCongNgheSac h WHERE h.deleted = false ORDER BY h.id DESC")
    Page<HoTroCongNgheSac> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    // Tìm hỗ trợ công nghệ sạc theo ID và chưa bị xóa
    Optional<HoTroCongNgheSac> findByIdAndDeletedFalse(Integer id);

    // Tìm kiếm theo từ khóa
    @Query("SELECT h FROM HoTroCongNgheSac h WHERE h.deleted = false AND " +
            "(LOWER(h.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.congSac) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.congNgheHoTro) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY h.id DESC")
    Page<HoTroCongNgheSac> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra cổng sạc và công nghệ hỗ trợ đã tồn tại (chưa bị xóa) - tương tự CumCamera
    boolean existsByCongSacAndCongNgheHoTroAndDeletedFalse(String congSac, String congNgheHoTro);

    // Kiểm tra cổng sạc và công nghệ hỗ trợ đã tồn tại (chưa bị xóa) loại trừ ID hiện tại - tương tự CumCamera
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HoTroCongNgheSac h " +
            "WHERE h.congSac = :congSac " +
            "AND h.congNgheHoTro = :congNgheHoTro " +
            "AND h.deleted = false " +
            "AND h.id != :excludeId")
    boolean existsByCongSacAndCongNgheHoTroAndDeletedFalseAndIdNot(
            @Param("congSac") String congSac,
            @Param("congNgheHoTro") String congNgheHoTro,
            @Param("excludeId") Integer excludeId);

    // Tìm hỗ trợ công nghệ sạc đã bị xóa theo cổng sạc và công nghệ hỗ trợ - tương tự CumCamera
    Optional<HoTroCongNgheSac> findByCongSacAndCongNgheHoTroAndDeletedTrue(String congSac, String congNgheHoTro);
}