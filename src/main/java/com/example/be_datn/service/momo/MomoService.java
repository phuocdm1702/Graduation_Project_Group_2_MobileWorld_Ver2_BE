package com.example.be_datn.service.momo;

import com.mservice.config.Environment;
import com.mservice.config.PartnerInfo;
import com.mservice.enums.RequestType;
import com.mservice.models.PaymentResponse;
import com.mservice.processor.CreateOrderMoMo;

import com.mservice.shared.utils.LogUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MomoService {

    private Environment environment;

    // Constructor to initialize Environment
    public MomoService(@Value("${momo.partnerCode}") String partnerCode,
                       @Value("${momo.accessKey}") String accessKey,
                       @Value("${momo.secretKey}") String secretKey,
                       @Value("${momo.endpoint}") String endpoint) {
        LogUtils.init();

        // Parse the endpoint to get base URL and create path
        String baseEndpoint = endpoint.substring(0, endpoint.indexOf("/v2/gateway/api/create"));
        String createPath = "/v2/gateway/api/create";

        this.environment = new Environment(
                new com.mservice.config.MoMoEndpoint(baseEndpoint, createPath, "", "", "", "", "", "", ""),
                new PartnerInfo(partnerCode, accessKey, secretKey),
                Environment.EnvTarget.DEV // Assuming dev environment
        );
    }

    public String createOrder(long amount, String orderInfo, String returnUrl, String notifyUrl) throws Exception {
        String requestId = String.valueOf(UUID.randomUUID());
        String orderId = String.valueOf(UUID.randomUUID());

        PaymentResponse captureWalletMoMoResponse = CreateOrderMoMo.process(
                environment,
                orderId,
                requestId,
                Long.toString(amount),
                orderInfo,
                returnUrl,
                notifyUrl,
                "", // extraData
                RequestType.CAPTURE_WALLET,
                Boolean.TRUE // autoCapture
        );

        if (captureWalletMoMoResponse != null && captureWalletMoMoResponse.getResultCode() == 0) {
            return captureWalletMoMoResponse.getPayUrl();
        } else if (captureWalletMoMoResponse != null) {
            throw new Exception("Momo API returned an error: " + captureWalletMoMoResponse.getMessage() + " (Result Code: " + captureWalletMoMoResponse.getResultCode() + ")");
        } else {
            throw new Exception("Failed to get response from Momo API.");
        }
    }

    // The orderReturn method from the original MomoService.java
    public int orderReturn(jakarta.servlet.http.HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            // Iterate over request parameters and put them into a Map
            request.getParameterMap().forEach((key, value) -> params.put(key, value[0]));

            String partnerCode = params.get("partnerCode");
            String accessKey = environment.getPartnerInfo().getAccessKey();
            String requestId = params.get("requestId");
            String amount = params.get("amount");
            String orderId = params.get("orderId");
            String orderInfo = params.get("orderInfo");
            String message = params.get("message");
            String resultCode = params.get("resultCode");
            String responseTime = params.get("responseTime");
            String extraData = params.get("extraData");
            String signature = params.get("signature");

            LogUtils.debug("[MomoService] Received Momo return parameters: " + params);

            if (signature == null || signature.isEmpty()) {
                LogUtils.error("[MomoService] Signature is missing in Momo return response.");
                return -1; // Invalid signature
            }

            // Construct the raw data string for signature verification
            String rawData = "partnerCode=" + partnerCode +
                    "&accessKey=" + accessKey +
                    "&requestId=" + requestId +
                    "&amount=" + amount +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&message=" + message +
                    "&resultCode=" + resultCode +
                    "&responseTime=" + responseTime +
                    "&extraData=" + extraData;

            String calculatedSignature = com.mservice.shared.utils.Encoder.signHmacSHA256(rawData, environment.getPartnerInfo().getSecretKey());

            if (calculatedSignature.equals(signature)) {
                if ("0".equals(resultCode)) { // Momo's success code is "0"
                    LogUtils.info("[MomoService] Momo payment successful for orderId: " + orderId);
                    return 1; // Success
                } else {
                    LogUtils.warn("[MomoService] Momo payment failed for orderId: " + orderId + " with resultCode: " + resultCode + " and message: " + message);
                    return 0; // Failed
                }
            } else {
                LogUtils.error("[MomoService] Invalid signature for Momo return. Calculated: " + calculatedSignature + ", Received: " + signature);
                return -1; // Invalid signature
            }
        } catch (Exception e) {
            LogUtils.error("[MomoService] Error processing Momo return: " + e.getMessage());
            return -1; // Error
        }
    }
}
