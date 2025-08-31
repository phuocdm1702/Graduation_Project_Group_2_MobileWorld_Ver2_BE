
package com.example.be_datn.service.momoPayment;

import com.example.be_datn.mservice.config.Environment;
import com.example.be_datn.mservice.enums.RequestType;
import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.mservice.processor.CreateOrderMoMo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.be_datn.service.order.HoaDonService; // Corrected import

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class MomoService {

    @Autowired
    private HoaDonService hoaDonService; // Injected HoaDonService

    // Map to store MoMo orderId to internal idHD mapping
    private final Map<String, String> momoOrderIdToIdHDMap = new ConcurrentHashMap<>();

    public PaymentResponse createMomoPayment(String orderId, String returnUrl, String notifyUrl, String amount, String orderInfo, String requestId, String idHD) throws Exception {
        momoOrderIdToIdHDMap.put(orderId, idHD); // Store the mapping
        Environment environment = Environment.selectEnv("dev");
        PaymentResponse captureWalletMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, amount, orderInfo, returnUrl, notifyUrl, "", RequestType.PAY_WITH_ATM, Boolean.TRUE);
        return captureWalletMoMoResponse;
    }

    public void processMomoPaymentResult(String momoOrderId, String resultCode) {
        String idHD = momoOrderIdToIdHDMap.remove(momoOrderId); // Retrieve and remove mapping
        if (idHD != null) {
            // Assuming 1 for PAID and 2 for FAILED based on common status codes
            // You might need to adjust these status codes based on your HoaDon entity
            if ("0".equals(resultCode)) { // MoMo success code
                hoaDonService.updateHoaDonStatus(Integer.parseInt(idHD), (short) 1, null); // Assuming 1 is PAID, null for idNhanVien
                System.out.println("MoMo payment successful for order: " + idHD);
            } else {
                hoaDonService.updateHoaDonStatus(Integer.parseInt(idHD), (short) 2, null); // Assuming 2 is FAILED, null for idNhanVien
                System.out.println("MoMo payment failed for order: " + idHD + " with result code: " + resultCode);
            }
        } else {
            System.err.println("MoMo orderId " + momoOrderId + " not found in map. Cannot update order status.");
        }
    }
}
