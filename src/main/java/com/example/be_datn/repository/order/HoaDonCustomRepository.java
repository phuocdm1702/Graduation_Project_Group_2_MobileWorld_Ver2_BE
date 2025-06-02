package com.example.be_datn.repository.order;

import com.example.be_datn.entity.order.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HoaDonCustomRepository {
    Page<HoaDon> getHoaDon(Pageable pageable);
}
