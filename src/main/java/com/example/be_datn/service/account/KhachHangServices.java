package com.example.be_datn.service.account;

import com.example.be_datn.dto.account.response.DiaChiKhachHangResponse;
import com.example.be_datn.dto.account.response.KhachHangDTO;
import com.example.be_datn.dto.account.response.KhachHangResponse;
import com.example.be_datn.entity.account.DiaChiKhachHang;
import com.example.be_datn.entity.account.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KhachHangServices {
    List<KhachHang> getall();


    List<KhachHangDTO> getKHPgg();

    List<KhachHang> searchFormAddPgg(String keyword);

    List<KhachHang> filterByGioiTinh(Boolean gioiTinh);

    KhachHang findById(Integer id);

    KhachHang addKhachHang(KhachHangResponse khachHangResponse);

    KhachHang addKhachHangBH(KhachHangResponse khachHangResponse);

    Optional<KhachHang> findByIdKH(Integer id);

    KhachHang updateKhachHang(Integer id, KhachHangResponse khachHangResponse);

    DiaChiKhachHang updateDchi(Integer id, KhachHangResponse khachHangDTO);

    boolean delete(Integer id);

    List<KhachHang> searchKhachHang(String keyword);

    void importKhachHangFromExcel(List<KhachHangResponse> khachHangResponses);

    List<DiaChiKhachHang> getAllAddressesByKhachHangId(Integer idKhachHang);

    void setMacDinh(Integer id, Boolean macDinh);

    void deleteDiaChi(Integer id);

    DiaChiKhachHang addDiaChi(DiaChiKhachHangResponse khachHangDTO);

    Map<String, Object> layThongTinKhachHang(Integer idKhachHang);
}
