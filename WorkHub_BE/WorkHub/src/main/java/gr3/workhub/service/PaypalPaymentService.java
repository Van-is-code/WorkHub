package gr3.workhub.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class PaypalPaymentService {
    private static final String CLIENT_ID = "ATHZZnvAWDcSdyMdXumuG9J4DJH585tZUW1xsCVj3qZ5Gvzs4xKMDB0vxkiLzyaphTFAtPWhMDNOyNKo";
    private static final String CLIENT_SECRET = "EBFpVsw2boAHOwHQip6m3l0LqWn0ecXSqmGd7XA9YnpUdn1WgvSISkar52bqIyzL-JuFZUR1jn4SsnFk";
    private static final String PAYPAL_API = "https://api-m.sandbox.paypal.com";

    private final RestTemplate restTemplate = new RestTemplate();

    public String createOrder(double amount, String currency, String returnUrl, String cancelUrl) {
        // 1. Get access token
        String accessToken = getAccessToken();
        // 2. Create order
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("intent", "CAPTURE");
        body.put("purchase_units", List.of(Map.of("amount", Map.of("currency_code", currency, "value", String.format("%.2f", amount)))));
        body.put("application_context", Map.of(
            "return_url", returnUrl,
            "cancel_url", cancelUrl
        ));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PAYPAL_API + "/v2/checkout/orders", entity, Map.class);
        Map result = response.getBody();
        // 3. Return approval link
        List<Map> links = (List<Map>) result.get("links");
        for (Map link : links) {
            if ("approve".equals(link.get("rel"))) {
                return (String) link.get("href");
            }
        }
        throw new RuntimeException("No approval link found");
    }

    public String getAccessToken() {
        String auth = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PAYPAL_API + "/v1/oauth2/token", entity, Map.class);
        return (String) response.getBody().get("access_token");
    }

    public boolean captureOrder(String orderId) {
        String accessToken = getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PAYPAL_API + "/v2/checkout/orders/" + orderId + "/capture", entity, Map.class);
        return "COMPLETED".equals(((Map)response.getBody().get("status")).toString());
    }
}
