package com.example.be_datn.dto.account.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
@Getter
@Setter
public class NhanVienResponse {
    private String ma;
    private String tenNhanVien;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;
    // Thêm trường MultipartFile để nhận file ảnh
    private MultipartFile anhNhanVien;
    // Thêm trường để hỗ trợ URL ảnh hiện có khi cập nhật
    private String existingAnhNhanVien;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    private String cccd;
    private String email;
    private String soDienThoai;
    private String matKhau;
    private String tenDangNhap;
    private Boolean gioiTinh;

}
