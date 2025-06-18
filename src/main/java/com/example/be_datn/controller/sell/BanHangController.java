package com.example.be_datn.controller.sell;

import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
import com.example.be_datn.entity.inventory.ChiTietGioHang;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.service.sell.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class BanHangController {
    @Autowired
    private BanHangService banHangService;

    @GetMapping("/hoa-don-cho")
    public ResponseEntity<List<HoaDon>> getHD() {
        return ResponseEntity.ok(banHangService.getHDCho());
    }

    @PostMapping("/add/tao-hd-cho")
    public ResponseEntity<HoaDonDTO> addHD(@RequestParam(required = false) Integer khachHangId) {
        Integer idKhachHangToUse = (khachHangId != null) ? khachHangId : 1;
        return ResponseEntity.ok(banHangService.taoHD(idKhachHangToUse));
    }

    @DeleteMapping("/xoa-hd-cho/{idHD}")
    public ResponseEntity<Void> huyHDCho(@PathVariable Integer idHD) throws Exception {
        banHangService.huyHDCho(idHD);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add/gio-hang")
    public ResponseEntity<GioHangDTO> addGioHang(@RequestParam Integer idHD, @RequestBody ChiTietGioHangDTO chiTietGioHangDTO) {
        GioHangDTO gh = banHangService.themVaoGH(idHD, chiTietGioHangDTO);
        return ResponseEntity.ok(gh);
    }

    @GetMapping("/gio-hang/data/{idHD}")
    public ResponseEntity<GioHangDTO> getGioHang(@PathVariable Integer idHD) {
        GioHangDTO gh = banHangService.layGioHang(idHD);
        return ResponseEntity.ok(gh);
    }

    @GetMapping("/gio-hang-chi-tiet/data/{idHD}")
    public ResponseEntity<HoaDonDTO> getHoaDonDetail(@PathVariable Integer idHD) {
        HoaDonDTO hd = banHangService.layChiTietHoaDonCho(idHD);
        return ResponseEntity.ok(hd);
    }

    @PostMapping("/thanh-toan/{idHD}")
    public ResponseEntity<HoaDonDTO> thanhToan(@PathVariable Integer idHD, @RequestBody HoaDonRequest hoaDonRequest) {
        HoaDonDTO hoaDonDTO = banHangService.thanhToan(idHD, hoaDonRequest);
        return ResponseEntity.ok(hoaDonDTO);
    }

    @GetMapping("/san-pham")
    public Page<ChiTietSanPhamGroupDTO> getSanPham(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "") String keyword) {
        return banHangService.getAllCTSP(page, size, keyword);
    }

    @GetMapping("/san-pham/{sanPhamId}/imeis")
    public ResponseEntity<List<String>> getIMEIsBySanPhamId(@PathVariable Integer sanPhamId,
                                                            @RequestParam String mauSac,
                                                            @RequestParam String dungLuongRam,
                                                            @RequestParam String dungLuongBoNhoTrong) {
        try {
            List<String> imeis = banHangService.getIMEIsBySanPhamIdAndAttributes(sanPhamId, mauSac, dungLuongRam, dungLuongBoNhoTrong);
            return ResponseEntity.ok(imeis);
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách IMEI: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
