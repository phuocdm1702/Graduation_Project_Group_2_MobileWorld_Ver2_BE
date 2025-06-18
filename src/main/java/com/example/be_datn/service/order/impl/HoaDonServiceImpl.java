package com.example.be_datn.service.order.impl;

import com.example.be_datn.common.order.HoaDonDetailMapper;
import com.example.be_datn.common.order.HoaDonMapper;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.repository.pay.HinhThucThanhToanRepository;
import com.example.be_datn.service.order.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonMapper hoaDonMapper;

    @Autowired
    private HoaDonDetailMapper hoaDonDetailMapper;
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;

//    @Override
//    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
//        Page<HoaDon> hoaDonPage = hoaDonRepository.getHoaDon(pageable);
//        List<HoaDonResponse> filteredList = hoaDonPage.getContent()
//                .stream()
//                .filter(hoaDon -> "Tại quầy".equals(hoaDon.getLoaiDon()))
//                .map(hoaDonMapper::mapToDto)
//                .collect(Collectors.toList());
//        return new PageImpl<>(filteredList, pageable, hoaDonPage.getTotalElements());
//    }
//
//    @Override
//    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount, Timestamp startDate, Timestamp endDate, Short trangThai, Pageable pageable) {
//        return hoaDonRepository.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai, pageable)
//                .map(hoaDonMapper::mapToDto);
//    }

    @Override
    @Cacheable(value = "hoaDonPage", key = "#loaiDon + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
        return hoaDonRepository.getHoaDon("Tại quầy", pageable);
    }

    @Override
    @Cacheable(value = "hoaDonFiltered", key = "#keyword + '-' + #minAmount + '-' + #maxAmount + '-' + #startDate + '-' + #endDate + '-' + #trangThai + '-' + #pageable")
    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword,
                                                    Long minAmount,
                                                    Long maxAmount,
                                                    Timestamp startDate,
                                                    Timestamp endDate,
                                                    Short trangThai,
                                                    Pageable pageable) {
        Page<HoaDonResponse> result = hoaDonRepository.getHoaDonAndFilters(
                keyword,
                minAmount,
                maxAmount,
                startDate,
                endDate,
                trangThai,
                false,
                pageable);
        return result;
    }

    @Override
    public HoaDonDetailResponse getHoaDonDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        // Ánh xạ chi tiết sản phẩm
        List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDon.getChiTietHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToSanPhamChiTietInfo)
                .collect(Collectors.toList());

        // Ánh xạ thông tin thanh toán
        List<HoaDonDetailResponse.ThanhToanInfo> thanhToanInfos = hoaDon.getHinhThucThanhToan()
                .stream()
                .map(hoaDonDetailMapper::mapToThanhToanInfo)
                .collect(Collectors.toList());

        // Ánh xạ lịch sử hóa đơn
        List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuHoaDonInfos = hoaDon.getLichSuHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToLichSuHoaDonInfo)
                .collect(Collectors.toList());

        // Sử dụng Builder để tạo response
        return new HoaDonDetailResponse.Builder()
                .withHoaDonInfo(hoaDon, hoaDon.getIdPhieuGiamGia())
                .withNhanVienInfo(hoaDon.getIdNhanVien())
                .withThanhToanInfos(thanhToanInfos)
                .withSanPhamChiTietInfos(sanPhamChiTietInfos)
                .withLichSuHoaDonInfos(lichSuHoaDonInfos)
                .build();
    }

    @Override
    public HoaDonResponse getHoaDonByMa(String maHD) {
        HoaDonResponse hoaDon = hoaDonRepository.findByMa(maHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHD));
        return hoaDon;
    }
}
