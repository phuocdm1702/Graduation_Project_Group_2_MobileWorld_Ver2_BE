package com.example.be_datn.controller.giao_ca;

import com.example.be_datn.dto.giao_ca.LichLamViecDTO;
import com.example.be_datn.service.giao_ca.LichLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/lich-lam-viec")
@CrossOrigin(origins = {"http://localhost:5173"})
public class LichLamViecController {

    @Autowired
    private LichLamViecService lichLamViecService;

    @GetMapping
    public List<LichLamViecDTO> getAll() {
        return lichLamViecService.getAll();
    }

    @PostMapping
    public LichLamViecDTO create(@RequestBody LichLamViecDTO lichLamViecDTO) {
        return lichLamViecService.create(lichLamViecDTO);
    }

    @PutMapping("/{id}")
    public LichLamViecDTO update(@PathVariable Integer id, @RequestBody LichLamViecDTO lichLamViecDTO) {
        return lichLamViecService.update(id, lichLamViecDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        lichLamViecService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Vui lòng chọn một file để upload.", HttpStatus.BAD_REQUEST);
        }
        try {
            lichLamViecService.importExcel(file);
            return new ResponseEntity<>("Import Excel thành công!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Import Excel thất bại: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}