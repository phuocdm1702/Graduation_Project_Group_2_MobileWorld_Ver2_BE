package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.product.Imel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    @Query("""
    SELECT new com.example.be_datn.entity.product.Imel(
        i.id,
        i.ma,
        i.imel,
        i.deleted
    )
    FROM Imel i
    LEFT JOIN ChiTietSanPham ctsp ON ctsp.idImel.id = i.id
    LEFT JOIN ImelDaBan idb ON idb.imel = i.imel
    WHERE i.deleted = :deleted
    AND (ctsp.deleted IS NULL OR ctsp.deleted = false)
    AND idb.imel IS NULL
    AND ctsp.id = :chiTietSanPhamId
""")
    Page<Imel> getAllImelSP(Pageable pageable, @Param("deleted") Boolean deleted, @Param("chiTietSanPhamId") Integer chiTietSanPhamId);

    List<HoaDonChiTiet> findByHoaDonIdAndDeletedFalse(Integer idHD);
}
