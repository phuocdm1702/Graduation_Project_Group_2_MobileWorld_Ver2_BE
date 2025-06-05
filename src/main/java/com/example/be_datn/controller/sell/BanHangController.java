package com.example.be_datn.controller.sell;

import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.entity.inventory.ChiTietGioHang;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.service.sell.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BanHangController {
    @Autowired
    private BanHangService banHangService;

    @GetMapping("/hoa-don-cho")
    public ResponseEntity<List<HoaDon>> getHD() {
        return ResponseEntity.ok(banHangService.getHDCho());
    }

    @PostMapping("/add/hd-cho")
    public ResponseEntity<HoaDonDTO> addHD() {
        return ResponseEntity.ok(banHangService.taoHD());
    }

    @DeleteMapping("/xoa-hd-cho/{idHD}")
    public ResponseEntity<Void> huyHDCho(@PathVariable Integer idHD) throws Exception {
        banHangService.huyHDCho(idHD);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hoa-don/{idHD}/gio-hang")
    public ResponseEntity<List<ChiTietGioHang>> getChiTietGioHang(@PathVariable Integer idHD) {
        return ResponseEntity.ok(banHangService.getSanPhamGioHang(idHD));
    }

}
