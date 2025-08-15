package com.example.be_datn.service.account.impl;

import com.example.be_datn.config.JwtUtil;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.repository.account.KhachHang.KhachHangRepository;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
import com.example.be_datn.repository.account.TaiKhoan.TaiKhoanRepository;
import com.example.be_datn.service.account.TaiKhoanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaiKhoanServicesImpl implements TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private final JwtUtil jwtUtil;

    private  final NhanVienRepository nhanVienRepository;

    public TaiKhoanServicesImpl(JwtUtil jwtUtil, NhanVienRepository nhanVienRepository) {
        this.jwtUtil = jwtUtil;
        this.nhanVienRepository = nhanVienRepository;
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
    public Map<String, Object> dangnhap(String login, String matKhau, HttpServletRequest request) {
        if (login == null || login.trim().isEmpty() || matKhau == null || matKhau.trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập hoặc email và mật khẩu không được để trống");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapOrEmailAndMatKhau(login, matKhau);
        if (taiKhoan == null) {
            throw new RuntimeException("Tên đăng nhập/email hoặc mật khẩu không đúng");
        }

        if (Boolean.FALSE.equals(taiKhoan.getDeleted())) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        if (taiKhoan.getIdQuyenHan() == null ||
                (taiKhoan.getIdQuyenHan().getId() != 1 && taiKhoan.getIdQuyenHan().getId() != 2)) {
            throw new RuntimeException("Bạn không có quyền để đăng nhập!");
        }

        // 👉 Lưu tài khoản vào session
        HttpSession session = request.getSession();
        session.setAttribute("taiKhoan", taiKhoan); // Hoặc chỉ lưu ID nếu muốn

        // 👉 Truy vấn nhân viên gắn với tài khoản
        NhanVien nhanVien = nhanVienRepository.findByIdTaiKhoan(taiKhoan);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng nhập thành công");
        response.put("idTaiKhoan", taiKhoan.getId());
        response.put("idNhanVien", nhanVien != null ? nhanVien.getId() : null);
        response.put("role", taiKhoan.getIdQuyenHan().getCapQuyenHan()); // hoặc .getId()

        return response;
    }


    @Override
    public Map<String, Object> dangnhapWeb(String login, String matKhau, HttpServletRequest request) {
        if (login == null || login.trim().isEmpty() || matKhau == null || matKhau.trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập hoặc email và mật khẩu không được để trống");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapOrEmailAndMatKhau(login, matKhau);
        if (taiKhoan == null) {
            throw new RuntimeException("Tên đăng nhập/email hoặc mật khẩu không đúng");
        }
        if (Boolean.FALSE.equals(taiKhoan.getDeleted())) {
            throw new RuntimeException("Tài khoản " + login + " đã bị vô hiệu hóa");
        }
        if (taiKhoan.getIdQuyenHan() == null ||
                (taiKhoan.getIdQuyenHan().getId() != 3)) { // Ví dụ quyền 3 là khách hàng
            throw new RuntimeException("Bạn không có quyền đăng nhập web khách hàng");
        }
        HttpSession session = request.getSession();
        session.setAttribute("taiKhoan", taiKhoan);

        KhachHang khachHang = khachHangRepository.findByIdTaiKhoan(taiKhoan);
        Integer idKhachHang = khachHang != null ? khachHang.getId() : null;

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng nhập thành công");
        response.put("tenDangNhap", taiKhoan.getTenDangNhap());
        response.put("idTaiKhoan", taiKhoan.getId());
        response.put("idKhachHang", idKhachHang);
        response.put("role", taiKhoan.getIdQuyenHan().getCapQuyenHan());

        return response;
    }



    @Override
    public List<TaiKhoan> getall(){
        return taiKhoanRepository.findAll();

    }

    @Override
    public Integer getCustomerIdByTaiKhoan(String login) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapOrEmail(login, login);
        if (taiKhoan != null) {
            KhachHang khachHang = khachHangRepository.findByIdTaiKhoan_Id(taiKhoan.getId());
            return khachHang != null ? khachHang.getId() : null;
        }
        return null;
    }
    public TaiKhoan findByUsername(String username) {
        return taiKhoanRepository.findByTenDangNhap(username);
    }
}
