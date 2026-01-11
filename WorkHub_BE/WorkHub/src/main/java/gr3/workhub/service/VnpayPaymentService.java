package gr3.workhub.service;

import gr3.workhub.entity.ServicePackage;
import gr3.workhub.entity.Transaction;
import gr3.workhub.entity.User;
import gr3.workhub.repository.ServicePackageRepository;
import gr3.workhub.repository.TransactionRepository;
import gr3.workhub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnpayPaymentService {

    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String TMN_CODE = "75BA1786";
    private static final String HASH_SECRET = "QN6HKV338APWH23KEKYH3AMTNON53TMG";
    private static final String RETURN_URL_PURCHASE = "https://yourdomain.com/workhub/api/v1/payments/vnpay/return/purchase";
    private static final String RETURN_URL_RENEW = "https://yourdomain.com/workhub/api/v1/payments/vnpay/return/renew";

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final TokenService tokenService;

    public String createVnpayPaymentUrl(HttpServletRequest request, Integer packageId, double price, String orderInfo, boolean renew) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        User user = userRepository.findById(userId).orElseThrow();
        ServicePackage servicePackage = servicePackageRepository.findById(packageId).orElseThrow();

        // Create pending transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setServicePackage(servicePackage);
        transaction.setAmount(price);
        transaction.setStatus(Transaction.Status.pending);
        transaction.setDescription(orderInfo);
        transaction = transactionRepository.save(transaction);

        String txnRef = transaction.getId().toString();

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", TMN_CODE);
        params.put("vnp_Amount", String.valueOf((long)(price * 100)));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        String returnUrl = renew ? RETURN_URL_RENEW : RETURN_URL_PURCHASE;
        params.put("vnp_ReturnUrl", returnUrl + "?packageId=" + packageId + "&price=" + price + "&description=" + URLEncoder.encode(orderInfo, StandardCharsets.UTF_8));
        params.put("vnp_IpAddr", request.getRemoteAddr());
        params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String value = params.get(fieldName);
            if (value != null && value.length() > 0) {
                hashData.append(fieldName).append('=').append(value);
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = hmacSHA512(HASH_SECRET, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return VNPAY_URL + "?" + query.toString();
    }

    public boolean verifyVnpayReturn(Map<String, String[]> parameterMap) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            params.put(entry.getKey(), entry.getValue()[0]);
        }
        String receivedHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String value = params.get(fieldName);
            if (value != null && value.length() > 0) {
                hashData.append(fieldName).append('=').append(value);
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }
        String calculatedHash = hmacSHA512(HASH_SECRET, hashData.toString());
        // Thêm log debug để kiểm tra lỗi sai chữ ký
        System.out.println("[VNPAY] PARAMS: " + params);
        System.out.println("[VNPAY] hashData: " + hashData);
        System.out.println("[VNPAY] receivedHash: " + receivedHash);
        System.out.println("[VNPAY] calculatedHash: " + calculatedHash);
        return calculatedHash.equalsIgnoreCase(receivedHash);
    }

    public void updateTransactionStatus(String txnRef, Transaction.Status status) {
        Transaction transaction = transactionRepository.findById(Integer.valueOf(txnRef)).orElseThrow();
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating HMAC SHA512", e);
        }
    }
}
