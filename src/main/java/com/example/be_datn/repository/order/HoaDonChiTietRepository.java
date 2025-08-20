package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.product.Imel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    // Thêm phương thức để lấy idSanPham dựa trên id của ChiTietSanPham
    @Query("SELECT ctsp.idSanPham.id FROM ChiTietSanPham ctsp WHERE ctsp.id = :chiTietSanPhamId")
    Integer findIdSanPhamByChiTietSanPhamId(@Param("chiTietSanPhamId") Integer chiTietSanPhamId);

    // Sửa phương thức getAllImelBySanPhamId để lọc các IMEI có cùng RAM, ROM, giá bán, màu sắc
    @Query("""
    SELECT new com.example.be_datn.entity.product.Imel(
        i.id,
        i.ma,
        i.imel,
        i.deleted
    )
    FROM Imel i
    LEFT JOIN ChiTietSanPham ctsp ON ctsp.idImel.id = i.id
    LEFT JOIN ImelDaBan idb ON idb.imel = i.imel AND idb.deleted = false
    WHERE i.deleted = :deleted
    AND (ctsp.deleted IS NULL OR ctsp.deleted = false)
    AND idb.imel IS NULL
    AND ctsp.idSanPham.id = :idSanPham
    AND ctsp.idRam.id = (SELECT ctsp2.idRam.id FROM ChiTietSanPham ctsp2 WHERE ctsp2.id = :chiTietSanPhamId)
    AND ctsp.idBoNhoTrong.id = (SELECT ctsp2.idBoNhoTrong.id FROM ChiTietSanPham ctsp2 WHERE ctsp2.id = :chiTietSanPhamId)
    AND ctsp.idMauSac.id = (SELECT ctsp2.idMauSac.id FROM ChiTietSanPham ctsp2 WHERE ctsp2.id = :chiTietSanPhamId)
    AND ctsp.giaBan = (SELECT ctsp2.giaBan FROM ChiTietSanPham ctsp2 WHERE ctsp2.id = :chiTietSanPhamId)
    """)
    Page<Imel> getAllImelBySanPhamId(Pageable pageable, @Param("deleted") Boolean deleted,
                                     @Param("idSanPham") Integer idSanPham,
                                     @Param("chiTietSanPhamId") Integer chiTietSanPhamId);

    List<HoaDonChiTiet> findByHoaDonIdAndDeletedFalse(Integer idHD);

    @Query("SELECT h FROM HoaDonChiTiet h WHERE h.hoaDon.id = :hoaDonId")
    List<HoaDonChiTiet> findByHoaDonId(@Param("hoaDonId") Integer hoaDonId);

    // Thêm method delete để hỗ trợ xóa trực tiếp (tùy chọn, nhưng hiệu quả hơn find + deleteAll)
    @Modifying
    @Query("DELETE FROM HoaDonChiTiet h WHERE h.hoaDon.id = :hoaDonId")
    void deleteByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
}