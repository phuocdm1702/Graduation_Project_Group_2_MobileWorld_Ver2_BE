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

    @Query("SELECT t FROM ThietKe t WHERE t.deleted = false ORDER BY t.id DESC")
    List<ThietKe> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT t FROM ThietKe t WHERE t.deleted = false ORDER BY t.id DESC")
    Page<ThietKe> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    Optional<ThietKe> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT t FROM ThietKe t WHERE t.deleted = false AND " +
            "(LOWER(t.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.chatLieuKhung) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.chatLieuMatLung) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY t.id DESC")
    Page<ThietKe> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByChatLieuKhungAndChatLieuMatLungAndDeletedFalse(
            String chatLieuKhung, String chatLieuMatLung);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM ThietKe t " +
            "WHERE t.chatLieuKhung = :chatLieuKhung " +
            "AND t.chatLieuMatLung = :chatLieuMatLung " +
            "AND t.deleted = false " +
            "AND t.id != :excludeId")
    boolean existsByChatLieuKhungAndChatLieuMatLungAndDeletedFalseAndIdNot(
            @Param("chatLieuKhung") String chatLieuKhung,
            @Param("chatLieuMatLung") String chatLieuMatLung,
            @Param("excludeId") Integer excludeId);

    Optional<ThietKe> findByChatLieuKhungAndChatLieuMatLungAndDeletedTrue(
            String chatLieuKhung, String chatLieuMatLung);
}