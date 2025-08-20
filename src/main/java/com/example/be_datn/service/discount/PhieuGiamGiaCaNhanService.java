package com.example.be_datn.service.discount;

import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaCaNhanService {
    List<PhieuGiamGiaCaNhan> getPGGCN();

    @Transactional
    PhieuGiamGiaCaNhan addPGGCN(PhieuGiamGiaCaNhan phieuGiamGiaCaNhan);

    @Transactional
    void deleteByPhieuGiamGiaId(Integer phieuGiamGiaId);

    List<PhieuGiamGiaCaNhan> findByPhieuGiamGiaId(Integer pggId);

    List<PhieuGiamGiaCaNhan> getall();

    Optional<PhieuGiamGiaCaNhan> checkDiscountCode(String ma);

    PhieuGiamGiaCaNhan findByKhachHangAndPhieuGiamGia(KhachHang khachHang, PhieuGiamGia phieuGiamGia);
}
