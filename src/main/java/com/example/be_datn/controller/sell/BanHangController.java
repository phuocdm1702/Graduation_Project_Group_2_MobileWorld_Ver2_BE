package com.example.be_datn.controller.sell;

import com.example.be_datn.config.VNPAY.VNPayService;
import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.entity.inventory.ChiTietGioHang;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.repository.product.ImelRepository;
import com.example.be_datn.service.account.KhachHangServices;
import com.example.be_datn.service.discount.PhieuGiamGiaCaNhanService;
import com.example.be_datn.service.discount.PhieuGiamGiaService;
import com.example.be_datn.service.order.HoaDonService;
import com.example.be_datn.service.sell.BanHangService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class BanHangController {
    @Autowired
    private BanHangService banHangService;
    private final PhieuGiamGiaCaNhanService phieuGiamGiaCaNhanService;
    private final PhieuGiamGiaService phieuGiamGiaService;
    private final HoaDonService hoaDonService;
    private final KhachHangServices khachHangServices;

    @Autowired
    private ImelRepository imelRepository;

    @Autowired
    private VNPayService vnPayService;

    public BanHangController(PhieuGiamGiaCaNhanService phieuGiamGiaCaNhanService, PhieuGiamGiaService phieuGiamGiaService, HoaDonService hoaDonService, KhachHangServices khachHangServices) {
        this.phieuGiamGiaCaNhanService = phieuGiamGiaCaNhanService;
        this.phieuGiamGiaService = phieuGiamGiaService;
        this.hoaDonService = hoaDonService;
        this.khachHangServices = khachHangServices;
    }

    @GetMapping("/hoa-don-cho")
    public ResponseEntity<List<HoaDon>> getHD() {
        // Service sẽ tự động gửi realtime update
        return ResponseEntity.ok(banHangService.getHDCho());
    }

    @PostMapping("/add/tao-hd-cho")
    public ResponseEntity<HoaDonDTO> addHD(@RequestBody HoaDonRequest request) {
        Integer idKhachHangToUse = (request.getIdKhachHang() != null) ? request.getIdKhachHang() : 1;
        Integer idNhanVienToUse = (request.getIdNhanVien() != null) ? request.getIdNhanVien() : 1;
        System.out.println("Request body: " + request); // Debug request body

        // Service sẽ tự động gửi realtime update khi tạo hóa đơn
        return ResponseEntity.ok(banHangService.taoHD(idKhachHangToUse, idNhanVienToUse));
    }

    @DeleteMapping("/xoa-hd-cho/{idHD}")
    public ResponseEntity<Void> huyHDCho(@PathVariable Integer idHD) {
        // Service sẽ tự động gửi realtime update khi xóa hóa đơn
        banHangService.huyHD(idHD);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add/gio-hang")
    public ResponseEntity<GioHangDTO> addGioHang(@RequestParam Integer idHD, @RequestBody ChiTietGioHangDTO chiTietGioHangDTO) {
        // Service sẽ tự động gửi realtime update khi thêm vào giỏ hàng
        GioHangDTO gh = banHangService.themVaoGH(idHD, chiTietGioHangDTO);
        return ResponseEntity.ok(gh);
    }

    @GetMapping("/gio-hang/data/{idHD}")
    public ResponseEntity<GioHangDTO> getGioHang(@PathVariable Integer idHD) {
        // Service sẽ tự động gửi realtime update khi lấy giỏ hàng
        GioHangDTO gh = banHangService.layGioHang(idHD);
        return ResponseEntity.ok(gh);
    }

    @GetMapping("/gio-hang-chi-tiet/data/{idHD}")
    public ResponseEntity<HoaDonDTO> getHoaDonDetail(@PathVariable Integer idHD) {
        // Service sẽ tự động gửi realtime update khi lấy chi tiết hóa đơn
        HoaDonDTO hd = banHangService.layChiTietHoaDonCho(idHD);
        return ResponseEntity.ok(hd);
    }

    @DeleteMapping("/gio-hang/xoa")
    public ResponseEntity<GioHangDTO> xoaSanPhamKhoiGioHang(
            @RequestParam Integer hdId,
            @RequestParam Integer spId,
            @RequestParam(required = false) String maImel) {
        // Service sẽ tự động gửi realtime update khi xóa sản phẩm khỏi giỏ hàng
        GioHangDTO updatedGioHang = banHangService.xoaSanPhamKhoiGioHang(hdId, spId, maImel);
        return ResponseEntity.ok(updatedGioHang);
    }

    @PostMapping("/thanh-toan/{idHD}")
    public ResponseEntity<HoaDonDTO> thanhToan(@PathVariable Integer idHD, @RequestBody HoaDonRequest hoaDonRequest) {
        // Service sẽ tự động gửi realtime update khi thanh toán thành công
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

    @GetMapping("/PGG-canhan")
    public ResponseEntity<List<PhieuGiamGiaCaNhan>> getall() {
        try {
            // Service sẽ tự động gửi realtime update cho danh sách phiếu giảm giá cá nhân
            List<PhieuGiamGiaCaNhan> result = phieuGiamGiaCaNhanService.getall();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/PGG-all")
    public ResponseEntity<List<PhieuGiamGia>> getallPGG() {
        try {
            // Service sẽ tự động gửi realtime update cho danh sách phiếu giảm giá
            List<PhieuGiamGia> result = phieuGiamGiaService.getallPGG();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 2. Cập nhật endpoint lấy thông tin khách hàng với WebSocket
    @GetMapping("/by-khach-hang/{idKhachHang}")
    public ResponseEntity<List<PhieuGiamGiaCaNhan>> getByKhachHang(@PathVariable Integer idKhachHang) {
        // Service sẽ tự động gửi realtime update cho thông tin khách hàng
        List<PhieuGiamGiaCaNhan> phieuGiamGias = banHangService.findByKhachHangId(idKhachHang);
        return ResponseEntity.ok(phieuGiamGias);
    }

    // 3. Cập nhật endpoint kiểm tra mã giảm giá với WebSocket
    @GetMapping("/pgg/check")
    public ResponseEntity<Map<String, Object>> checkDiscountCode(@RequestParam("ma") String ma) {
        try {
            Optional<PhieuGiamGiaCaNhan> optional = phieuGiamGiaCaNhanService.checkDiscountCode(ma);

            Map<String, Object> response = new HashMap<>();
            if (optional.isPresent()) {
                response.put("isValid", true);
                response.put("phieuGiamGia", optional.get());
                response.put("message", "Mã giảm giá hợp lệ");
                return ResponseEntity.ok(response);
            } else {
                response.put("isValid", false);
                response.put("phieuGiamGia", null);
                response.put("message", "Mã giảm giá không hợp lệ hoặc đã hết hạn");
                return ResponseEntity.ok(response); // Vẫn trả về OK với thông tin lỗi
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("isValid", false);
            errorResponse.put("phieuGiamGia", null);
            errorResponse.put("message", "Lỗi khi kiểm tra mã giảm giá: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/chi-tiet-san-pham/id")
    public ResponseEntity<Integer> getChiTietSanPhamId(
            @RequestParam("sanPhamId") Integer sanPhamId,
            @RequestParam("mauSac") String mauSac,
            @RequestParam("dungLuongRam") String dungLuongRam,
            @RequestParam("dungLuongBoNhoTrong") String dungLuongBoNhoTrong) {
        try {
            Integer chiTietSanPhamId = banHangService.getChiTietSanPhamId(sanPhamId, mauSac, dungLuongRam, dungLuongBoNhoTrong);
            return ResponseEntity.ok(chiTietSanPhamId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/chi-tiet-san-pham/by-imei")
    public ResponseEntity<ChiTietSanPham> getChiTietSanPhamByIMEI(@RequestParam("imei") String imei) {
        try {
            ChiTietSanPham chiTietSanPham = banHangService.getChiTietSanPhamByIMEI(imei);
            return ResponseEntity.ok(chiTietSanPham);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/chi-tiet-san-pham/update-imei-status")
    public ResponseEntity<Void> updateIMEIStatus(@RequestParam String imei, @RequestParam boolean deleted) {
        Optional<Imel> imelOpt = imelRepository.findByImel(imei.trim());
        if (imelOpt.isPresent()) {
            Imel imelEntity = imelOpt.get();
            imelEntity.setDeleted(deleted);
            imelRepository.save(imelEntity);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/products/by-barcode-or-imei")
    public ResponseEntity<Map<String, Object>> getProductByBarcodeOrImei(@RequestParam String code) {
        Map<String, Object> product = banHangService.findProductByBarcodeOrImei(code);
        return ResponseEntity.ok(product);
    }
    @PutMapping("/updatePhieuGiamGia")
    public ResponseEntity<HoaDon> updatePhieuGiamGia(
            @RequestParam("hoaDonId") Integer hoaDonId,
            @RequestParam(value = "idPhieuGiamGia", required = false) Integer idPhieuGiamGia) {
        HoaDon updatedHoaDon = hoaDonService.updatePhieuGiamGia(hoaDonId, idPhieuGiamGia);
        return ResponseEntity.ok(updatedHoaDon);
    }
    @GetMapping("/getById")
    public ResponseEntity<KhachHang> getKhachHangById(@RequestParam("id") Integer id) {
        KhachHang khachHang = khachHangServices.findById(id);
        return ResponseEntity.ok(khachHang);
    }
}