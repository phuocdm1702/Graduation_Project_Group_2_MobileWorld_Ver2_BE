package com.example.be_datn.service.momoPayment;

import com.example.be_datn.mservice.config.Environment;
import com.example.be_datn.mservice.enums.RequestType;
import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.mservice.processor.CreateOrderMoMo;
import com.example.be_datn.service.order.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MomoService {

    @Autowired
    private HoaDonService hoaDonService;

    public PaymentResponse createMomoPayment(String orderId, String returnUrl, String notifyUrl, String amount, String orderInfo, String requestId, String idHD) throws Exception {
        Environment environment = Environment.selectEnv("dev");
        // Pass idHD in the extraData field to make the process stateless
        PaymentResponse captureWalletMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, amount, orderInfo, returnUrl, notifyUrl, idHD, RequestType.PAY_WITH_ATM, Boolean.TRUE);
        return captureWalletMoMoResponse;
    }

    public void processMomoPaymentResult(String idHD, String resultCode) {
        if (idHD != null && !idHD.trim().isEmpty()) {
            try {
                Integer hoaDonId = Integer.parseInt(idHD.trim());
                if ("0".equals(resultCode)) { // MoMo success code
                    // Status 1: Chờ giao hàng (Pending delivery)
                    hoaDonService.updateHoaDonStatus(hoaDonId, (short) 1, null);
                    System.out.println("MoMo payment successful for order: " + hoaDonId);
                } else {
                    // Status 4: Đã hủy (Cancelled) for failed payments
                    hoaDonService.updateHoaDonStatus(hoaDonId, (short) 4, null);
                    System.out.println("MoMo payment failed for order: " + hoaDonId + " with result code: " + resultCode);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid order ID format received from MoMo extraData: " + idHD);
            } catch (Exception e) {
                System.err.println("Error processing MoMo payment result for order ID " + idHD + ": " + e.getMessage());
            }
        } else {
            System.err.println("MoMo payment result processed without an order ID (idHD) in extraData.");
        }
    }
}