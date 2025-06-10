package com.example.be_datn.service.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;

public interface HoaDonService {
    Page<HoaDonResponse> getHoaDon(Pageable pageable);

    Page<HoaDonResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount,
                                             Timestamp startDate, Timestamp endDate, Short trangThai, Pageable pageable);

    HoaDonDetailResponse getHoaDonDetail(Integer id);
}
