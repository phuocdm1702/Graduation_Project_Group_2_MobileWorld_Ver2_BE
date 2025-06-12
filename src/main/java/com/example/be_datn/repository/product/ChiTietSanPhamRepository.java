package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {
    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham AND c.deleted = :deleted")
    List<ChiTietSanPham> findByIdSanPhamIdAndDeletedFalse(@Param("idSanPham") Integer idSanPham, @Param("deleted") boolean deleted);

    @Query("SELECT COUNT(c) FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham AND c.deleted = :deleted")
    long countByIdSanPhamIdAndDeletedFalse(@Param("idSanPham") Integer idSanPham, @Param("deleted") boolean deleted);
}