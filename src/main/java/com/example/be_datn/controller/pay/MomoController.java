package com.example.be_datn.controller.pay;

import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.service.momoPayment.MomoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.be_datn.service.order.HoaDonService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/momo")
public class MomoController {

    @Autowired
    private MomoService momoService;

    @Autowired
    private HoaDonService hoaDonService;

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestParam("amount") String amount, @RequestParam("orderInfo") String orderInfo) {
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            // The returnUrl and notifyUrl should ideally be configured in a properties file
            String returnUrl = "http://localhost:8080/api/momo/success";
            String notifyUrl = "http://localhost:8080/api/momo/notify";
            String requestId = String.valueOf(System.currentTimeMillis());

            // Extract idHD from orderInfo. This is fragile, but we keep it as per original logic.
            // A more robust way would be to pass idHD as a separate parameter from the frontend.
            String idHD = null;
            if (orderInfo != null && orderInfo.startsWith("Thanh toan hoa don ")) {
                // Corrected the substring to have one space and added trim() for safety
                idHD = orderInfo.substring("Thanh toan hoa don ".length()).trim();
            }

            if (idHD == null || idHD.isEmpty()) {
                // It's critical to have an idHD to proceed.
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            PaymentResponse paymentResponse = momoService.createMomoPayment(orderId, returnUrl, notifyUrl, amount, orderInfo, requestId, idHD);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/success")
    public RedirectView success(@RequestParam("resultCode") String resultCode, @RequestParam("extraData") String extraData) {
        String idHD = extraData;

        if ("0".equals(resultCode)) {
            boolean paymentProcessed = false;
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                try {
                    // Attempt to update the invoice status.
                    // Status 1: Chờ giao hàng (Pending Delivery)
                    hoaDonService.updateHoaDonStatus(Integer.parseInt(idHD), (short) 1, null);
                    paymentProcessed = true;
                    System.out.println("MoMo payment processed successfully for order ID: " + idHD + " on attempt " + (i + 1));
                    break; // Exit loop on success
                } catch (Exception e) {
                    System.err.println("Attempt " + (i + 1) + ": Failed to process MoMo payment for order ID " + idHD + ". Error: " + e.getMessage());
                    if (i < retries - 1) {
                        try {
                            Thread.sleep(1000); // Wait for 1 second before retrying
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment processing was interrupted.");
                        }
                    } else {
                        // All retries failed
                        System.err.println("All retries failed for order ID: " + idHD + ". The invoice might not exist or another issue occurred.");
                        // Redirect to a failure page if all retries fail
                        return new RedirectView("http://localhost:5173/payment-failure?orderId=" + idHD);
                    }
                }
            }

            if (paymentProcessed) {
                // On successful payment processing, redirect to the detail page.
                return new RedirectView("http://localhost:5173/hoaDon/" + idHD.trim() + "/detail");
            }
        }

        // If resultCode is not 0 or if processing failed after retries
        return new RedirectView("http://localhost:5173/payment-failure?orderId=" + idHD);
    }

    @PostMapping("/notify")
    public ResponseEntity<String> notify(@RequestBody String requestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            String resultCode = jsonNode.get("resultCode").asText();
            // The invoice ID (idHD) is now reliably passed in extraData
            String idHD = jsonNode.has("extraData") ? jsonNode.get("extraData").asText() : "";

            if (!idHD.isEmpty()) {
                momoService.processMomoPaymentResult(idHD, resultCode);
            } else {
                System.err.println("Received MoMo IPN without extraData (idHD). Body: " + requestBody);
            }

            // Respond to MoMo that the notification has been received.
            return new ResponseEntity<>("Notification received", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}