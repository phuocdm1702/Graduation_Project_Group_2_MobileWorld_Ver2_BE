package com.example.be_datn.config.VNPAY;

import com.example.be_datn.service.order.HoaDonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VNPayService {

    @Autowired
    private HoaDonService hoaDonService; // Inject HoaDonService

    // Map to store VNPay vnp_TxnRef to internal idHD mapping
    private final Map<String, String> vnpTxnRefToIdHDMap = new ConcurrentHashMap<>();

    public String createOrder(HttpServletRequest request, long amount, String orderInfor, String urlReturn, String idHD){ // Added idHD parameter
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8) + System.currentTimeMillis(); // Appending timestamp for more uniqueness
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "250000";

        vnpTxnRefToIdHDMap.put(vnp_TxnRef, idHD); // Store the mapping

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount*100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String salt = VNPayConfig.vnp_HashSecret;
        String vnp_SecureHash = VNPayConfig.hmacSHA512(salt, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    public String orderReturn(HttpServletRequest request) { // Changed return type to String
        System.out.println("VNPayService: Entering orderReturn method.");
        try {
            Map fields = new HashMap();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            String vnp_TxnRef = request.getParameter("vnp_TxnRef"); // Get vnp_TxnRef
            String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus"); // Get transaction status

            System.out.println("VNPayService: Received vnp_TxnRef: " + vnp_TxnRef + ", vnp_TransactionStatus: " + vnp_TransactionStatus);

            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            String signValue = VNPayConfig.hashAllFields(fields);
            System.out.println("VNPayService: Calculated Hash: " + signValue + ", Received Hash: " + vnp_SecureHash);

            String idHD = vnpTxnRefToIdHDMap.remove(vnp_TxnRef); // Retrieve and remove mapping
            System.out.println("VNPayService: Retrieved idHD from map: " + idHD);

            if (signValue.equals(vnp_SecureHash)) {
                System.out.println("VNPayService: Signature valid.");
                if ("00".equals(vnp_TransactionStatus)) { // VNPay success code
                    System.out.println("VNPayService: Transaction status is SUCCESS (00).");
                    if (idHD != null) {
                        try {
                            Integer orderIdInt = Integer.parseInt(idHD); // Parse to Integer
                            System.out.println("VNPayService: Updating order status for idHD: " + idHD + " to PAID.");
                            hoaDonService.updateHoaDonStatus(orderIdInt, (short) 1, null); // Assuming 1 is PAID
                            System.out.println("VNPayService: Order status updated successfully for order: " + idHD);
                            return idHD; // Return idHD for successful redirect
                        } catch (NumberFormatException e) {
                            System.err.println("VNPayService: Error parsing idHD from vnp_TxnRef: " + idHD + ". " + e.getMessage());
                            return null;
                        }
                    } else {
                        System.err.println("VNPayService: vnp_TxnRef " + vnp_TxnRef + " not found in map. Cannot update order status.");
                        return null; // Indicate failure
                    }
                } else {
                    System.out.println("VNPayService: Transaction status is FAILED: " + vnp_TransactionStatus);
                    if (idHD != null) {
                        try {
                            Integer orderIdInt = Integer.parseInt(idHD); // Parse to Integer
                            System.out.println("VNPayService: Updating order status for idHD: " + idHD + " to FAILED.");
                            hoaDonService.updateHoaDonStatus(orderIdInt, (short) 2, null); // Assuming 2 is FAILED
                            System.out.println("VNPayService: Order status updated successfully for order: " + idHD);
                        } catch (NumberFormatException e) {
                            System.err.println("VNPayService: Error parsing idHD from vnp_TxnRef: " + idHD + ". " + e.getMessage());
                        }
                    }
                    return null; // Indicate failure
                }
            } else {
                System.err.println("VNPayService: Invalid Signature. Fields: " + fields);
                return null; // Indicate failure
            }
        } catch (Exception e) {
            System.err.println("VNPayService: Error in orderReturn: " + e.getMessage());
            e.printStackTrace();
            return null; // Indicate failure
        }
    }

}
