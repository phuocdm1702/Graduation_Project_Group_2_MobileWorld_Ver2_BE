package com.example.be_datn.controller.clientController;

import com.example.be_datn.config.VNPAY.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/client/vnpay")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ClientVnpayController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam("amount") long amount,
                                                @RequestParam("orderInfo") String orderInfo,
                                                @RequestParam("returnUrl") String returnUrl) {
        String idHD = null;
        if (orderInfo != null && orderInfo.startsWith("Thanh toan don hang #")) {
            idHD = orderInfo.substring("Thanh toan don hang #".length()).trim();
        } else if (orderInfo != null && orderInfo.startsWith("Thanh toan hoa don ")) {
            idHD = orderInfo.substring("Thanh toan hoa don ".length()).trim();
        }


        if (idHD == null || idHD.isEmpty()) {
            throw new RuntimeException("Order ID (idHD) could not be extracted from orderInfo: " + orderInfo);
        }

        String paymentUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl, idHD);
        return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
    }

    @GetMapping("/return")
    public ResponseEntity<Map<String, String>> paymentReturn(HttpServletRequest request) {
        String idHD = vnPayService.orderReturn(request);

        if (idHD != null && !idHD.isEmpty()) {
            // Just return a success status. The frontend will handle the final transaction submission.
            return ResponseEntity.ok(Map.of("status", "success", "idHD", idHD));
        } else {
            // Return a failure status.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "failure", "message", "Invalid VNPay signature or failed transaction."));
        }
    }
}