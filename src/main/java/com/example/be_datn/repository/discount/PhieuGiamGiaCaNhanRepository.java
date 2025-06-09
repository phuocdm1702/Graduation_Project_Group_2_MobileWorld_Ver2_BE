package com.example.be_datn.repository.discount;

import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, Integer> {

    @Modifying
    @Query("DELETE FROM PhieuGiamGiaCaNhan p WHERE p.idPhieuGiamGia.id = :phieuGiamGiaId")
    void deleteByIdPhieuGiamGia(@Param("phieuGiamGiaId") Integer phieuGiamGiaId);

    List<PhieuGiamGiaCaNhan> findByIdPhieuGiamGia(PhieuGiamGia phieuGiamGia);

    List<PhieuGiamGiaCaNhan> findByIdPhieuGiamGia_Id(Integer pggId);

    List<PhieuGiamGiaCaNhan> findByIdKhachHangId(Integer idKhachHang);

    Optional<PhieuGiamGiaCaNhan> findByMa(String ma);

}
