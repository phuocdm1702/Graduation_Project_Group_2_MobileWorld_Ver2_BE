package com.example.be_datn.controller.discount;

import com.example.be_datn.config.Email.EmailSend;
import com.example.be_datn.dto.discount.request.PhieuGiamGiaRequest;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
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

@RequestMapping("/api")
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
    private TaiKhoanService taiKhoanServices;

    @Autowired
    private EmailSend emailSend;

    @GetMapping("/data-kh")
    public List<KhachHang> fetchDataKH() {
        List<KhachHang> listKH = khachHangService.getall();
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
        pgg.setTrangThai(false);
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

                    String email = taiKhoanServices.findById(khachHangID);
                    if (email != null && !email.trim().isEmpty()) {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String ngayHH = dateFormat.format(pgg.getNgayKetThuc());
                            Double TinhSTGTD = pgg.getPhanTramGiamGia() * pgg.getHoaDonToiThieu() / 100;

                            emailSend.sendDiscountEmail(email, pggcn.getMa(), ngayHH, pgg.getPhanTramGiamGia(), TinhSTGTD);
                        } catch (Exception e) {
                            System.err.println("Failed to send email to " + email + ": " + e.getMessage());
                        }
                    }
                }
            }
        }

        return ResponseEntity.ok(savePgg);
    }
}
