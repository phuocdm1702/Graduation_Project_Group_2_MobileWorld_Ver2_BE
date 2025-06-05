package com.example.be_datn.service.sell.impl;

import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.inventory.ChiTietGioHang;
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
import java.util.List;
import java.util.Random;


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

    private String generatedRandomCode() {
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(6);
        for(int i = 0; i < 6; i++) {
            code.append(character.charAt(random.nextInt(character.length())));
        }
        return "HD_" + code.toString();
    }

    private String generatedRandomCodeGH() {
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(8);
        for(int i = 0; i < 8; i++) {
            code.append(character.charAt(random.nextInt(character.length())));
        }
        return "GH_" + code.toString();
    }

    private String generatedRandomCodeCTGH() {
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(8);
        for(int i = 0; i < 8; i++) {
            code.append(character.charAt(random.nextInt(character.length())));
        }
        return "CTGH_" + code.toString();
    }

    @Override
    public List<HoaDon> getHDCho() {
        return hoaDonRepository.findAllHDNotConfirm();
    }

    @Override
    @Transactional
    public void huyHDCho(Integer idHD) {
        if(!hoaDonRepository.existsById(idHD)) {
            throw new RuntimeException("Không tìm thấy hoá đơn có id: " + idHD);
        }
        hoaDonRepository.deleteById(idHD);
    }

    @Override
    @Transactional
    public HoaDonDTO taoHD() {
        Integer khachHangId = 1;
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Khách hàng với ID 1 không tồn tại"));

        Integer nhanVienId = 1;
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Nhân viên với ID 1 không tồn tại"));

//        List<ChiTietGioHang> chiTietGioHangs = chiTietGioHangRepository.findByIdGioHangIdAndDeletedFalse(gioHang.getId());
//        if (chiTietGioHangs.isEmpty()) {
//            throw new RuntimeException("Giỏ hàng trống, không thể tạo hóa đơn");
//        }

        // Tính tổng tiền sản phẩm
//        BigDecimal tienSanPham = chiTietGioHangs.stream()
//                .map(ChiTietGioHang::getTongTien)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);

        HoaDon hoaDon = HoaDon.builder()
                .idKhachHang(khachHang)
                .idNhanVien(nhanVien)
                .ma(generatedRandomCode())
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
                .trangThai((short) 0)
                .deleted(true)
                .createdAt(new Date())
                .createdBy(nhanVienId)
                .build();

        hoaDon = hoaDonRepository.save(hoaDon);

        GioHang gh = GioHang.builder()
                .idKhachHang(khachHang)
                .ma(generatedRandomCodeGH())
                .deleted(false)
                .idHoaDon(hoaDon)
                .build();
        gioHangRepository.save(gh);

        return mapToHoaDonDto(hoaDon);
    }

    @Override
    public List<ChiTietGioHang> getSanPhamGioHang(Integer idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn có id: " + idHoaDon));

        if(hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hoá đơn có id: " + idHoaDon + "không phải hoá đơn chờ!");
        }

        GioHang gioHang = gioHangRepository.findByIdHoaDon(hoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng có id hoá đơn: " + idHoaDon));

        return chiTietGioHangRepository.findByIdGioHangIdAndDeletedFalse(gioHang.getId());
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