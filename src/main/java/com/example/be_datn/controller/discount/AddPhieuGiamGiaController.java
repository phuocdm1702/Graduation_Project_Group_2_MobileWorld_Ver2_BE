package com.example.be_datn.controller.discount;

import com.example.be_datn.config.Email.EmailSend;
import com.example.be_datn.dto.account.response.KhachHangDTO;
import com.example.be_datn.dto.discount.request.PhieuGiamGiaRequest;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.service.EmailNotificationService;
import com.example.be_datn.service.account.KhachHangServices;
import com.example.be_datn.service.account.TaiKhoanService;
import com.example.be_datn.service.discount.PhieuGiamGiaCaNhanService;
import com.example.be_datn.service.discount.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequestMapping("/api/phieu-giam-gia")
@RestController
@CrossOrigin("http://localhost:5173")
public class AddPhieuGiamGiaController {
    @Autowired
    private KhachHangServices khachHangService;

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @Autowired
    private PhieuGiamGiaCaNhanService phieuGiamGiaCaNhanService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @GetMapping("/data-kh")
    public List<KhachHangDTO> fetchDataKH() {
        List<KhachHangDTO> listKH = khachHangService.getKHPgg();
        if (listKH == null) {
            System.out.println("Danh sách khách hàng trả về null, trả về mảng rỗng");
            return Collections.emptyList();
        }
        return listKH;
    }

    @GetMapping("/search-kh")
    public ResponseEntity<?> searchKHAddPgg(@RequestParam("keyword") String keyword) {
        List<KhachHang> listSearch = khachHangService.searchFormAddPgg(keyword);
        if (listSearch == null) {
            System.out.println("Danh sách tìm kiếm trả về null, trả về mảng rỗng");
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(listSearch);
    }

    @GetMapping("/filter-kh-by-gioitinh")
    public ResponseEntity<?> filterKHByGioiTinh(
            @RequestParam(value = "gioiTinh", required = false) String gioiTinh) {
        Boolean parsedGioiTinh = null;
        if (gioiTinh != null && !gioiTinh.isEmpty()) {
            if ("true".equalsIgnoreCase(gioiTinh)) {
                parsedGioiTinh = true;
            } else if ("false".equalsIgnoreCase(gioiTinh)) {
                parsedGioiTinh = false;
            } else {
                return ResponseEntity.badRequest().body("Giá trị gioiTinh không hợp lệ. Phải là 'true', 'false' hoặc không có.");
            }
        }
        List<KhachHang> listFiltered = khachHangService.filterByGioiTinh(parsedGioiTinh);
        if (listFiltered == null) {
            System.out.println("Danh sách lọc theo giới tính trả về null, trả về mảng rỗng");
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(listFiltered);
    }

    @Transactional
    @PostMapping("/addPhieuGiamGia")
    public ResponseEntity<PhieuGiamGia> addPGG(@RequestBody PhieuGiamGiaRequest dtoPGG) {
        PhieuGiamGia pgg = new PhieuGiamGia();
        pgg.setMa(dtoPGG.getMa());
        pgg.setTenPhieuGiamGia(dtoPGG.getTenPhieuGiamGia());
        pgg.setLoaiPhieuGiamGia(dtoPGG.getLoaiPhieuGiamGia());
        pgg.setPhanTramGiamGia(dtoPGG.getPhanTramGiamGia());
        pgg.setSoTienGiamToiDa(dtoPGG.getSoTienGiamToiDa());
        pgg.setHoaDonToiThieu(dtoPGG.getHoaDonToiThieu());
        pgg.setSoLuongDung(dtoPGG.getSoLuongDung());
        pgg.setNgayBatDau(dtoPGG.getNgayBatDau());
        pgg.setNgayKetThuc(dtoPGG.getNgayKetThuc());
        pgg.setTrangThai(true);
        pgg.setRiengTu(Objects.equals(dtoPGG.getRiengTu(), 1));
        pgg.setMoTa(dtoPGG.getMoTa());
        pgg.setDeleted(false);

        PhieuGiamGia savePgg = phieuGiamGiaService.addPGG(pgg);
        if (savePgg == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        if (Objects.equals(dtoPGG.getRiengTu(), 1) && dtoPGG.getCustomerIds() != null && !dtoPGG.getCustomerIds().isEmpty()) {
            for (Integer khachHangID : dtoPGG.getCustomerIds()) {
                KhachHang kh = khachHangService.findById(khachHangID);
                if (kh != null) {
                    PhieuGiamGiaCaNhan pggcn = new PhieuGiamGiaCaNhan();
                    pggcn.setIdPhieuGiamGia(pgg);
                    pggcn.setIdKhachHang(kh);
                    pggcn.setMa(dtoPGG.getMa() + "-" + khachHangID);
                    pggcn.setNgayNhan(new Date());
                    pggcn.setNgayHetHan(pgg.getNgayKetThuc());
                    pggcn.setTrangThai(true);
                    pggcn.setDeleted(false);

                    phieuGiamGiaCaNhanService.addPGGCN(pggcn);

                    emailNotificationService.sendVoucherEmailAsync(khachHangID, pgg.getId());
                }
            }
        }

        return ResponseEntity.ok(savePgg);
    }
}
