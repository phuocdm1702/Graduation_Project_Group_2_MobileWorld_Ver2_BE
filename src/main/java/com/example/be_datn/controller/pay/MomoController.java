
package com.example.be_datn.controller.pay;

import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.service.momoPayment.MomoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            PaymentResponse paymentResponse = momoService.createMomoPayment(orderId, returnUrl, notifyUrl, amount, orderInfo, requestId);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> success(@RequestParam("partnerCode") String partnerCode, @RequestParam("orderId") String orderId, @RequestParam("requestId") String requestId, @RequestParam("amount") String amount, @RequestParam("orderInfo") String orderInfo, @RequestParam("orderType") String orderType, @RequestParam("transId") String transId, @RequestParam("resultCode") String resultCode, @RequestParam("message") String message, @RequestParam("payType") String payType, @RequestParam("responseTime") String responseTime, @RequestParam("extraData") String extraData, @RequestParam("signature") String signature) {
        // Process the successful payment
        return new ResponseEntity<>("Payment Successful", HttpStatus.OK);
    }

    @PostMapping("/notify")
    public ResponseEntity<String> notify(@RequestBody String requestBody) {
        // Process the notification from MoMo
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
