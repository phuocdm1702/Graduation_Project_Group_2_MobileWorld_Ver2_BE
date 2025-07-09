package com.example.be_datn.controller.pay;

import com.example.be_datn.config.VNPAY.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class VNPAYController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam("amount") long amount,
                                                @RequestParam("orderInfo") String orderInfo) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String paymentUrl = vnPayService.createOrder(request, amount, orderInfo, baseUrl);
        return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
    }

    @GetMapping("/vnpay-payment")
    public ResponseEntity<String> paymentReturn(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);

        String statusMessage;
        switch (paymentStatus) {
            case 1:
                statusMessage = "Transaction Successful";
                break;
            case 0:
                statusMessage = "Transaction Failed";
                break;
            default:
                statusMessage = "Invalid Signature";
                break;
        }

        return new ResponseEntity<>(statusMessage, HttpStatus.OK);
    }

}
