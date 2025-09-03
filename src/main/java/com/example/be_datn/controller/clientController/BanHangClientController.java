package com.example.be_datn.controller.clientController;

import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponseForClient;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.service.clientService.BanHangClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "http://localhost:3000")
public class BanHangClientController {
    @Autowired
    private BanHangClientService banHangClientService;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @PostMapping("/hoa-don-cho")
    public ResponseEntity<HoaDonDetailResponse> taoHoaDonCho(@RequestParam(required = false) Integer khachHangId) {
        HoaDonDetailResponse hoaDonDetailResponse = banHangClientService.taoHoaDonCho(khachHangId);
        return ResponseEntity.ok(hoaDonDetailResponse);
    }

    @GetMapping("/hoa-don-cho/khach-hang/{khachHangId}")
    public ResponseEntity<List<HoaDonDetailResponse>> getPendingInvoicesByCustomer(@PathVariable Integer khachHangId) {
        List<HoaDonDetailResponse> pendingInvoices = banHangClientService.getPendingInvoicesByCustomer(khachHangId);
        return ResponseEntity.ok(pendingInvoices);
    }

    @PostMapping("/gio-hang/them")
    public ResponseEntity<GioHangDTO> themSanPhamVaoGioHang(@RequestParam Integer idHD, @RequestBody ChiTietGioHangDTO chiTietGioHangDTO) {
        GioHangDTO gioHangDTO = banHangClientService.themSanPhamVaoGioHang(idHD, chiTietGioHangDTO);
        return ResponseEntity.ok(gioHangDTO);
    }

    @GetMapping("/gio-hang/{idHD}")
    public ResponseEntity<GioHangDTO> layGioHang(@PathVariable Integer idHD) {
        GioHangDTO gioHangDTO = banHangClientService.layGioHang(idHD);
        return ResponseEntity.ok(gioHangDTO);
    }

    @DeleteMapping("/gio-hang/xoa")
    public ResponseEntity<GioHangDTO> xoaSanPhamKhoiGioHang(
            @RequestParam Integer idHD,
            @RequestParam Integer spId,
            @RequestParam(required = false) String maImel) {
        GioHangDTO gioHangDTO = banHangClientService.xoaSanPhamKhoiGioHang(idHD, spId, maImel);
        return ResponseEntity.ok(gioHangDTO);
    }

    @PostMapping("/thanh-toan/{idHD}")
    public ResponseEntity<HoaDonDetailResponse> thanhToan(@PathVariable Integer idHD, @RequestBody HoaDonRequest hoaDonRequest) {
        HoaDonDetailResponse hoaDonDetailResponse = banHangClientService.thanhToan(idHD, hoaDonRequest);
        return ResponseEntity.ok(hoaDonDetailResponse);
    }

    @GetMapping("/san-pham")
    public ResponseEntity<Page<ChiTietSanPhamGroupDTO>> layDanhSachSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {
        Page<ChiTietSanPhamGroupDTO> sanPhams = banHangClientService.layDanhSachSanPham(page, size, keyword);
        return ResponseEntity.ok(sanPhams);
    }

    @GetMapping("/phieu-giam-gia")
    public ResponseEntity<List<PhieuGiamGiaCaNhan>> layPhieuGiamGiaCaNhan(@RequestParam Integer idKhachHang) {
        List<PhieuGiamGiaCaNhan> phieuGiamGias = banHangClientService.layPhieuGiamGiaCaNhan(idKhachHang);
        return ResponseEntity.ok(phieuGiamGias);
    }

    @GetMapping("/san-pham/barcode-or-imei")
    public ResponseEntity<Map<String, Object>> timSanPhamTheoBarcodeHoacImei(@RequestParam String code) {
        Map<String, Object> sanPham = banHangClientService.timSanPhamTheoBarcodeHoacImei(code);
        return ResponseEntity.ok(sanPham);
    }

    @GetMapping("/khach-hang/{idKhachHang}")
    public ResponseEntity<KhachHang> layThongTinKhachHang(@PathVariable Integer idKhachHang) {
        KhachHang khachHang = banHangClientService.layThongTinKhachHang(idKhachHang);
        return ResponseEntity.ok(khachHang);
    }

    @GetMapping("/chi-tiet-san-pham/{chiTietSanPhamId}")
    public ResponseEntity<ChiTietSanPhamResponseForClient> getChiTietSanPhamById(@PathVariable Integer chiTietSanPhamId) {
        Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findById(chiTietSanPhamId);

        if (chiTietSanPhamOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy chi tiết sản phẩm cho ID: " + chiTietSanPhamId);
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
        ChiTietSanPhamResponseForClient responseDTO = ChiTietSanPhamResponseForClient.builder()
                .id(chiTietSanPham.getId())
                .ma(chiTietSanPham.getMa())
                .giaBan(chiTietSanPham.getGiaBan())
                .ghiChu(chiTietSanPham.getGhiChu())
                .deleted(chiTietSanPham.getDeleted())
                .createdAt(chiTietSanPham.getCreatedAt())
                .createdBy(chiTietSanPham.getCreatedBy())
                .updatedAt(chiTietSanPham.getUpdatedAt())
                .updatedBy(chiTietSanPham.getUpdatedBy())
                .idSanPham(chiTietSanPham.getIdSanPham() != null ? chiTietSanPham.getIdSanPham().getId() : null)
                .tenSanPham(chiTietSanPham.getIdSanPham() != null ? chiTietSanPham.getIdSanPham().getTenSanPham() : null)
                .maImel(chiTietSanPham.getIdImel() != null ? chiTietSanPham.getIdImel().getMa() : null)
                .mauSac(chiTietSanPham.getIdMauSac() != null ? chiTietSanPham.getIdMauSac().getMauSac() : null)
                .ram(chiTietSanPham.getIdRam() != null ? chiTietSanPham.getIdRam().getDungLuongRam() : null)
                .boNhoTrong(chiTietSanPham.getIdBoNhoTrong() != null ? chiTietSanPham.getIdBoNhoTrong().getDungLuongBoNhoTrong() : null)
                .duongDanAnh(chiTietSanPham.getIdAnhSanPham() != null ? chiTietSanPham.getIdAnhSanPham().getDuongDan() : null)
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/hoa-don-cho/{idHD}")
    public ResponseEntity<Void> xoaHoaDonCho(@PathVariable Integer idHD) {
        try {
            banHangClientService.xoaHoaDonCho(idHD);
            return ResponseEntity.noContent().build();  // 204 Success
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 400 với message nếu cần
        }
    }

    @PostMapping("/hoa-don/xac-nhan-imei/{idHD}")
    public ResponseEntity<HoaDonDetailResponse> xacNhanVaGanImei(@PathVariable Integer idHD, @RequestBody Object payload) {
        try {
            System.out.println("=== Client IMEI Confirmation Controller ===");
            System.out.println("HoaDon ID: " + idHD);
            System.out.println("Received payload: " + payload);
            System.out.println("Payload type: " + payload.getClass().getName());

            // Convert payload to Map<Integer, String>
            Map<Integer, String> imelMap = new HashMap<>();

            if (payload instanceof Map) {
                Map<?, ?> rawMap = (Map<?, ?>) payload;
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    try {
                        Integer key = null;
                        String value = null;

                        // Handle different key types
                        if (entry.getKey() instanceof Integer) {
                            key = (Integer) entry.getKey();
                        } else if (entry.getKey() instanceof String) {
                            key = Integer.parseInt((String) entry.getKey());
                        }

                        // Handle different value types
                        if (entry.getValue() instanceof String) {
                            value = (String) entry.getValue();
                        }

                        if (key != null && value != null) {
                            imelMap.put(key, value);
                            System.out.println("Mapped: " + key + " -> " + value);
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to parse entry: " + entry.getKey() + " -> " + entry.getValue());
                    }
                }
            }

            System.out.println("Final IMEI Map: " + imelMap);

            HoaDonDetailResponse response = banHangClientService.xacNhanVaGanImei(idHD, imelMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR in xacNhanVaGanImei controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}