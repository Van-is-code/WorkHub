//package gr3.workhub.service;
//
//import gr3.workhub.config.PaymentGatewayConfig;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentGatewayService {
//    private final PaymentGatewayConfig paymentGatewayConfig;
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String initiateMomoPayment(String orderId, double amount, String returnUrl) {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("orderId", orderId);
//        requestBody.put("amount", amount);
//        requestBody.put("returnUrl", returnUrl);
//        requestBody.put("apiKey", paymentGatewayConfig.getMomoApiKey());
//
//        return restTemplate.postForObject(paymentGatewayConfig.getMomoApiUrl(), requestBody, String.class);
//    }
//
//    public String initiateVnpayPayment(String orderId, double amount, String returnUrl) {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("orderId", orderId);
//        requestBody.put("amount", amount);
//        requestBody.put("returnUrl", returnUrl);
//        requestBody.put("apiKey", paymentGatewayConfig.getVnpayApiKey());
//
//        return restTemplate.postForObject(paymentGatewayConfig.getVnpayApiUrl(), requestBody, String.class);
//    }
//}