package com.example.be_datn.controller.pay;

import com.example.be_datn.config.VNPAY.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView; // Added import

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
        // Extract idHD from orderInfo (e.g., "Thanh toan hoa don 123")
        String idHD = null;
        if (orderInfo != null && orderInfo.startsWith("Thanh toan hoa don ")) {
            idHD = orderInfo.substring("Thanh toan hoa don ".length()).trim();
        }

        if (idHD == null || idHD.isEmpty()) {
            throw new RuntimeException("Order ID (idHD) could not be extracted from orderInfo: " + orderInfo);
        }

        String paymentUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl, idHD);
        return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
    }

    @GetMapping("/vnpay-payment")
    public RedirectView paymentReturn(HttpServletRequest request) {
        String idHD = String.valueOf(vnPayService.orderReturn(request)); // Now returns idHD or null

        String frontendRedirectUrl;
        if (idHD != null) {
            frontendRedirectUrl = "http://localhost:5173/hoaDon/" + idHD + "/detail";
        } else {
            frontendRedirectUrl = "http://localhost:5173/banHang"; // Redirect to a general sales page or error page
        }

        return new RedirectView(frontendRedirectUrl);
    }

}
