package com.example.be_datn.controller.clientController;

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
@RequestMapping("/api/client/momo")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientMomoController {

    @Autowired
    private MomoService momoService;

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestParam("amount") String amount, @RequestParam("orderInfo") String orderInfo) {
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            // URLs for client-side flow
            String returnUrl = "http://localhost:3000/checkout-page";
            String notifyUrl = "http://localhost:8080/api/client/momo/notify"; // Can be a separate notify URL for client if needed
            String requestId = String.valueOf(System.currentTimeMillis());

            String idHD = null;
            if (orderInfo != null && orderInfo.startsWith("Thanh toan hoa don ")) {
                idHD = orderInfo.substring("Thanh toan hoa don ".length()).trim();
            }

            if (idHD == null || idHD.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            PaymentResponse paymentResponse = momoService.createMomoPayment(orderId, returnUrl, notifyUrl, amount, orderInfo, requestId, idHD);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping("/notify")
    public ResponseEntity<String> notify(@RequestBody String requestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            String resultCode = jsonNode.get("resultCode").asText();
            String idHD = jsonNode.has("extraData") ? jsonNode.get("extraData").asText() : "";

            if (!idHD.isEmpty()) {
                momoService.processMomoPaymentResult(idHD, resultCode);
            } else {
                System.err.println("Received MoMo IPN without extraData (idHD). Body: " + requestBody);
            }

            return new ResponseEntity<>("Notification received", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}