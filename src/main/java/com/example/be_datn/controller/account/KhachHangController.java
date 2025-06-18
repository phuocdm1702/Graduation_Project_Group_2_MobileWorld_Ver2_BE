package com.example.be_datn.controller.account;

import com.example.be_datn.dto.account.response.KhachHangResponse;
import com.example.be_datn.entity.account.DiaChiKhachHang;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.service.account.KhachHangServices;
import com.example.be_datn.service.account.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/khach-hang")
@CrossOrigin(origins = {"http://localhost:5173"})

public class KhachHangController {
    private final KhachHangServices khachHangServices;
    private final TaiKhoanService taiKhoanService;

    @Autowired
    public KhachHangController(KhachHangServices khachHangServices, TaiKhoanService taiKhoanService) {
        this.khachHangServices = khachHangServices;
        this.taiKhoanService = taiKhoanService;
    }

    //hien thi du lieu
    @GetMapping("/home")
    public List<KhachHang> getall(){
        return khachHangServices.getall();
    }

    //add du lieu
    @PostMapping("/add")
    public ResponseEntity<?> addKhachHang(@RequestBody KhachHangResponse khachHangResponse) {
        try {
            KhachHang newKhachHang = khachHangServices.addKhachHang(khachHangResponse);
            return ResponseEntity.ok("Chúc mừng  "+ newKhachHang.getTen()+  "  đã trở thành khách hàng mới!");
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Lỗi khi thêm khách hàng: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //add nhanh khach hang o ban hang
    @PostMapping("/add-Bh")
    public ResponseEntity<?> addKhachHangBh(@RequestBody KhachHangResponse khachHangResponse) {
        try {
            KhachHang newKhachHang = khachHangServices.addKhachHangBH(khachHangResponse);
            return ResponseEntity.ok("Chúc mừng  " + newKhachHang.getTen() + "  đã trở thành khách hàng!");
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Lỗi khi thêm khách hàng: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //delete khach hang
    @PutMapping("/delete/{id}")
    public ResponseEntity<String> softDelete(@PathVariable Integer id) {
        boolean deleted = khachHangServices.delete(id);
        if (deleted) {
            return ResponseEntity.ok("Xóa mềm thành công");
        }
        return ResponseEntity.badRequest().body("Khách hàng không tồn tại");
    }

    //detail khach hang
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getNhanVienDetail(@PathVariable Integer id) {
        Optional<KhachHang> khachHang = khachHangServices.findByIdKH(id);
        if (khachHang.isPresent()) {
            return ResponseEntity.ok(khachHang.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Khach Hang không tồn tại");
    }

    //update khach hang
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateKhachHang(@PathVariable Integer id, @RequestBody KhachHangResponse khachHangResponse) {
        try {
            KhachHang updatedKhachHang = khachHangServices.updateKhachHang(id, khachHangResponse);
            return ResponseEntity.ok(updatedKhachHang);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //update dia chi
    @PutMapping("/updateDchi/{id}")
    public ResponseEntity<?> updateDchi(@PathVariable Integer id, @RequestBody KhachHangResponse khachHangDTO) {
        try {
            KhachHang updateDchi = khachHangServices.updateDchi(id, khachHangDTO);
            return ResponseEntity.ok(updateDchi);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //thay doi trang thai khach hang
    //thay doi trang thai khach hang
    @PutMapping("/trang-thai/{id}")
    public ResponseEntity<?> trangthai(@PathVariable Integer id) {
        try {
            TaiKhoan updatedKhachHang = taiKhoanService.trangthaiKH(id);
            String message = updatedKhachHang.getDeleted() ? "Đã hủy kích hoạt khách hàng!" : "Đã kích hoạt khách hàng!";
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    //search khach hang
    @GetMapping("/search")
    public ResponseEntity<List<KhachHang>> searchKhachHang(@RequestParam("query") String keyword) {
        List<KhachHang> result = khachHangServices.searchKhachHang(keyword);
        return ResponseEntity.ok(result);
    }

    //import khach hang ra excel
    @PostMapping("/import")
    public ResponseEntity<String> importKhachHangFromExcel(@RequestBody List<KhachHangResponse> khachHangResponses) {
        try {
            khachHangServices.importKhachHangFromExcel(khachHangResponses);
            return ResponseEntity.ok("Nhập dữ liệu từ Excel thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    //lay dia chi cung khach hang
    @GetMapping("/getByKhachHang/{idKhachHang}")
    public ResponseEntity<?> getAllAddressesByKhachHangId(@PathVariable Integer idKhachHang) {
        try {
            List<DiaChiKhachHang> addresses = khachHangServices.getAllAddressesByKhachHangId(idKhachHang);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    //thay doi defaul cho ae
    @PutMapping("/setDefault/{id}")
    public ResponseEntity<String> setDefaultAddress(@PathVariable Integer id, @RequestBody DiaChiKhachHang diaChi) {
        try {
            khachHangServices.setMacDinh( id, diaChi.getMacDinh());
            return ResponseEntity.ok("Cập nhật địa chỉ mặc định thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật địa chỉ mặc định: " + e.getMessage());
        }
    }
    //xóa kh
    @PutMapping("/deleteDCKH/{id}")
    public ResponseEntity<String> deleteDiaChi(@PathVariable Integer id) {
        try {
            khachHangServices.deleteDiaChi(id);
            return ResponseEntity.ok("Xóa địa chỉ thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
