package com.example.be_datn.controller.pay;

import com.example.be_datn.config.VNPAY.VNPayService;
import com.example.be_datn.service.order.HoaDonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class VNPAYController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private HoaDonService hoaDonService;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam("amount") long amount,
                                                @RequestParam("orderInfo") String orderInfo) {
        String idHD = null;
        if (orderInfo != null && orderInfo.startsWith("Thanh toan don hang #")) {
            idHD = orderInfo.substring("Thanh toan don hang #".length()).trim();
        } else if (orderInfo != null && orderInfo.startsWith("Thanh toan hoa don ")) {
            idHD = orderInfo.substring("Thanh toan hoa don ".length()).trim();
        }

        if (idHD == null || idHD.isEmpty()) {
            throw new RuntimeException("Order ID (idHD) could not be extracted from orderInfo: " + orderInfo);
        }

        // Force the return URL to be the backend endpoint to ensure backend processing
        String returnUrl = "http://localhost:8080/api/payment/vnpay-payment";

        String paymentUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl, idHD);
        return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
    }

    @GetMapping("/vnpay-payment")
    public RedirectView paymentReturn(HttpServletRequest request) {
        String idHD = vnPayService.orderReturn(request);

        if (idHD != null && !idHD.isEmpty()) {
            boolean paymentProcessed = false;
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                try {
                    // Attempt to update the invoice status.
                    // Status 1: Chờ giao hàng (Pending Delivery)
                    hoaDonService.updateHoaDonStatus(Integer.parseInt(idHD), (short) 1, null);
                    paymentProcessed = true;
                    System.out.println("VNPay payment processed successfully for order ID: " + idHD + " on attempt " + (i + 1));
                    break; // Exit loop on success
                } catch (Exception e) {
                    System.err.println("Attempt " + (i + 1) + ": Failed to process VNPay payment for order ID " + idHD + ". Error: " + e.getMessage());
                    if (i < retries - 1) {
                        try {
                            Thread.sleep(1000); // Wait for 1 second before retrying
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment processing was interrupted.");
                        }
                    } else {
                        System.err.println("All retries failed for order ID: " + idHD + ". The invoice might not exist or another issue occurred.");
                        return new RedirectView("http://localhost:5173/payment-failure?orderId=" + idHD);
                    }
                }
            }

            if (paymentProcessed) {
                return new RedirectView("http://localhost:5173/hoaDon/" + idHD + "/detail");
            }
        }

        // If idHD is null or processing failed after retries
        return new RedirectView("http://localhost:5173/payment-failure");
    }

}
