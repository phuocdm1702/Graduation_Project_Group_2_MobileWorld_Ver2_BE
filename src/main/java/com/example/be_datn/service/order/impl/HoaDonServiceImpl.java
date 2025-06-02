package com.example.be_datn.service.order.impl;

import com.example.be_datn.common.order.HoaDonMapper;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.service.order.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonMapper hoaDonMapper;

    @Override
    public Page<HoaDonResponse> getHoaDon(Pageable pageable) {
        return hoaDonRepository.getHoaDon(pageable).map(hoaDonMapper::mapToDto);
    }
}
