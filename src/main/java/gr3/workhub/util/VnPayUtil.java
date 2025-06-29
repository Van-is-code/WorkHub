//package gr3.workhub.util;
//
//import gr3.workhub.entity.Transaction;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Component
//public class VnPayUtil {
//
//    @Value("${vnpay.tmnCode}")
//    private String vnp_TmnCode;
//
//    @Value("${vnpay.hashSecret}")
//    private String vnp_HashSecret;
//
//    @Value("${vnpay.payUrl}")
//    private String vnp_PayUrl;
//
//    @Value("${vnpay.returnUrl}")
//    private String vnp_ReturnUrl;
//
//    private static VnPayUtil instance;
//
//    @PostConstruct
//    public void init() {
//        instance = this;
//    }
//
//    public static String generateVnPayUrl(Transaction transaction) {
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_Version", "2.1.0");
//        vnp_Params.put("vnp_Command", "pay");
//        vnp_Params.put("vnp_TmnCode", instance.vnp_TmnCode);
//        vnp_Params.put("vnp_Amount", String.valueOf((long) (transaction.getAmount() * 100)));
//        vnp_Params.put("vnp_CurrCode", "VND");
//
//        // Unique transaction reference
//        String txnRef = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
//        vnp_Params.put("vnp_TxnRef", txnRef);
//
//        // Không encode Base64, giữ nguyên mô tả
//        String rawOrderInfo = transaction.getDescription(); // ví dụ: "Hóa đơn thanh toán gói package_123"
//        vnp_Params.put("vnp_OrderInfo", rawOrderInfo);
//
//        vnp_Params.put("vnp_OrderType", "other");
//        vnp_Params.put("vnp_Locale", "vn");
//        vnp_Params.put("vnp_ReturnUrl", instance.vnp_ReturnUrl);
//        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
//        vnp_Params.put("vnp_CreateDate",
//                transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//
//        // Sắp xếp các key tăng dần
//        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//
//        StringBuilder hashData = new StringBuilder();
//        StringBuilder query = new StringBuilder();
//
//        for (int i = 0; i < fieldNames.size(); i++) {
//            String key = fieldNames.get(i);
//            String value = vnp_Params.get(key);
//
//            if (value != null && !value.isEmpty()) {
//                // ✅ Không encode khi hash
//                hashData.append(key).append('=').append(value);
//
//                // ✅ Encode khi tạo URL
//                query.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
//
//                if (i < fieldNames.size() - 1) {
//                    hashData.append('&');
//                    query.append('&');
//                }
//            }
//        }
//
//        String secureHash = hmacSHA512(instance.vnp_HashSecret, hashData.toString());
//        query.append("&vnp_SecureHash=").append(secureHash);
//
//        // Debug log
//        System.out.println("✅ rawOrderInfo: " + rawOrderInfo);
//        System.out.println("🔐 hashData: " + hashData.toString());
//        System.out.println("🔐 hash tạo ra: " + secureHash);
//
//        return instance.vnp_PayUrl + "?" + query;
//    }
//
//    private static String hmacSHA512(String key, String data) {
//        try {
//            javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
//            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//            hmac512.init(secretKey);
//            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            StringBuilder hash = new StringBuilder();
//            for (byte b : bytes) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) hash.append('0');
//                hash.append(hex);
//            }
//            return hash.toString();
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi khi tạo chữ ký HMAC SHA512", e);
//        }
//    }
//}
