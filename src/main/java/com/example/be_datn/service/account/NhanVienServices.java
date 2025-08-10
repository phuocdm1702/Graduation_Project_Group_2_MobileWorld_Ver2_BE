package com.example.be_datn.service.account;

import com.example.be_datn.dto.account.response.NhanVienResponse;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.dto.account.NhanVienLookupDTO;
import java.util.List;
import java.util.Optional;

public interface NhanVienServices {
    List<NhanVien> getall();

    NhanVien addNhanVien(NhanVienResponse nhanVienResponse);

    boolean delete(Integer id);

    NhanVien updateNhanVien(Integer id, NhanVienResponse nhanVienResponse);

    Optional<NhanVien> findById(Integer id);

    List<NhanVien> searchNhanVien(String keyword, String status);

    NhanVien trangthai(Integer id);

    void importNhanVien(List<NhanVien> nhanViens);

    List<com.example.be_datn.dto.account.NhanVienLookupDTO> getAllNhanVienLookup();
}
    