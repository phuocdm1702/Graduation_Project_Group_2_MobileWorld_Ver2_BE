package com.example.be_datn.controller.pay.momo;

import com.example.be_datn.service.momo.MomoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/momo")
public class MomoController {

    private final MomoService momoService;

    @Autowired
    public MomoController(MomoService momoService) {
        this.momoService = momoService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMomoPayment(@RequestParam long amount,
                                               @RequestParam String orderInfo,
                                               @RequestParam String returnUrl,
                                               @RequestParam String notifyUrl) {
        System.out.println("MomoController createMomoPayment endpoint hit!");
        try {
            String paymentUrl = momoService.createOrder(amount, orderInfo, returnUrl, notifyUrl);
            Map<String, String> response = new HashMap<>();
            response.put("payUrl", paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Momo payment: " + e.getMessage());
        }
    }

    @GetMapping("/return")
    public ResponseEntity<?> handleMomoReturn(jakarta.servlet.http.HttpServletRequest request) {
        try {
            int status = momoService.orderReturn(request);
            if (status == 1) {
                return ResponseEntity.ok("Momo payment successful.");
            } else if (status == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Momo payment failed.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing Momo return.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling Momo return: " + e.getMessage());
        }
    }
}
