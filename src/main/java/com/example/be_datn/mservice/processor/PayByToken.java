package com.example.be_datn.mservice.processor;

import com.example.be_datn.mservice.config.Environment;
import com.example.be_datn.mservice.enums.Language;
import com.example.be_datn.mservice.models.HttpResponse;
import com.example.be_datn.mservice.models.PaymentRequest;
import com.example.be_datn.mservice.models.PaymentResponse;
import com.example.be_datn.mservice.models.PaymentTokenRequest;
import com.example.be_datn.mservice.shared.constants.Parameter;
import com.example.be_datn.mservice.shared.exception.MoMoException;
import com.example.be_datn.mservice.shared.utils.Encoder;
import com.example.be_datn.mservice.shared.utils.LogUtils;

public class PayByToken extends AbstractProcess<PaymentTokenRequest, PaymentResponse> {
    public PayByToken(Environment environment) {
        super(environment);
    }

    public static PaymentResponse process(Environment env, String orderId, String requestId, String amount, String orderInfo,
                                   String returnUrl, String notifyUrl, String extraData, String partnerClientId, String token, Boolean autoCapture) {
        try {
            PayByToken m2Processor = new PayByToken(env);

            PaymentTokenRequest request = m2Processor.createTokenPaymentRequest(orderId, requestId, amount, orderInfo,
                    returnUrl, notifyUrl, extraData, partnerClientId, token, autoCapture);
            PaymentResponse captureMoMoResponse = m2Processor.execute(request);

            return captureMoMoResponse;
        } catch (Exception exception) {
            LogUtils.error("[PayByTokenProcess] "+ exception);
        }
        return null;
    }

    @Override
    public PaymentResponse execute(PaymentTokenRequest request) throws MoMoException {
        try {

            String payload = getGson().toJson(request, PaymentTokenRequest.class);

            HttpResponse response = execute.sendToMoMo(environment.getMomoEndpoint().getTokenPayUrl(), payload);

            if (response.getStatus() != 200) {
                throw new MoMoException("[PaymentResponse] [" + request.getOrderId() + "] -> Error API");
            }

            System.out.println("uweryei7rye8wyreow8: "+ response.getData());

            PaymentResponse paymentResponse = getGson().fromJson(response.getData(), PaymentResponse.class);
            String responserawData = Parameter.REQUEST_ID + "=" + paymentResponse.getRequestId() +
                    "&" + Parameter.ORDER_ID + "=" + paymentResponse.getOrderId() +
                    "&" + Parameter.MESSAGE + "=" + paymentResponse.getMessage() +
                    "&" + Parameter.PAY_URL + "=" + paymentResponse.getPayUrl() +
                    "&" + Parameter.RESULT_CODE + "=" + paymentResponse.getResultCode();

            LogUtils.info("[PaymentMoMoResponse] rawData: " + responserawData);

            return paymentResponse;

        } catch (Exception exception) {
            LogUtils.error("[PaymentMoMoResponse] "+ exception);
            throw new IllegalArgumentException("Invalid params capture MoMo Request");
        }
    }

    public PaymentTokenRequest createTokenPaymentRequest(String orderId, String requestId, String amount, String orderInfo,
                                                         String returnUrl, String notifyUrl, String extraData, String partnerClientId, String token, Boolean autoCapture) {
        try {
            String requestRawData = new StringBuilder()
                    .append(Parameter.ACCESS_KEY).append("=").append(partnerInfo.getAccessKey()).append("&")
                    .append(Parameter.AMOUNT).append("=").append(amount).append("&")
                    .append(Parameter.EXTRA_DATA).append("=").append(extraData).append("&")
                    .append(Parameter.ORDER_ID).append("=").append(orderId).append("&")
                    .append(Parameter.ORDER_INFO).append("=").append(orderInfo).append("&")
                    .append(Parameter.PARTNER_CLIENT_ID).append("=").append(partnerClientId).append("&")
                    .append(Parameter.PARTNER_CODE).append("=").append(partnerInfo.getPartnerCode()).append("&")
                    .append(Parameter.REQUEST_ID).append("=").append(requestId).append("&")
                    .append(Parameter.TOKEN).append("=").append(token)
                    .toString();

            String signRequest = Encoder.signHmacSHA256(requestRawData, partnerInfo.getSecretKey());
            LogUtils.debug("[PaymentTokenRequest] rawData: " + requestRawData + ", [Signature] -> " + signRequest);

            return new PaymentTokenRequest(partnerInfo.getPartnerCode(), orderId, requestId, Language.EN, "MoMo Store", partnerClientId, token, Long.valueOf(amount), "test StoreId",
                    returnUrl, notifyUrl, orderInfo, extraData, autoCapture, null, signRequest);
        } catch (Exception e) {
            LogUtils.error("[PaymentTokenRequest] "+ e);
        }

        return null;
    }
}
