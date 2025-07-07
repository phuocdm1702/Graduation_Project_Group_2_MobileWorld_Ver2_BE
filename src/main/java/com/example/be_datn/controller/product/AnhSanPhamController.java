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
}