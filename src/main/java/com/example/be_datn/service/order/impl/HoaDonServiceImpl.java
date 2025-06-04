package com.example.be_datn.service.order.impl;

import com.example.be_datn.common.order.HoaDonMapper;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.service.order.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonMapper hoaDonMapper;

    @Override
    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
        Page<HoaDon> hoaDonPage = hoaDonRepository.getHoaDon(pageable);
        List<HoaDonResponse> filteredList = hoaDonPage.getContent()
                .stream()
                .filter(hoaDon -> "Tại quầy".equals(hoaDon.getLoaiDon()))
                .map(hoaDonMapper::mapToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(filteredList, pageable, hoaDonPage.getTotalElements());
    }

    @Override
    public Page<HoaDonResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount, Timestamp startDate, Timestamp endDate, Short trangThai, Pageable pageable) {
        return hoaDonRepository.getHoaDonAndFilters(keyword, minAmount, maxAmount, startDate, endDate, trangThai, pageable)
                .map(hoaDonMapper::mapToDto);
    }
}
