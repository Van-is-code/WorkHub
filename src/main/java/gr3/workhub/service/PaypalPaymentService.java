package gr3.workhub.service;

import gr3.workhub.entity.ServicePackage;
import gr3.workhub.entity.Transaction;
import gr3.workhub.entity.User;
import gr3.workhub.entity.UserPackage;
import gr3.workhub.repository.ServicePackageRepository;
import gr3.workhub.repository.TransactionRepository;
import gr3.workhub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaypalPaymentService {
    @Value("${paypal.client.id:ATHZZnvAWDcSdyMdXumuG9J4DJH585tZUW1xsCVj3qZ5Gvzs4xKMDB0vxkiLzyaphTFAtPWhMDNOyNKo}")
    private String CLIENT_ID;
    
    @Value("${paypal.client.secret:EBFpVsw2boAHOwHQip6m3l0LqWn0ecXSqmGd7XA9YnpUdn1WgvSISkar52bqIyzL-JuFZUR1jn4SsnFk}")
    private String CLIENT_SECRET;
    
    @Value("${paypal.mode:sandbox}")
    private String PAYPAL_MODE;
    
    private String getPaypalApiUrl() {
        return "sandbox".equals(PAYPAL_MODE) 
            ? "https://api-m.sandbox.paypal.com" 
            : "https://api-m.paypal.com";
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final TokenService tokenService;
    private final PaymentValidationService paymentValidationService;

    /**
     * Tạo transaction pending và PayPal order
     */
    public Map<String, Object> createPaypalOrder(HttpServletRequest request, Integer packageId, double amount, String description) {
        log.info("🔄 Creating PayPal order for package: {}, amount: {}", packageId, amount);
        
        try {
            Integer userId = tokenService.extractUserIdFromRequest(request);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("ServicePackage not found"));

            // Kiểm tra validation
            PaymentValidationService.PaymentValidationResult validation = paymentValidationService.validatePurchase(user, servicePackage);
            
            // Nếu có userpackage active, không cho phép tạo transaction mới
            if (validation.getExistingUserPackage() != null && validation.getExistingUserPackage().getStatus() == UserPackage.Status.active) {
                throw new IllegalStateException(validation.getMessage());
            }

            // Tạo transaction pending
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setServicePackage(servicePackage);
            transaction.setAmount(amount);
            transaction.setStatus(Transaction.Status.pending);
            transaction.setDescription(description + " (PayPal)");
            transaction = transactionRepository.save(transaction);
            
            log.info("✅ Created pending transaction: {}", transaction.getId());

            // Tạo PayPal order
            String returnUrl = "http://localhost:8080/workhub/api/v1/payments/paypal/return?transactionId=" + transaction.getId();
            String cancelUrl = "http://localhost:3000/payment-cancel";
            String approvalUrl = createOrder(amount, "USD", returnUrl, cancelUrl);
            
            log.info("✅ Created PayPal order with approval URL");

            Map<String, Object> result = new HashMap<>();
            result.put("transactionId", transaction.getId());
            result.put("orderId", "PAYPAL_" + transaction.getId() + "_" + System.currentTimeMillis());
            result.put("approvalUrl", approvalUrl);
            result.put("amount", amount);
            result.put("description", description);
            
            return result;
            
        } catch (Exception e) {
            log.error("❌ Error creating PayPal order", e);
            throw new RuntimeException("Failed to create PayPal order: " + e.getMessage());
        }
    }

    /**
     * Tạo PayPal order và trả về approval URL
     */
    private String createOrder(double amount, String currency, String returnUrl, String cancelUrl) {
        try {
            // 1. Get access token
            String accessToken = getAccessToken();
            
            // 2. Create order
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = new HashMap<>();
            body.put("intent", "CAPTURE");
            body.put("purchase_units", List.of(Map.of(
                "amount", Map.of("currency_code", currency, "value", String.format("%.2f", amount))
            )));
            body.put("application_context", Map.of(
                "return_url", returnUrl,
                "cancel_url", cancelUrl,
                "brand_name", "WorkHub",
                "landing_page", "LOGIN",
                "user_action", "PAY_NOW"
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(getPaypalApiUrl() + "/v2/checkout/orders", entity, Map.class);
            
            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("PayPal API returned status: " + response.getStatusCode());
            }
            
            Map result = response.getBody();
            if (result == null) {
                throw new RuntimeException("PayPal API returned null response");
            }
            
            // 3. Return approval link
            List<Map> links = (List<Map>) result.get("links");
            for (Map link : links) {
                if ("approve".equals(link.get("rel"))) {
                    return (String) link.get("href");
                }
            }
            throw new RuntimeException("No approval link found in PayPal response");
        } catch (Exception e) {
            log.error("Error creating PayPal order", e);
            throw new RuntimeException("Failed to create PayPal order: " + e.getMessage());
        }
    }

    /**
     * Lấy access token từ PayPal
     */
    private String getAccessToken() {
        try {
            String auth = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + auth);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(getPaypalApiUrl() + "/v1/oauth2/token", entity, Map.class);
            
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to get PayPal access token: " + response.getStatusCode());
            }
            
            Map result = response.getBody();
            if (result == null || !result.containsKey("access_token")) {
                throw new RuntimeException("Invalid PayPal access token response");
            }
            
            return (String) result.get("access_token");
        } catch (Exception e) {
            log.error("Error getting PayPal access token", e);
            throw new RuntimeException("Failed to get PayPal access token: " + e.getMessage());
        }
    }

    /**
     * Capture payment từ PayPal
     */
    public boolean captureOrder(String orderId) {
        try {
            log.info("🔄 Capturing PayPal order: {}", orderId);
            
            String accessToken = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                getPaypalApiUrl() + "/v2/checkout/orders/" + orderId + "/capture", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() != HttpStatus.CREATED) {
                log.error("PayPal capture failed with status: {}", response.getStatusCode());
                return false;
            }
            
            Map result = response.getBody();
            if (result == null) {
                log.error("PayPal capture returned null response");
                return false;
            }
            
            String status = (String) result.get("status");
            log.info("✅ PayPal capture result status: {}", status);
            
            return "COMPLETED".equals(status);
        } catch (Exception e) {
            log.error("❌ Error capturing PayPal order: {}", orderId, e);
            return false;
        }
    }

    /**
     * Cập nhật trạng thái transaction
     */
    public void updateTransactionStatus(Integer transactionId, Transaction.Status status) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));
            transaction.setStatus(status);
            transactionRepository.save(transaction);
            log.info("✅ Updated transaction {} status to {}", transactionId, status);
        } catch (Exception e) {
            log.error("❌ Error updating transaction status: {}", transactionId, e);
            throw new RuntimeException("Failed to update transaction status: " + e.getMessage());
        }
    }
}
