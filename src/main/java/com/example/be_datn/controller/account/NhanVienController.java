package com.example.be_datn.controller.account;

import com.example.be_datn.dto.account.response.NhanVienResponse;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.service.account.NhanVienServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:8080"})
@RequestMapping("/nhan-vien")

public class NhanVienController {

    private final NhanVienServices nhanVienServices;

    @Autowired
    public NhanVienController(NhanVienServices nhanVienServices) {
        this.nhanVienServices = nhanVienServices;
    }

    //Lay du lieu
    @GetMapping("/home")
    public List<NhanVien> getallNhanVien() {
        return nhanVienServices.getall();
    }

    //add nhan vien
    @PostMapping("/add")
    public ResponseEntity<?> addNhanVien(@RequestBody NhanVienResponse nhanVienResponse) {
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
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNhanVien(@PathVariable Integer id, @RequestBody NhanVienResponse nhanVienResponse) {
        try {
            NhanVien updatedNhanVien = nhanVienServices.updateNhanVien(id, nhanVienResponse);
            return ResponseEntity.ok(updatedNhanVien);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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

}
