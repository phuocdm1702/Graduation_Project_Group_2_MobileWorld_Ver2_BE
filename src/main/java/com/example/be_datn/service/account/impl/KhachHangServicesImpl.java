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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.Normalizer;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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


    //add nhanh khach hang ban hang
    @Override
    public KhachHang addKhachHangBH(KhachHangResponse khachHangResponse) {

        QuyenHan quyenHan = new QuyenHan();
        quyenHan.setId(2); // Quyền khách hàng

        // tao tai khoan
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setSoDienThoai(khachHangResponse.getSoDienThoai());
        taiKhoan.setIdQuyenHan(quyenHan);
        taiKhoan = taiKhoanRepository.save(taiKhoan);

        // tao khach hang
        KhachHang kh = new KhachHang();
        kh.setCreatedAt(new Date());
        kh.setMa(generateMaKhachHang(khachHangResponse.getTenKH()));
        kh.setIdTaiKhoan(taiKhoan);
        kh.setTen(khachHangResponse.getTenKH());
        kh.setDeleted(false);
        kh = khachHangRepository.save(kh);

        //them dia chi khach hang
        DiaChiKhachHang dchi = new DiaChiKhachHang();
        dchi.setMa(MaDiaChiKH());
        dchi.setDiaChiCuThe(khachHangResponse.getDiaChiCuThe());
        dchi.setQuan(khachHangResponse.getQuan());
        dchi.setThanhPho(khachHangResponse.getThanhPho());
        dchi.setPhuong(khachHangResponse.getPhuong());
        dchi.setMacDinh(true);
        dchi.setIdKhachHang(kh);
        dchi = diaChiKhachHangRepository.save(dchi);

        kh.setIdDiaChiKhachHang(dchi);
        return khachHangRepository.save(kh);
    }

    //detele khach hang
    public boolean delete(Integer id) {
        Optional<KhachHang> optionalKH = khachHangRepository.findById(id);
        if (optionalKH.isPresent()) {
            KhachHang kh = optionalKH.get();
            kh.setDeleted(true);
            khachHangRepository.save(kh);
            return true;
        }
        return false;
    }

    //detail cua khach hang
    @Override
    public Optional<KhachHang> findByIdKH(Integer id) {
        return khachHangRepository.findById(id);
    }

    //update khach hang
    @Override
    public KhachHang updateKhachHang(Integer id, KhachHangResponse khachHangResponse) {
        return khachHangRepository.findById(id)
                .map(existingNhanVien -> {

                    TaiKhoan taiKhoan = taiKhoanRepository.findById(existingNhanVien.getIdTaiKhoan().getId())
                            .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
                    taiKhoanRepository.findByEmail(khachHangResponse.getEmail()).ifPresent(tk ->{
                        if (!tk.getId().equals(taiKhoan.getId())){
                            throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác!");
                        }
                    });
                    List<TaiKhoan> taiKhoanList = taiKhoanRepository.findBySoDienThoai(khachHangResponse.getSoDienThoai());
                    for (TaiKhoan tk : taiKhoanList) {
                        if (!tk.getId().equals(taiKhoan.getId())) {
                            throw new RuntimeException("Số điện thoại đã được sử dụng bởi tài khoản khác!");
                        }
                    }

                    existingNhanVien.setCccd(khachHangResponse.getCccd());
                    existingNhanVien.setNgaySinh(khachHangResponse.getNgaySinh());
                    existingNhanVien.setTen(khachHangResponse.getTenKH());

                    taiKhoan.setEmail(khachHangResponse.getEmail());
                    taiKhoan.setSoDienThoai(khachHangResponse.getSoDienThoai());
                    taiKhoan.setDeleted(khachHangResponse.getGioiTinh());

                    taiKhoanRepository.save(taiKhoan);

                    return khachHangRepository.save(existingNhanVien);
                }).orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại!"));
    }

    //update dia chi
    @Override
    public KhachHang updateDchi(Integer id, KhachHangResponse khachHangResponse) {
        return khachHangRepository.findById(id)
                .map(existingKhachHang -> {
                    existingKhachHang.setTen(khachHangResponse.getTenKH());
                    if (existingKhachHang.getIdDiaChiKhachHang() != null) {
                        DiaChiKhachHang diachi = diaChiKhachHangRepository.findById(existingKhachHang.getIdDiaChiKhachHang().getId())
                                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
                        diachi.setDiaChiCuThe(khachHangResponse.getDiaChiCuThe());
                        diachi.setPhuong(khachHangResponse.getPhuong());
                        diachi.setThanhPho(khachHangResponse.getThanhPho());
                        diachi.setQuan(khachHangResponse.getQuan());
                        diaChiKhachHangRepository.save(diachi);
                    }
                    return khachHangRepository.save(existingKhachHang);
                }).orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
    }

    //thay doi trang thai
    public KhachHang trangthai(Integer id) {
        Optional<KhachHang> optionalKhachHang = khachHangRepository.findById(id);
        if (!optionalKhachHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + id);
        }
        KhachHang khachHang = optionalKhachHang.get();
        khachHang.setDeleted(!khachHang.getDeleted());
        return khachHangRepository.save(khachHang);
    }

    //search khach hang
    @Override
    public List<KhachHang> searchKhachHang(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of(); //tra ve tat ca du lieu neu k tim thay
        }
        return khachHangRepository.searchBh(keyword.trim());
    }
}
