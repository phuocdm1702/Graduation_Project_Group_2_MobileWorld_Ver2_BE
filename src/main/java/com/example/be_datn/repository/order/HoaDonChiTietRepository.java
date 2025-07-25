package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.product.Imel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    WHERE i.deleted = :deleted
    """)
    Page<Imel> getAllImelSP(Pageable pageable, @Param("deleted") Boolean deleted);
}
