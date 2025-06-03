package com.example.be_datn.controller.sell;

import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.service.sell.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ban-hang")
public class BanHangController {
    @Autowired
    private BanHangService banHangService;

    @PostMapping("/add/hoa-don")
    public ResponseEntity<HoaDonDTO> addHD() {
        return ResponseEntity.ok(banHangService.taoHD());
    }


}
