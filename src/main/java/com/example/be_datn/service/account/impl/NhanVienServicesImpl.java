package com.example.be_datn.service.account.impl;

import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.repository.account.impl.NhanVienRepository;
import com.example.be_datn.service.account.NhanVienServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NhanVienServicesImpl implements NhanVienServices {

    private final NhanVienRepository nhanVienRepository;

    @Autowired
    public NhanVienServicesImpl(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public List<NhanVien> getall(){
        return nhanVienRepository.findAll();
    }
}
