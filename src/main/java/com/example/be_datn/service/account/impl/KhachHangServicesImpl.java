package com.example.be_datn.service.account.impl;

import com.example.be_datn.common.Email.EmailServices;
import com.example.be_datn.dto.account.response.DiaChiKhachHangResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    private final EmailServices emailServices;


    @Autowired
    public KhachHangServicesImpl(KhachHangRepository khachHangRepository, TaiKhoanRepository taiKhoanRepository, DiaChiKhachHangRepository diaChiKhachHangRepository, EmailServices emailServices) {
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.diaChiKhachHangRepository = diaChiKhachHangRepository;
        this.emailServices = emailServices;
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


    @Override
    public List<KhachHang> searchFormAddPgg(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return khachHangRepository.findAll();
        }
        return khachHangRepository.searchFormAdd(keyword);
    }

    @Override
    public KhachHang findById(Integer id) {
        Optional<KhachHang> khachHang = khachHangRepository.findById(id);
        return khachHang.orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
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
        taiKhoan.setDeleted(true);

        String randomPassword = emailServices.generateRandomPassword(8);
        taiKhoan.setMatKhau(randomPassword);

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
        kh.setGioiTinh(khachHangResponse.getGioiTinh());
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
        try {
            emailServices.EmailKH(
                    khachHangResponse.getEmail(),
                    khachHangResponse.getTenKH(),
                    khachHangResponse.getEmail(),
                    randomPassword
            );
        } catch (Exception e) {
            // Log lỗi nếu cần, nhưng không làm ảnh hưởng quá trình thêm nhân viên
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
        return khachHangRepository.save(kh);
    }


    //add nhanh khach hang ban hang
    @Override
    public KhachHang addKhachHangBH(KhachHangResponse khachHangResponse) {
        if (!taiKhoanRepository.findBySoDienThoai(khachHangResponse.getSoDienThoai()).isEmpty()) {
            throw new RuntimeException("SDT đã được sử dụng!");
        }
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
                    existingNhanVien.setGioiTinh(khachHangResponse.getGioiTinh());

                    taiKhoan.setEmail(khachHangResponse.getEmail());
                    taiKhoan.setSoDienThoai(khachHangResponse.getSoDienThoai());

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

    //import khach hang ra excel
    @Override
    public void importKhachHangFromExcel(List<KhachHangResponse> khachHangResponses) {
        for (KhachHangResponse dto : khachHangResponses) {
            if (dto.getMa() == null || dto.getTenKH() == null || dto.getEmail() == null) {
                throw new IllegalArgumentException("Mã, tên hoặc email khách hàng không được để trống!");
            }

            Optional<KhachHang> existingKhachHang = khachHangRepository.findByMa(dto.getMa());
            if (existingKhachHang.isPresent()) {
                // Cập nhật khách hàng hiện có
                KhachHang existing = existingKhachHang.get();
                existing.setTen(dto.getTenKH());
                existing.setDeleted(dto.getGioiTinh() != null ? dto.getGioiTinh() : false);
                existing.setUpdatedAt(new Date());

                if (existing.getIdTaiKhoan() != null) {
                    TaiKhoan taiKhoan = taiKhoanRepository.findById(existing.getIdTaiKhoan().getId())
                            .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
                    taiKhoan.setEmail(dto.getEmail());
                    taiKhoan.setSoDienThoai(dto.getSoDienThoai());
                    taiKhoan.setTenDangNhap(dto.getUserName() != null ? dto.getUserName() : dto.getEmail());
                    taiKhoanRepository.save(taiKhoan);
                }

                List<DiaChiKhachHang> existingAddresses = diaChiKhachHangRepository.findByIdKhachHang(existing);
                if (!existingAddresses.isEmpty()) {
                    DiaChiKhachHang defaultAddress = existingAddresses.stream()
                            .filter(DiaChiKhachHang::getMacDinh)
                            .findFirst()
                            .orElse(existingAddresses.get(0));
                    defaultAddress.setDiaChiCuThe(dto.getDiaChiCuThe() != null ? dto.getDiaChiCuThe() : "Chưa có dữ liệu");
                    defaultAddress.setThanhPho(dto.getThanhPho() != null ? dto.getThanhPho() : "N/A");
                    defaultAddress.setQuan(dto.getQuan() != null ? dto.getQuan() : "N/A");
                    defaultAddress.setPhuong(dto.getPhuong() != null ? dto.getPhuong() : "N/A");
                    diaChiKhachHangRepository.save(defaultAddress);
                } else {
                    DiaChiKhachHang diaChi = new DiaChiKhachHang();
                    diaChi.setMa(MaDiaChiKH());
                    diaChi.setIdKhachHang(existing);
                    diaChi.setDiaChiCuThe(dto.getDiaChiCuThe() != null ? dto.getDiaChiCuThe() : "Chưa có dữ liệu");
                    diaChi.setThanhPho(dto.getThanhPho() != null ? dto.getThanhPho() : "N/A");
                    diaChi.setQuan(dto.getQuan() != null ? dto.getQuan() : "N/A");
                    diaChi.setPhuong(dto.getPhuong() != null ? dto.getPhuong() : "N/A");
                    diaChi.setMacDinh(true);
                    diaChiKhachHangRepository.save(diaChi);
                    existing.setIdDiaChiKhachHang(diaChi);
                }

                khachHangRepository.save(existing);
            } else {
                // Thêm khách hàng mới
                QuyenHan quyenHan = new QuyenHan();
                quyenHan.setId(2);

                TaiKhoan taiKhoan = new TaiKhoan();
                taiKhoan.setMa(MaTaiKhoan());
                taiKhoan.setEmail(dto.getEmail());
                taiKhoan.setSoDienThoai(dto.getSoDienThoai());
                taiKhoan.setTenDangNhap(dto.getUserName() != null ? dto.getUserName() : dto.getEmail());
                taiKhoan.setIdQuyenHan(quyenHan);
                taiKhoan.setDeleted(dto.getGioiTinh() != null ? dto.getGioiTinh() : false);

                String randomPassword = emailServices.generateRandomPassword(8);
                taiKhoan.setMatKhau(randomPassword);
                taiKhoan = taiKhoanRepository.save(taiKhoan);

                KhachHang khachHang = new KhachHang();
                khachHang.setMa(dto.getMa());
                khachHang.setIdTaiKhoan(taiKhoan);
                khachHang.setTen(dto.getTenKH());
                khachHang.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : new Date());
                khachHang.setDeleted(dto.getGioiTinh() != null ? dto.getGioiTinh() : false);
                khachHang = khachHangRepository.save(khachHang);

                DiaChiKhachHang diaChi = new DiaChiKhachHang();
                diaChi.setMa(MaDiaChiKH());
                diaChi.setIdKhachHang(khachHang);
                diaChi.setDiaChiCuThe(dto.getDiaChiCuThe() != null ? dto.getDiaChiCuThe() : "Chưa có dữ liệu");
                diaChi.setThanhPho(dto.getThanhPho() != null ? dto.getThanhPho() : "N/A");
                diaChi.setQuan(dto.getQuan() != null ? dto.getQuan() : "N/A");
                diaChi.setPhuong(dto.getPhuong() != null ? dto.getPhuong() : "N/A");
                diaChi.setMacDinh(true);
                diaChiKhachHangRepository.save(diaChi);
                khachHang.setIdDiaChiKhachHang(diaChi);
                khachHangRepository.save(khachHang);

                try {
                    emailServices.sendWelcomeEmail(taiKhoan.getEmail(), khachHang.getTen(), taiKhoan.getEmail(), randomPassword);
                } catch (Exception e) {
                    System.err.println("Lỗi gửi email: " + e.getMessage());
                }
            }
        }
    }
    @Override
    public List<DiaChiKhachHang> getAllAddressesByKhachHangId(Integer idKhachHang) {
        return diaChiKhachHangRepository.findAllByIdKhachHangId(idKhachHang);
    }

    //thay doi default
    public void setMacDinh(Integer id, Boolean macDinh) {
        DiaChiKhachHang address = diaChiKhachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại với ID: " + id));

        if (macDinh) {
            // Sử dụng findAllByIdKhachHangId thay vì findByIdKhachHang
            List<DiaChiKhachHang> allAddresses = diaChiKhachHangRepository.findAllByIdKhachHangId(address.getIdKhachHang().getId());
            for (DiaChiKhachHang addr : allAddresses) {
                if (!addr.getId().equals(id)) {
                    addr.setMacDinh(false);
                }
            }
            diaChiKhachHangRepository.saveAll(allAddresses);
        }

        address.setMacDinh(macDinh);
        diaChiKhachHangRepository.save(address);

        if (macDinh) {
            KhachHang khachHang = address.getIdKhachHang();
            khachHang.setIdDiaChiKhachHang(address);
            khachHangRepository.save(khachHang);
        }
    }
    //xoa dckh
    @Override
    public void deleteDiaChi(Integer id)  {
        Optional<DiaChiKhachHang> diaChiOptional = diaChiKhachHangRepository.findById(id);
        if (diaChiOptional.isPresent()) {
            DiaChiKhachHang diaChiKhachHang = diaChiOptional.get();
            diaChiKhachHang.setDeleted(false);
            diaChiKhachHangRepository.save(diaChiKhachHang); // Xóa hoàn toàn
        } else {
            throw new RuntimeException("Không tìm thấy địa chỉ với id: " + id);
        }
    }

    public String MaDchi2() {
        Integer maxMa = khachHangRepository.findMaxMa();
        if (maxMa == null) {
            return "DCKH00001";
        }
        int nextNumber = maxMa + 1;
        return String.format("DCKH%05d", nextNumber);
    }

    public DiaChiKhachHang addDiaChi(DiaChiKhachHangResponse khachHangDTO) {


        // Kiểm tra idKhachHang có tồn tại
        KhachHang khachHang = khachHangRepository.findById(khachHangDTO.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại với ID: " + khachHangDTO.getIdKhachHang()));

        // Tạo đối tượng địa chỉ mới
        DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
        diaChiKhachHang.setIdKhachHang(khachHang); // Gán đối tượng KhachHang
        diaChiKhachHang.setMa(MaDchi2());
        diaChiKhachHang.setThanhPho(khachHangDTO.getThanhPho());
        diaChiKhachHang.setDiaChiCuThe(khachHangDTO.getDiaChiCuThe());
        diaChiKhachHang.setQuan(khachHangDTO.getQuan());
        diaChiKhachHang.setPhuong(khachHangDTO.getPhuong());

        // Lưu vào database
        return diaChiKhachHangRepository.save(diaChiKhachHang);
    }
}
