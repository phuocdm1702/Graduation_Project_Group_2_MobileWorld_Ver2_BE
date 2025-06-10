package com.example.be_datn.service.account.impl;


import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.repository.account.TaiKhoan.TaiKhoanRepository;
import com.example.be_datn.service.account.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaiKhoanServicesImpl implements TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public String findById(Integer idTK) {
        Optional<TaiKhoan> tk = taiKhoanRepository.findById(idTK);
        return tk != null ? tk.get().getEmail() : null;
    }

}
