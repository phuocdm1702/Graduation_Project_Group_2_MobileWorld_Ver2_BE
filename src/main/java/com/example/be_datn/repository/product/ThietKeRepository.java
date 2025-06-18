package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ThietKe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThietKeRepository extends JpaRepository<ThietKe, Integer> {

    List<ThietKe> findByDeletedFalse();

    Page<ThietKe> findByDeletedFalse(Pageable pageable);

    Optional<ThietKe> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT COUNT(t) > 0 FROM ThietKe t WHERE t.ma = :ma AND t.deleted = false")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma);

    @Query("SELECT COUNT(t) > 0 FROM ThietKe t WHERE t.ma = :ma AND t.deleted = false AND t.id != :excludeId")
    boolean existsByMaAndDeletedFalse(@Param("ma") String ma, @Param("excludeId") Integer excludeId);

    @Query("SELECT t FROM ThietKe t WHERE t.ma = :ma AND t.deleted = true")
    Optional<ThietKe> findByMaAndDeletedTrue(@Param("ma") String ma);

    @Query("SELECT t FROM ThietKe t WHERE t.deleted = false AND " +
            "(LOWER(t.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.chatLieuKhung) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.chatLieuMatLung) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ThietKe> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM ThietKe t WHERE t.deleted = false AND " +
            "LOWER(t.chatLieuKhung) = LOWER(:chatLieuKhung)")
    Page<ThietKe> findByChatLieuKhungIgnoreCase(@Param("chatLieuKhung") String chatLieuKhung, Pageable pageable);
}