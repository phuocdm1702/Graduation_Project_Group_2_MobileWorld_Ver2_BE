package com.example.be_datn.controller.account;

import com.example.be_datn.dto.account.response.NhanVienResponse;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.service.account.NhanVienServices;
import com.example.be_datn.service.account.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:5173"})
@RequestMapping("/nhan-vien")

public class NhanVienController {

    private final NhanVienServices nhanVienServices;

    private final TaiKhoanService taiKhoanService;


    @Autowired
    public NhanVienController(NhanVienServices nhanVienServices, TaiKhoanService taiKhoanService) {
        this.nhanVienServices = nhanVienServices;
        this.taiKhoanService = taiKhoanService;
    }

    //Lay du lieu
    @GetMapping("/home")
    public List<NhanVien> getallNhanVien() {
        return nhanVienServices.getall();
    }

    //add nhan vien
    @PostMapping("/add")
    public ResponseEntity<?> addNhanVien(@ModelAttribute NhanVienResponse nhanVienResponse) {
        try {
            NhanVien nhanVien = nhanVienServices.addNhanVien(nhanVienResponse);
            return new ResponseEntity<>(nhanVien, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thêm nhân viên: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //xoa mem nhan vien
    @PutMapping("/delete/{id}")
    public ResponseEntity<String> softDelete(@PathVariable Integer id) {
        boolean deleted = nhanVienServices.delete(id);
        if (deleted) {
            return ResponseEntity.ok("Xóa mềm thành công");
        }
        return ResponseEntity.badRequest().body("nv không tồn tại");
    }

    //update nhan vien
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateNhanVien(
            @PathVariable Integer id,
            @ModelAttribute NhanVienResponse nhanVienRequest
    ) {
        try {
            NhanVien updatedNhanVien = nhanVienServices.updateNhanVien(id, nhanVienRequest);
            return ResponseEntity.ok(updatedNhanVien);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //detail nhan vien
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getNhanVienDetail(@PathVariable Integer id) {
        Optional<NhanVien> nhanVien = nhanVienServices.findById(id);
        if (nhanVien.isPresent()) {
            return ResponseEntity.ok(nhanVien.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nhân viên không tồn tại");
    }
    //search nhan vien
    @GetMapping("/search")
    public ResponseEntity<List<NhanVien>> searchNhanVien(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        try {
            List<NhanVien> nhanViens = nhanVienServices.searchNhanVien(keyword, status);
            return ResponseEntity.ok(nhanViens);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //doi trang thai nhanh di-lam?nghi-lam
    @PutMapping("/trang-thai/{id}")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        try {
            TaiKhoan updateNhanVien = taiKhoanService.trangthaiNV(id);
            String message = updateNhanVien.getDeleted() ? "Đã cho nghỉ làm!" : "Đã cho đi làm lại!";
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    //import ra excel
    @PostMapping("/import")
    public ResponseEntity<String> importNhanVien(@RequestBody List<NhanVien> nhanViens) {
        try {
            nhanVienServices.importNhanVien(nhanViens);
            return ResponseEntity.ok("Nhập dữ liệu từ Excel thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Nhập dữ liệu từ Excel thất bại: " + e.getMessage());
        }
    }

}