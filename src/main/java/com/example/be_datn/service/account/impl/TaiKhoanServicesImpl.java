package com.example.be_datn.service.account.impl;

import com.example.be_datn.config.JwtUtil;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.repository.account.TaiKhoan.TaiKhoanRepository;
import com.example.be_datn.service.account.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaiKhoanServicesImpl implements TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    private final JwtUtil jwtUtil;

    public TaiKhoanServicesImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


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
        nv.setDeleted(!nv.getDeleted());
        return taiKhoanRepository.save(nv);
    }
    @Override
    public String dangnhap(String login, String matKhau) {
        if (login == null || login.trim().isEmpty() || matKhau == null || matKhau.trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập hoặc email và mật khẩu không được để trống");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapOrEmailAndMatKhau(login, matKhau);
        if (taiKhoan == null) {
            throw new RuntimeException("Tên đăng nhập/email hoặc mật khẩu không đúng");
        }
        if (taiKhoan.getDeleted() == true) {
            throw new RuntimeException("Tài khoản " + login + " đã bị vô hiệu hóa");
        }
        if (taiKhoan.getIdQuyenHan() == null || taiKhoan.getIdQuyenHan().getId() != 1) {
            throw new RuntimeException("Bạn không có quyền để đăng nhập!");
        }
        return jwtUtil.generateToken(taiKhoan.getTenDangNhap());
    }

    @Override
    public String dangnhapWeb(String login, String matKhau) {
        if (login == null || login.trim().isEmpty() || matKhau == null || matKhau.trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập hoặc email và mật khẩu không được để trống");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapOrEmailAndMatKhau(login, matKhau);
        if (taiKhoan == null) {
            throw new RuntimeException("Tên đăng nhập/email hoặc mật khẩu không đúng");
        }
        if (taiKhoan.getDeleted() == false) {
            throw new RuntimeException("Tài khoản " + login + " đã bị vô hiệu hóa");
        }
        return taiKhoan.getTenDangNhap();
    }

    @Override
    public List<TaiKhoan> getall(){
        return taiKhoanRepository.findAll();

    }
}
