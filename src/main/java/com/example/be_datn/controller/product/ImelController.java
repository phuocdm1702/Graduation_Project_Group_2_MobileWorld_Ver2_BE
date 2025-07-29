package com.example.be_datn.controller.product;

import com.example.be_datn.repository.product.ImelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/imel")
public class ImelController {

    private final ImelRepository imelRepository;

    public ImelController(ImelRepository imelRepository) {
        this.imelRepository = imelRepository;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkImelExists(@RequestParam String imei) {
        boolean exists = imelRepository.existsByImelAndDeletedFalse(imei);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}