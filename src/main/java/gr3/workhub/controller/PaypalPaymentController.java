package gr3.workhub.controller;

import gr3.workhub.entity.Transaction;
import gr3.workhub.entity.UserPackage;
import gr3.workhub.repository.TransactionRepository;
import gr3.workhub.service.PaypalPaymentService;
import gr3.workhub.service.PaymentIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/workhub/api/v1/payments/paypal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PayPal Payment", description = "PayPal payment endpoints")
public class PaypalPaymentController {

    private final PaypalPaymentService paypalPaymentService;
    private final PaymentIntegrationService paymentIntegrationService;
    private final TransactionRepository transactionRepository;

    @Operation(summary = "Tạo PayPal order", description = "Tạo PayPal order và trả về approval URL")
    @PostMapping("/create")
    public ResponseEntity<?> createPaypalOrder(
            @Parameter(description = "ID gói dịch vụ") @RequestParam Integer packageId,
            @Parameter(description = "Số tiền") @RequestParam Double amount,
            @Parameter(description = "Mô tả") @RequestParam String description,
            @Parameter(description = "Loại tiền tệ") @RequestParam(defaultValue = "USD") String currency,
            HttpServletRequest request
    ) {
        log.info("🔄 PayPal create order called - packageId: {}, amount: {}, description: {}", packageId, amount, description);
        
        try {
            Map<String, Object> result = paypalPaymentService.createPaypalOrder(request, packageId, amount, description);
            
            log.info("✅ PayPal order created successfully - transactionId: {}, orderId: {}", 
                    result.get("transactionId"), result.get("orderId"));
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error creating PayPal order", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi tạo PayPal order: " + e.getMessage()));
        }
    }

    @Operation(summary = "Xác nhận thanh toán PayPal", description = "Xác nhận và hoàn tất thanh toán PayPal")
    @PostMapping("/capture")
    public ResponseEntity<?> capturePaypalOrder(
            @Parameter(description = "ID đơn hàng PayPal") @RequestParam String orderId,
            @Parameter(description = "ID giao dịch") @RequestParam Integer transactionId
    ) {
        log.info("🔄 PayPal capture called - orderId: {}, transactionId: {}", orderId, transactionId);
        
        try {
            // Kiểm tra transaction có tồn tại và đang pending không
            Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
            
            if (transaction.getStatus() != Transaction.Status.pending) {
                log.warn("⚠️ Transaction {} is not pending, current status: {}", transactionId, transaction.getStatus());
                if (transaction.getStatus() == Transaction.Status.completed) {
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Thanh toán đã được xác nhận trước đó.",
                        "userPackage", "already_activated"
                    ));
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "Transaction không ở trạng thái pending"));
                }
            }
            
            // Capture payment từ PayPal
            boolean captureSuccess = paypalPaymentService.captureOrder(orderId);
            
            if (!captureSuccess) {
                log.error("❌ PayPal capture failed for orderId: {}", orderId);
                paypalPaymentService.updateTransactionStatus(transactionId, Transaction.Status.failed);
                return ResponseEntity.badRequest().body(Map.of("error", "Xác nhận thanh toán PayPal thất bại"));
            }
            
            // Update transaction status to completed
            paypalPaymentService.updateTransactionStatus(transactionId, Transaction.Status.completed);
            
            log.info("🔄 Creating UserPackage for transaction: {}", transactionId);
            
            // Tạo UserPackage từ transaction đã completed
            UserPackage userPackage = paymentIntegrationService.handlePaymentCompletionFromTransaction(transactionId);
            
            log.info("✅ PayPal payment captured successfully for orderId: {}, transactionId: {}, userPackageId: {}", 
                    orderId, transactionId, userPackage.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Thanh toán thành công! Gói đã được kích hoạt.",
                "userPackage", userPackage
            ));
        } catch (Exception e) {
            log.error("❌ Error capturing PayPal order: {}", orderId, e);
            paypalPaymentService.updateTransactionStatus(transactionId, Transaction.Status.failed);
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi xác nhận thanh toán: " + e.getMessage()));
        }
    }

    @Operation(summary = "Xử lý callback từ PayPal", description = "Xử lý callback khi user quay về từ PayPal")
    @GetMapping("/return")
    public ResponseEntity<?> handlePaypalReturn(
            @Parameter(description = "PayPal token (order ID)") @RequestParam(required = false) String token,
            @Parameter(description = "PayPal Payer ID") @RequestParam(required = false) String PayerID,
            @Parameter(description = "Transaction ID from return_url") @RequestParam(required = false) Integer transactionId
    ) {
        log.info("🔄 PayPal return callback - token: {}, PayerID: {}, transactionId: {}", token, PayerID, transactionId);
        try {
            if (token == null || transactionId == null) {
                log.error("❌ Missing token or transactionId in PayPal return");
                return ResponseEntity.status(302)
                    .header("Location", "http://localhost:5173/payment-cancel?error=missing_info")
                    .build();
            }
            // Không capture ở backend nữa, chỉ redirect về FE với đủ thông tin
            String redirectUrl = String.format("http://localhost:5173/payment-confirm?token=%s&PayerID=%s&transactionId=%d", token, PayerID, transactionId);
            log.info("🔄 Redirecting to frontend: {}", redirectUrl);
            return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
        } catch (Exception e) {
            log.error("❌ Error processing PayPal return: {}", token, e);
            return ResponseEntity.status(302)
                .header("Location", "http://localhost:5173/payment-cancel?error=processing_failed")
                .build();
        }
    }

    @Operation(summary = "Tạo PayPal order cho gia hạn", description = "Tạo PayPal order để gia hạn gói dịch vụ")
    @PostMapping("/create-renewal")
    public ResponseEntity<Map<String, Object>> createPaypalRenewalOrder(
            HttpServletRequest request,
            @Parameter(description = "ID gói dịch vụ") @RequestParam Integer packageId,
            @Parameter(description = "Số tiền gia hạn") @RequestParam double amount,
            @Parameter(description = "Mô tả gia hạn", example = "Gia hạn gói tiêu chuẩn") @RequestParam String description,
            @Parameter(description = "Loại tiền tệ", example = "USD") @RequestParam(defaultValue = "USD") String currency) {

        try {
            log.info("🔄 Creating PayPal renewal order for package: {}, amount: {}", packageId, amount);
            
            // Sử dụng PaymentIntegrationService để xử lý gia hạn
            UserPackage userPackage = paymentIntegrationService.handleSuccessfulRenewal(request, packageId, amount, description);
            
            // Tạo PayPal order cho gia hạn
            Map<String, Object> result = paypalPaymentService.createPaypalOrder(request, packageId, amount, "Gia hạn: " + description);
            
            log.info("✅ PayPal renewal order created successfully - transactionId: {}, orderId: {}", 
                    result.get("transactionId"), result.get("orderId"));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("❌ Error creating PayPal renewal order: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
