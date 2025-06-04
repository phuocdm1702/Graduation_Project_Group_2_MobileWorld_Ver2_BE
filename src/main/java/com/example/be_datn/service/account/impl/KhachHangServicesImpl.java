package com.example.be_datn.service.account.impl;

import com.example.be_datn.dto.account.response.KhachHangResponse;
import com.example.be_datn.entity.account.DiaChiKhachHang;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.QuyenHan;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.repository.account.DiaChiKH.DiaChiKhachHangRepository;
import com.example.be_datn.repository.account.KhachHang.KhachHangRepository;
import com.example.be_datn.repository.account.TaiKhoan.TaiKhoanRepository;
import com.example.be_datn.service.account.KhachHangServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangServicesImpl implements KhachHangServices {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final DiaChiKhachHangRepository diaChiKhachHangRepository;

    @Autowired
    public KhachHangServicesImpl(KhachHangRepository khachHangRepository, TaiKhoanRepository taiKhoanRepository, DiaChiKhachHangRepository diaChiKhachHangRepository) {
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.diaChiKhachHangRepository = diaChiKhachHangRepository;
    }

    //hien thi du lieu
    @Override
    public List<KhachHang> getall(){
        return khachHangRepository.findAll()
                .stream()
                .filter(kh -> !"KH00001".equals(kh.getMa())).collect(Collectors.toUnmodifiableList());
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

    //tao ma Dckh
    public String MaDiaChiKH() {
        int index = 1;
        String ma;
        do {
            ma = String.format("DCKH%05d", index++);
        } while (diaChiKhachHangRepository.existsByMa(ma));
        return ma;
    }

    // Hàm loại bỏ dấu tiếng Việt
    private String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    //MatutangNV
    public String generateMaKhachHang(String tenKhachHang) {
        String[] parts = tenKhachHang.split("\\s+");

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
        while (khachHangRepository.existsByMa(finalCode)) {
            finalCode = baseCode + String.format("%02d", counter);
            counter++;
        }

        return finalCode;
    }

    //add khach hang
    @Override
    public KhachHang addKhachHang(KhachHangResponse khachHangResponse) {

        if (taiKhoanRepository.findByEmail(khachHangResponse.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        if (!taiKhoanRepository.findBySoDienThoai(khachHangResponse.getSoDienThoai()).isEmpty()) {
            throw new RuntimeException("SDT đã được sử dụng!");
        }
        QuyenHan quyenHan = new QuyenHan();
        quyenHan.setId(2); // Quyền khách hàng

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMa(MaTaiKhoan());
        taiKhoan.setEmail(khachHangResponse.getEmail());
        taiKhoan.setSoDienThoai(khachHangResponse.getSoDienThoai());
        taiKhoan.setTenDangNhap(khachHangResponse.getUserName());
        taiKhoan.setIdQuyenHan(quyenHan);
        taiKhoan.setDeleted(khachHangResponse.getGioiTinh());

//        String randomPassword = emailServices.generateRandomPassword(8);
//        taiKhoan.setMatKhau(randomPassword);

        taiKhoan = taiKhoanRepository.save(taiKhoan);

        KhachHang kh = new KhachHang();
        if (kh.getCreatedAt() == null) {
            kh.setCreatedAt(new Date()); // Tự động thêm nếu không có
        }
        kh.setMa(generateMaKhachHang(khachHangResponse.getTenKH()));
        kh.setIdTaiKhoan(taiKhoan);
        kh.setTen(khachHangResponse.getTenKH());
        kh.setNgaySinh(khachHangResponse.getNgaySinh());
        kh.setCccd(khachHangResponse.getCccd());
        kh.setDeleted(false);
        kh.setAnhKhachHang(khachHangResponse.getAnhKhachHang());
        kh.setGioiTinh(khachHangResponse.getGioiTinh() != null && khachHangResponse.getGioiTinh() ? (short) 1 : (short) 0);
        kh = khachHangRepository.save(kh); // Lưu trước để có ID

        DiaChiKhachHang dchi = new DiaChiKhachHang();
        dchi.setMa(MaDiaChiKH());
        dchi.setThanhPho(khachHangResponse.getThanhPho());
        dchi.setQuan(khachHangResponse.getQuan());
        dchi.setPhuong(khachHangResponse.getPhuong());
        dchi.setDiaChiCuThe(khachHangResponse.getDiaChiCuThe());
        dchi.setMacDinh(true);
        dchi.setIdKhachHang(kh);
        diaChiKhachHangRepository.save(dchi);

        kh.setIdDiaChiKhachHang(dchi); // Gán lại địa chỉ khách hàng vào khachHang
//        try {
//            emailServices.EmailKH(
//                    khachHangDTO.getEmail(),
//                    khachHangDTO.getTenKH(),
//                    khachHangDTO.getEmail(),
//                    randomPassword
//            );
//        } catch (Exception e) {
//            // Log lỗi nếu cần, nhưng không làm ảnh hưởng quá trình thêm nhân viên
//            System.err.println("Lỗi gửi email: " + e.getMessage());
//        }
        return khachHangRepository.save(kh);
    }

}
