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

    @Override
    public TaiKhoan trangthaiKH(Integer id) {
        Optional<TaiKhoan> tk = taiKhoanRepository.findById(id);
        if (!tk.isPresent()) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + id);
        }
        TaiKhoan khachHang = tk.get();
        khachHang.setDeleted(!khachHang.getDeleted());
        return taiKhoanRepository.save(khachHang);
    }

    @Override
    public TaiKhoan trangthaiNV(Integer id) {
        Optional<TaiKhoan> tk = taiKhoanRepository.findById(id);
        if (!tk.isPresent()) {
            throw new RuntimeException("Không tìm thấy nhân viên với ID: " + id);
        }
        TaiKhoan nv = tk.get();
        nv.setDeleted(!nv.getDeleted()); // Toggle trạng thái
        return taiKhoanRepository.save(nv);
    }

}
