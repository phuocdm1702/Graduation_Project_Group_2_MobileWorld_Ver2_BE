
package com.example.be_datn.service.momoPayment;

import com.example.be_datn.mservice.config.Environment;
import com.example.be_datn.mservice.enums.RequestType;
import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.mservice.processor.CreateOrderMoMo;
import org.springframework.stereotype.Service;

@Service
public class MomoService {

    public PaymentResponse createMomoPayment(String orderId, String returnUrl, String notifyUrl, String amount, String orderInfo, String requestId) throws Exception {
        Environment environment = Environment.selectEnv("dev");
        PaymentResponse captureWalletMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, amount, orderInfo, returnUrl, notifyUrl, "", RequestType.PAY_WITH_ATM, Boolean.TRUE);
        return captureWalletMoMoResponse;
    }
}
