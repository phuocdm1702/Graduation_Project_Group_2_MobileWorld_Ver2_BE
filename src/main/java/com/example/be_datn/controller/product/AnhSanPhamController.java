package com.example.be_datn.controller.product;

import com.example.be_datn.entity.product.AnhSanPham;
import com.example.be_datn.repository.product.AnhSanPhamRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/anh-san-pham")
public class AnhSanPhamController {

    private final AnhSanPhamRepository anhSanPhamRepository;

    public AnhSanPhamController(AnhSanPhamRepository anhSanPhamRepository) {
        this.anhSanPhamRepository = anhSanPhamRepository;
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkExistingImages(@RequestBody Map<String, Object> request) {
        String productGroupKey = (String) request.get("productGroupKey");
        List<String> hashes = (List<String>) request.get("hashes");
        Map<String, String> result = new HashMap<>();

        for (String hash : hashes) {
            Optional<AnhSanPham> anhSanPham = anhSanPhamRepository.findByProductGroupKeyAndHash(productGroupKey, hash);
            anhSanPham.ifPresent(img -> result.put(hash, img.getDuongDan()));
        }

        return ResponseEntity.ok(result);
    }
}