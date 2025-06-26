package com.example.be_datn.controller.home;

import com.example.be_datn.dto.home.LichSuHoaDonDTOForHome;
import com.example.be_datn.service.home.TrangChuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trangchu")
@CrossOrigin(origins = "http://localhost:5173")
public class TrangChuController {
    @Autowired
    private TrangChuService trangChuService;

    public TrangChuController(TrangChuService trangChuService) {
        this.trangChuService = trangChuService;
    }

    @GetMapping("/orders")
    public ResponseEntity<TrangChuService.ThongKeDTO> getSoLuongHoaDonHomNayVaHomQua() {
        return ResponseEntity.ok(trangChuService.getSoLuongHoaDonHomNayVaHomQua());
    }

    @GetMapping("/revenue")
    public ResponseEntity<TrangChuService.ThongKeDTO> getTongTienThangNayVaThangTruoc() {
        return ResponseEntity.ok(trangChuService.getTongTienThangNayVaThangTruoc());
    }

    @GetMapping("/customers")
    public ResponseEntity<TrangChuService.ThongKeDTO> thongKeKhachHang() {
        return ResponseEntity.ok(trangChuService.thongKeKhachHang());
    }

    @GetMapping("/products")
    public ResponseEntity<TrangChuService.ThongKeDTO> thongKeChiTietSanPham() {
        return ResponseEntity.ok(trangChuService.thongKeChiTietSanPham());
    }

    @GetMapping("/activities")
    public ResponseEntity<List<LichSuHoaDonDTOForHome>> getAllLichSuHoaDon() {
        return ResponseEntity.ok(trangChuService.getAllLichSuHoaDon());
    }
}