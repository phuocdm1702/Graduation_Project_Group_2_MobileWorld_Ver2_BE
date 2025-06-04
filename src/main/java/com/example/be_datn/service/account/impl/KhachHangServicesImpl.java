package com.example.be_datn.service.account.impl;

import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.repository.account.KhachHang.KhachHangRepository;
import com.example.be_datn.service.account.KhachHangServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangServicesImpl implements KhachHangServices {
    private final KhachHangRepository khachHangRepository;

    @Autowired
    public KhachHangServicesImpl(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public List<KhachHang> getall(){
        return khachHangRepository.findAll()
                .stream()
                .filter(kh -> !"KH00001".equals(kh.getMa())).collect(Collectors.toUnmodifiableList());
    }
}
