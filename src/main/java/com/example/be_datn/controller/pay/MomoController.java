
package com.example.be_datn.controller.pay;

import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.service.momoPayment.MomoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.fasterxml.jackson.databind.JsonNode; // Added import
import com.fasterxml.jackson.databind.ObjectMapper; // Added import

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/momo")
public class MomoController {

    @Autowired
    private MomoService momoService;

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestParam("amount") String amount, @RequestParam("orderInfo") String orderInfo) {
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            String returnUrl = "http://localhost:8080/api/momo/success";
            String notifyUrl = "http://localhost:8080/api/momo/notify";
            String requestId = String.valueOf(System.currentTimeMillis());

            // Extract idHD from orderInfo (e.g., "Thanh toan hoa don 123")
            String idHD = null;
            if (orderInfo != null && orderInfo.startsWith("Thanh toan hoa don ")) {
                idHD = orderInfo.substring("Thanh toan hoa don ".length());
            }

            PaymentResponse paymentResponse = momoService.createMomoPayment(orderId, returnUrl, notifyUrl, amount, orderInfo, requestId, idHD);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/success")
    public RedirectView success(@RequestParam("partnerCode") String partnerCode, @RequestParam("orderId") String orderId, @RequestParam("requestId") String requestId, @RequestParam("amount") String amount, @RequestParam("orderInfo") String orderInfo, @RequestParam("orderType") String orderType, @RequestParam("transId") String transId, @RequestParam("resultCode") String resultCode, @RequestParam("message") String message, @RequestParam("payType") String payType, @RequestParam("responseTime") String responseTime, @RequestParam("extraData") String extraData, @RequestParam("signature") String signature) {
        // Extract idHD from orderInfo (e.g., "Thanh toan hoa don 123")
        String idHD = null;
        if (orderInfo != null && orderInfo.startsWith("Thanh toan hoa don ")) {
            idHD = orderInfo.substring("Thanh toan hoa don ".length());
        }

        // Process the payment result to update order status in DB
        momoService.processMomoPaymentResult(orderId, resultCode);

        // Determine frontend redirect URL based on payment status
        String frontendRedirectUrl;
        if ("0".equals(resultCode) && idHD != null) { // MoMo success code
            frontendRedirectUrl = "http://localhost:5173/hoaDon/" + idHD + "/detail";
        } else {
            // Handle payment failure or missing idHD
            frontendRedirectUrl = "http://localhost:5173/banHang"; // Redirect to a general sales page or error page
        }

        return new RedirectView(frontendRedirectUrl);
    }

    @PostMapping("/notify")
    public ResponseEntity<String> notify(@RequestBody String requestBody) {
        try {
            // Parse the JSON request body
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            String orderId = jsonNode.get("orderId").asText();
            String resultCode = jsonNode.get("resultCode").asText();
            // You might also want to validate the signature here for security
            // String signature = jsonNode.get("signature").asText();

            momoService.processMomoPaymentResult(orderId, resultCode);

            return new ResponseEntity<>("Received", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
