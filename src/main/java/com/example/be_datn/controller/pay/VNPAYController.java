package com.example.be_datn.controller.pay;

import com.example.be_datn.config.VNPAY.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class VNPAYController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam("amount") long amount,
                                                @RequestParam("orderInfo") String orderInfo,
                                                @RequestParam("returnUrl") String returnUrl) {
        String paymentUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl);
        return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
    }

    @GetMapping("/vnpay-payment")
    public ResponseEntity<Map<String, String>> paymentReturn(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);
        Map<String, String> response = new HashMap<>();
        switch (paymentStatus) {
            case 1:
                response.put("status", "success");
                response.put("message", "Transaction Successful");
                break;
            case 0:
                response.put("status", "failed");
                response.put("message", "Transaction Failed");
                break;
            default:
                response.put("status", "error");
                response.put("message", "Invalid Signature");
                break;
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
