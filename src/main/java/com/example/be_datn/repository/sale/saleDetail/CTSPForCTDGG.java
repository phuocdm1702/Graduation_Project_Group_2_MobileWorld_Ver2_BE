package com.example.be_datn.repository.sale.saleDetail;

import com.example.be_datn.entity.product.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CTSPForCTDGG extends JpaRepository<ChiTietSanPham,Integer> {
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.idSanPham.id IN :idSanPham AND ctsp.deleted = false")
    public List<ChiTietSanPham> findAllByIdSanPhamIn(@Param("idSanPham") List<Integer> idSanPham);
}
