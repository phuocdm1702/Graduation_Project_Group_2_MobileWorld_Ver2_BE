package com.example.be_datn.controller.pay;

import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.service.momoPayment.MomoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/momo")
public class MomoController {

    @Autowired
    private MomoService momoService;

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
        // The invoice ID (idHD) is now reliably passed in extraData
        String idHD = extraData;

        // Process the payment result to update order status in DB
        momoService.processMomoPaymentResult(idHD, resultCode);

        // Determine frontend redirect URL based on payment status
        String frontendRedirectUrl;
        if ("0".equals(resultCode) && idHD != null && !idHD.trim().isEmpty()) { // MoMo success code
            frontendRedirectUrl = "http://localhost:5173/hoaDon/" + idHD.trim() + "/detail";
        } else {
            // For failed payments, redirect to a failure page or back to the cart
            frontendRedirectUrl = "http://localhost:5173/payment-failure"; // Example failure URL
        }

        return new RedirectView(frontendRedirectUrl);
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