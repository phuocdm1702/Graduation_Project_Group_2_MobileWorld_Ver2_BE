package com.example.be_datn.service.sell.impl;

import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.inventory.GioHang;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.repository.account.KhachHangRepository;
import com.example.be_datn.repository.account.impl.NhanVienRepository;
import com.example.be_datn.repository.inventory.GioHangChiTietRepository;
import com.example.be_datn.repository.inventory.GioHangRepository;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.service.sell.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Service
public class BanHangServiceImpl implements BanHangService {
    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository chiTietGioHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Override
    @Transactional
    public HoaDonDTO taoHD() {
        Integer khachHangId = 1;
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Khách hàng với ID 1 không tồn tại"));

        Integer nhanVienId = 1;
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Nhân viên với ID 1 không tồn tại"));

        GioHang gioHang = gioHangRepository.findTopByIdKhachHangIdAndDeletedFalseOrderByIdDesc(khachHangId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại cho khách hàng ID 1"));


        HoaDon hoaDon = HoaDon.builder()
                .idKhachHang(khachHang)
                .idNhanVien(nhanVien)
                .ma("HD_" + UUID.randomUUID().toString())
                .tienSanPham(BigDecimal.ZERO)
                .loaiDon("Tại quầy")
                .phiVanChuyen(BigDecimal.ZERO)
                .tongTien(BigDecimal.ZERO)
                .tongTienSauGiam(BigDecimal.ZERO)
                .ghiChu("N/A")
                .tenKhachHang("Khách lẻ")
                .diaChiKhachHang("N/A")
                .soDienThoaiKhachHang("N/A")
                .email("N/A")
                .ngayTao(new Date())
                .trangThai((short) 0) // Trạng thái chờ
                .deleted(false)
                .createdAt(new Date())
                .createdBy(nhanVienId)
                .build();

        hoaDon = hoaDonRepository.save(hoaDon);
        gioHang.setIdHoaDon(hoaDon);
        gioHangRepository.save(gioHang);
        return mapToHoaDonDto(hoaDon);
    }

    private HoaDonDTO mapToHoaDonDto(HoaDon hoaDon) {
        return new HoaDonDTO(
                hoaDon.getId(),
                hoaDon.getIdKhachHang().getId(),
                hoaDon.getIdPhieuGiamGia() != null ? hoaDon.getIdPhieuGiamGia().getId() : null,
                hoaDon.getIdNhanVien().getId(),
                hoaDon.getMa(),
                hoaDon.getTienSanPham(),
                hoaDon.getLoaiDon(),
                hoaDon.getPhiVanChuyen(),
                hoaDon.getTongTien(),
                hoaDon.getTongTienSauGiam(),
                hoaDon.getGhiChu(),
                hoaDon.getTenKhachHang(),
                hoaDon.getDiaChiKhachHang(),
                hoaDon.getSoDienThoaiKhachHang(),
                hoaDon.getEmail(),
                hoaDon.getNgayTao(),
                hoaDon.getTrangThai()
        );
    }
}