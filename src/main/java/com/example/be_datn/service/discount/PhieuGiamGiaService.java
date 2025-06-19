package com.example.be_datn.service.discount;

import com.example.be_datn.dto.discount.request.PhieuGiamGiaRequest;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaService {
    Page<PhieuGiamGia> getPGG(Pageable pageable);

    Page<PhieuGiamGia> searchData(String keyword, Pageable pageable);

    Page<PhieuGiamGia> filterByLoaiPhieu(String loaiPhieu, Pageable pageable);

    Page<PhieuGiamGia> filterByTrangThai(String trangThai, Pageable pageable);

    Page<PhieuGiamGia> filterByDateRange(Date ngayBatDau, Date ngayKetThuc, Pageable pageable);

    Page<PhieuGiamGia> filterByMinOrder(Double minOrder, Pageable pageable);

    Page<PhieuGiamGia> filterByValue(Double valueFilter, Pageable pageable);

    Page<PhieuGiamGia> filterPhieuGiamGia(
            String loaiPhieuGiamGia,
            String trangThai,
            Date ngayBatDau,
            Date ngayKetThuc,
            Double minOrder,
            Double valueFilter,
            Pageable pageable);

    Optional<PhieuGiamGia> getById(Integer id);

    PhieuGiamGia addPGG(PhieuGiamGia phieuGiamGia);

    PhieuGiamGiaRequest updateTrangthai(Integer id, Boolean trangThai);

    PhieuGiamGiaRequest getDetailPGG(Integer id);

    PhieuGiamGia updatePGG(PhieuGiamGia editPGG);

    List<PhieuGiamGia> getall();

    List<PhieuGiamGia> getallPGG();
}
