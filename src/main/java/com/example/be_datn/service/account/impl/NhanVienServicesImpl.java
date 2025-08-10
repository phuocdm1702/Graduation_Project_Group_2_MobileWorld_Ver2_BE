package com.example.be_datn.service.account.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.be_datn.common.Email.EmailServices;
import com.example.be_datn.dto.account.NhanVienLookupDTO;
import com.example.be_datn.dto.account.response.NhanVienResponse;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.account.QuyenHan;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
import com.example.be_datn.repository.account.TaiKhoan.TaiKhoanRepository;
import com.example.be_datn.service.account.NhanVienServices;
import com.example.be_datn.service.product.impl.ChiTietSanPhamServiceImpl;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NhanVienServicesImpl implements NhanVienServices {
    private static final Logger logger = LoggerFactory.getLogger(NhanVienServicesImpl.class);
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final EmailServices emailServices;
    private final Cloudinary cloudinary;


    @Autowired
    public NhanVienServicesImpl(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository, EmailServices emailServices, Cloudinary cloudinary) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.emailServices = emailServices;
        this.cloudinary = cloudinary;
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

    private String uploadImageToCloudinary(MultipartFile image, String fileName) {
        try {
            // Kiểm tra file hợp lệ
            if (image.isEmpty()) {
                logger.error("File ảnh rỗng: {}", fileName);
                throw new IllegalArgumentException("File ảnh không được để trống");
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.error("Định dạng file không hợp lệ: {}", contentType);
                throw new IllegalArgumentException("File không phải là ảnh hợp lệ");
            }
            if (image.getSize() > 10 * 1024 * 1024) {
                logger.error("File quá lớn: {} bytes", image.getSize());
                throw new IllegalArgumentException("File ảnh quá lớn (tối đa 10MB)");
            }

            // Tạo public_id duy nhất
            String publicId = "employee_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            // Tải ảnh lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "image"
            ));
            // Lấy URL
            String imageUrl = (String) uploadResult.get("secure_url");
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                throw new RuntimeException("Cloudinary trả về URL ảnh rỗng");
            }
            logger.info("Tải ảnh lên Cloudinary thành công: {} -> {}", fileName, imageUrl);
            return imageUrl;
        } catch (IOException e) {
            logger.error("Lỗi khi tải ảnh {} lên Cloudinary: {}", fileName, e.getMessage());
            throw new RuntimeException("Lỗi khi tải ảnh " + fileName + " lên Cloudinary: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi không mong muốn khi tải ảnh {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Lỗi không mong muốn khi tải ảnh " + fileName + ": " + e.getMessage(), e);
        }
    }

    //add nhan vien
    @Transactional
    @Override
    public NhanVien addNhanVien(NhanVienResponse nhanVienResponse) {
        // 1. Validate dữ liệu đầu vào
        if (nhanVienResponse.getAnhNhanVien() == null || nhanVienResponse.getAnhNhanVien().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng upload ảnh nhân viên!");
        }

        if (taiKhoanRepository.existsByEmail(nhanVienResponse.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        if (taiKhoanRepository.existsBySoDienThoai(nhanVienResponse.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng!");
        }

        // 2. Upload ảnh sau khi validate
        String fileName = nhanVienResponse.getAnhNhanVien().getOriginalFilename();
        String anhNhanVienUrl = uploadImageToCloudinary(nhanVienResponse.getAnhNhanVien(), fileName);

        // 3. Tạo tài khoản
        QuyenHan quyenHan = new QuyenHan();
        quyenHan.setId(3);

        String randomPassword = emailServices.generateRandomPassword(8);

        TaiKhoan taiKhoan = TaiKhoan.builder()
                .ma(MaTaiKhoan())
                .email(nhanVienResponse.getEmail())
                .soDienThoai(nhanVienResponse.getSoDienThoai())
                .tenDangNhap(nhanVienResponse.getTenDangNhap())
                .idQuyenHan(quyenHan)
                .matKhau(randomPassword)
                .deleted(true)
                .build();

        taiKhoan = taiKhoanRepository.save(taiKhoan);

        // 4. Tạo nhân viên
        NhanVien nhanVien = NhanVien.builder()
                .ma(generateMaNhanVien(nhanVienResponse.getTenNhanVien()))
                .idTaiKhoan(taiKhoan)
                .tenNhanVien(nhanVienResponse.getTenNhanVien())
                .ngaySinh(nhanVienResponse.getNgaySinh())
                .thanhPho(nhanVienResponse.getThanhPho())
                .quan(nhanVienResponse.getQuan())
                .phuong(nhanVienResponse.getPhuong())
                .diaChiCuThe(nhanVienResponse.getDiaChiCuThe())
                .cccd(nhanVienResponse.getCccd())
                .anhNhanVien(anhNhanVienUrl)
                .deleted(nhanVienResponse.getGioiTinh())
                .deleted(false)
                .createdAt(Instant.now())
                .build();

        nhanVien = nhanVienRepository.save(nhanVien);

        // 5. Gửi email (không ảnh hưởng transaction)
        try {
            emailServices.sendWelcomeEmail(
                    nhanVienResponse.getEmail(),
                    nhanVienResponse.getTenNhanVien(),
                    nhanVienResponse.getEmail(),
                    randomPassword
            );
        } catch (Exception e) {
            logger.warn("Lỗi gửi email: {}", e.getMessage());
        }

        return nhanVien;
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
                    String anhNhanVienUrl = existingNhanVien.getAnhNhanVien();
                    if (nhanVienResponse.getAnhNhanVien() != null && !nhanVienResponse.getAnhNhanVien().isEmpty()) {
                        // Tải ảnh mới lên Cloudinary
                        String fileName = nhanVienResponse.getAnhNhanVien().getOriginalFilename();
                        anhNhanVienUrl = uploadImageToCloudinary(nhanVienResponse.getAnhNhanVien(), fileName);
                    } else if (nhanVienResponse.getExistingAnhNhanVien() != null && !nhanVienResponse.getExistingAnhNhanVien().isEmpty()) {
                        // Sử dụng URL ảnh hiện có
                        anhNhanVienUrl = nhanVienResponse.getExistingAnhNhanVien();
                        logger.info("Sử dụng URL ảnh hiện có cho nhân viên: {}", anhNhanVienUrl);
                    } else {
                        logger.warn("Không có ảnh mới hoặc URL hiện có được cung cấp, giữ nguyên ảnh: {}", anhNhanVienUrl);
                    }
                    //tk
                    taiKhoan.setEmail(nhanVienResponse.getEmail());
                    taiKhoan.setSoDienThoai(nhanVienResponse.getSoDienThoai());
                    taiKhoanRepository.save(taiKhoan);
                    //khachhang
                    existingNhanVien.setTenNhanVien(nhanVienResponse.getTenNhanVien());
                    existingNhanVien.setNgaySinh(nhanVienResponse.getNgaySinh());
                    existingNhanVien.setThanhPho(nhanVienResponse.getThanhPho());
                    existingNhanVien.setQuan(nhanVienResponse.getQuan());
                    existingNhanVien.setPhuong(nhanVienResponse.getPhuong());
                    existingNhanVien.setAnhNhanVien(anhNhanVienUrl);
                    existingNhanVien.setDiaChiCuThe(nhanVienResponse.getDiaChiCuThe());
                    existingNhanVien.setCccd(nhanVienResponse.getCccd());
                    existingNhanVien.setUpdatedAt(new Date().toInstant());
                    existingNhanVien.setDeleted(nhanVienResponse.getGioiTinh());
                    existingNhanVien.setUpdatedBy(1);

                    return nhanVienRepository.save(existingNhanVien);
                }).orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại!"));
    }

    //detail nhan vien
    @Override
    public Optional<NhanVien> findById(Integer id) {
        return nhanVienRepository.findById(id);
    }

    //search nhan vien
    @Override
    public List<NhanVien> searchNhanVien(String keyword, String status) {
        List<NhanVien> allNhanViens = nhanVienRepository.findAll();

        // Lọc theo trạng thái
        if (status != null && !status.isEmpty()) {
            boolean isDeleted = status.equals("da-nghi");
            allNhanViens = allNhanViens.stream()
                    .filter(nv -> nv.getDeleted() == isDeleted)
                    .collect(Collectors.toList());
        }

        //neu k co du lieu thi se tra lai du lieu ma da loc theo combobox
        if (keyword == null || keyword.trim().isEmpty()) {
            return allNhanViens;
        }

        //search ra cac du lieu (ma,ten,email,sdt)
        String keywordLower = keyword.toLowerCase();
        return allNhanViens.stream()
                .filter(nv ->
                        (nv.getMa() != null && nv.getMa().toLowerCase().trim().contains(keywordLower)) ||
                                (nv.getTenNhanVien() != null && nv.getTenNhanVien().toLowerCase().trim().contains(keywordLower)) ||
                                (nv.getIdTaiKhoan() != null && nv.getIdTaiKhoan().getEmail() != null
                                        && nv.getIdTaiKhoan().getEmail().toLowerCase().trim().contains(keywordLower)) ||
                                (nv.getIdTaiKhoan() != null && nv.getIdTaiKhoan().getSoDienThoai() != null
                                        && nv.getIdTaiKhoan().getSoDienThoai().toLowerCase().trim().contains(keywordLower))
                )
                .collect(Collectors.toList());
    }

    //thay doi trang thai
    public NhanVien trangthai(Integer id) {
        Optional<NhanVien> optionalNhanVien = nhanVienRepository.findById(id);
        if (!optionalNhanVien.isPresent()) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + id);
        }
        NhanVien nv = optionalNhanVien.get();
        nv.setDeleted(!nv.getDeleted());
        return nhanVienRepository.save(nv);
    }

    //import nhan vien ra excel
    @Override
    public void importNhanVien(List<NhanVien> nhanViens) {
        for (NhanVien nhanVien : nhanViens) {
            // Kiểm tra xem nhân viên đã tồn tại hay chưa (dựa trên mã)
            Optional<NhanVien> existingNhanVien = nhanVienRepository.findByMa(nhanVien.getMa());

            if (existingNhanVien.isPresent()) {
                // Nếu nhân viên đã tồn tại, cập nhật thông tin
                NhanVien existing = existingNhanVien.get();
                existing.setTenNhanVien(nhanVien.getTenNhanVien());
                existing.setThanhPho(nhanVien.getThanhPho());
                existing.setQuan(nhanVien.getQuan());
                existing.setPhuong(nhanVien.getPhuong());
                existing.setDiaChiCuThe(nhanVien.getDiaChiCuThe());
                existing.setDeleted(nhanVien.getDeleted());
                existing.setUpdatedAt(new Date().toInstant());

                // Cập nhật thông tin tài khoản
                if (existing.getIdTaiKhoan() != null && nhanVien.getIdTaiKhoan() != null) {
                    TaiKhoan taiKhoan = taiKhoanRepository.findById(existing.getIdTaiKhoan().getId())
                            .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
                    taiKhoan.setEmail(nhanVien.getIdTaiKhoan().getEmail());
                    taiKhoan.setSoDienThoai(nhanVien.getIdTaiKhoan().getSoDienThoai());
                    taiKhoanRepository.save(taiKhoan);
                }

                nhanVienRepository.save(existing);
            } else {
                // Nếu nhân viên không tồn tại, thêm mới
                // Tạo tài khoản mới cho nhân viên
                QuyenHan quyenHan = new QuyenHan();
                quyenHan.setId(3); // Quyền nhân viên

                TaiKhoan taiKhoan = new TaiKhoan();
                taiKhoan.setMa(MaTaiKhoan());
                taiKhoan.setEmail(nhanVien.getIdTaiKhoan().getEmail());
                taiKhoan.setSoDienThoai(nhanVien.getIdTaiKhoan().getSoDienThoai());
                taiKhoan.setTenDangNhap(nhanVien.getIdTaiKhoan().getTenDangNhap() != null ? nhanVien.getIdTaiKhoan().getTenDangNhap() : nhanVien.getIdTaiKhoan().getEmail());
                taiKhoan.setIdQuyenHan(quyenHan);
                taiKhoan.setDeleted(false);

                String randomPassword = emailServices.generateRandomPassword(8);
                taiKhoan.setMatKhau(randomPassword != null ? randomPassword : "defaultPassword");
                taiKhoan = taiKhoanRepository.save(taiKhoan);

                // Gửi email chào mừng
                try {
                    emailServices.sendWelcomeEmail(
                            taiKhoan.getEmail(),
                            nhanVien.getTenNhanVien(),
                            taiKhoan.getEmail(),
                            randomPassword
                    );
                } catch (Exception e) {
                    System.err.println("Lỗi gửi email: " + e.getMessage());
                }

                // Tạo nhân viên mới
                nhanVien.setIdTaiKhoan(taiKhoan);
                nhanVien.setCreatedAt(nhanVien.getCreatedAt() != null ? nhanVien.getCreatedAt() : new Date().toInstant());
                nhanVienRepository.save(nhanVien);
            }
        }
    }


    @Override
    public List<NhanVienLookupDTO> getAllNhanVienLookup() {
        return nhanVienRepository.findAll().stream()
                .map(nhanVien -> new NhanVienLookupDTO(nhanVien.getId(), nhanVien.getTenNhanVien()))
                .collect(Collectors.toList());
    }
}


