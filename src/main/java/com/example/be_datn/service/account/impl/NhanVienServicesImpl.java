package com.example.be_datn.service.account.impl;

import com.example.be_datn.dto.account.response.NhanVienResponse;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.account.QuyenHan;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.repository.account.impl.NhanVienRepository;
import com.example.be_datn.repository.account.impl.TaiKhoanRepository;
import com.example.be_datn.service.account.NhanVienServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NhanVienServicesImpl implements NhanVienServices {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    @Autowired
    public NhanVienServicesImpl(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    public List<NhanVien> getall(){
        return nhanVienRepository.findAll();
    }


    //tao ma Tk
    public String MaTaiKhoan() {
        int index = 1;
        String ma;
        do {
            ma = String.format("TK%05d", index++);
        } while (taiKhoanRepository.existsByMa(ma));
        return ma;
    }

    // Hàm loại bỏ dấu tiếng Việt
    private String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    //MatutangNV
    public String generateMaNhanVien(String tenNhanVien) {
        String[] parts = tenNhanVien.split("\\s+");

        String lastName = removeAccents(parts[parts.length - 1]).toLowerCase();

        String initials = "";
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                initials += removeAccents(parts[i]).toLowerCase().charAt(0);
            }
        }
        String baseCode = lastName + initials;

        // Kiểm tra trùng lặp và thêm số thứ tự nếu cần
        String finalCode = baseCode;
        int counter = 1;
        while (nhanVienRepository.existsByMa(finalCode)) {
            finalCode = baseCode + String.format("%02d", counter);
            counter++;
        }

        return finalCode;
    }

    //add nhan vien
    @Override
    public NhanVien addNhanVien(NhanVienResponse nhanVienResponse) {
        if (taiKhoanRepository.findByEmail(nhanVienResponse.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        if (!taiKhoanRepository.findBySoDienThoai(nhanVienResponse.getSoDienThoai()).isEmpty()) {
            throw new RuntimeException("SDT đã được sử dụng!");
        }

        QuyenHan quyenHan = new QuyenHan();;
        quyenHan.setId(3);
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMa(MaTaiKhoan());
        taiKhoan.setEmail(nhanVienResponse.getEmail());
        taiKhoan.setSoDienThoai(nhanVienResponse.getSoDienThoai());
        taiKhoan.setTenDangNhap(nhanVienResponse.getTenDangNhap());
        taiKhoan.setIdQuyenHan(quyenHan);
        taiKhoan.setDeleted(nhanVienResponse.getGioiTinh());
        taiKhoan.setTenDangNhap(nhanVienResponse.getTenDangNhap());

//        String randomPassword = emailServices.generateRandomPassword(8);
//        taiKhoan.setMatKhau(randomPassword);

        taiKhoan = taiKhoanRepository.save(taiKhoan);

        NhanVien nhanVien = new NhanVien();
        if (nhanVien.getCreatedAt() == null) {
            nhanVien.setCreatedAt(new Date().toInstant()); // Tự động thêm nếu không có
        }
        nhanVien.setMa(generateMaNhanVien(nhanVienResponse.getTenNhanVien()));
        nhanVien.setIdTaiKhoan(taiKhoan);
        nhanVien.setTenNhanVien(nhanVienResponse.getTenNhanVien());
        nhanVien.setNgaySinh(nhanVienResponse.getNgaySinh());
        nhanVien.setThanhPho(nhanVienResponse.getThanhPho());
        nhanVien.setQuan(nhanVienResponse.getQuan());
        nhanVien.setPhuong(nhanVienResponse.getPhuong());
        nhanVien.setDiaChiCuThe(nhanVienResponse.getDiaChiCuThe());
        nhanVien.setCccd(nhanVienResponse.getCccd());
        nhanVien.setAnhNhanVien(nhanVienResponse.getAnhNhanVien());
        nhanVien.setDeleted(false);

//        try {
//            emailServices.sendWelcomeEmail(
//                    nhanVienResponse.getEmail(),
//                    nhanVienResponse.getTenNhanVien(),
//                    nhanVienResponse.getEmail(),
//                    randomPassword
//            );
//        } catch (Exception e) {
//            System.err.println("Lỗi gửi email: " + e.getMessage());
//        }
        return nhanVienRepository.save(nhanVien);
    }

    //xoa mem nv
    public boolean delete(Integer id) {
        Optional<NhanVien> optionalKH = nhanVienRepository.findById(id);
        if (optionalKH.isPresent()) {
            NhanVien kh = optionalKH.get();
            kh.setDeleted(true);
            nhanVienRepository.save(kh);
            return true;
        }
        return false;
    }

    //update nhan vien
    public NhanVien updateNhanVien(Integer id, NhanVienResponse nhanVienResponse) {
        return nhanVienRepository.findById(id)
                .map(existingNhanVien -> {

                    TaiKhoan taiKhoan = taiKhoanRepository.findById(existingNhanVien.getIdTaiKhoan().getId())
                            .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
                    taiKhoanRepository.findByEmail(nhanVienResponse.getEmail()).ifPresent(tk ->{
                        if (!tk.getId().equals(taiKhoan.getId())){
                            throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác!");
                        }
                    });
                    List<TaiKhoan> taiKhoanList = taiKhoanRepository.findBySoDienThoai(nhanVienResponse.getSoDienThoai());
                    for (TaiKhoan tk : taiKhoanList) {
                        if (!tk.getId().equals(taiKhoan.getId())) {
                            throw new RuntimeException("Số điện thoại đã được sử dụng bởi tài khoản khác!");
                        }
                    }
                    //tk
                    taiKhoan.setEmail(nhanVienResponse.getEmail());
                    taiKhoan.setSoDienThoai(nhanVienResponse.getSoDienThoai());
                    taiKhoan.setDeleted(nhanVienResponse.getGioiTinh());
                    taiKhoanRepository.save(taiKhoan);
                    //khachhang
                    existingNhanVien.setTenNhanVien(nhanVienResponse.getTenNhanVien());
                    existingNhanVien.setNgaySinh(nhanVienResponse.getNgaySinh());
                    existingNhanVien.setThanhPho(nhanVienResponse.getThanhPho());
                    existingNhanVien.setQuan(nhanVienResponse.getQuan());
                    existingNhanVien.setPhuong(nhanVienResponse.getPhuong());
                    existingNhanVien.setAnhNhanVien(nhanVienResponse.getAnhNhanVien());
                    existingNhanVien.setDiaChiCuThe(nhanVienResponse.getDiaChiCuThe());
                    existingNhanVien.setCccd(nhanVienResponse.getCccd());
                    existingNhanVien.setUpdatedAt(new Date().toInstant());
                    existingNhanVien.setUpdatedBy(1);

                    return nhanVienRepository.save(existingNhanVien);
                }).orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại!"));
    }

    //detail nhan vien
    @Override
    public Optional<NhanVien> findById(Integer id) {
        return nhanVienRepository.findById(id);
    }
}
