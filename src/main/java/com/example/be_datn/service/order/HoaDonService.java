package com.example.be_datn.service.order;

import com.example.be_datn.dto.order.response.HoaDonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HoaDonService {
    Page<HoaDonResponse> getHoaDon(Pageable pageable);
}
