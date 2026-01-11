package gr3.workhub.controller;

import gr3.workhub.service.VnpayPaymentService;
import gr3.workhub.service.PaymentIntegrationService;
import gr3.workhub.entity.Transaction;
import gr3.workhub.entity.UserPackage;
import gr3.workhub.repository.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "✅ VNPAY Payment", description = "API thanh toán qua VNPAY cho gói dịch vụ")
@RestController
@CrossOrigin
@RequestMapping("/workhub/api/v1/payments/vnpay")
@RequiredArgsConstructor
public class VnpayPaymentController {
    private final VnpayPaymentService vnpayPaymentService;
    private final TransactionRepository transactionRepository;
    private final PaymentIntegrationService paymentIntegrationService;

    @Operation(summary = "Tạo link thanh toán VNPAY", description = "Trả về URL để redirect sang VNPAY cho user thanh toán.")
    @PostMapping("/create")
    public ResponseEntity<String> createVnpayPaymentUrl(
            HttpServletRequest request,
            @Parameter(description = "ID gói dịch vụ") @RequestParam Integer packageId,
            @Parameter(description = "Giá tiền thanh toán") @RequestParam double price,
            @Parameter(description = "Mô tả đơn hàng", example = "Mua gói tiêu chuẩn") @RequestParam String orderInfo,
            @Parameter(description = "Gia hạn gói?", example = "false") @RequestParam(defaultValue = "false") boolean renew
    ) {
        String url = vnpayPaymentService.createVnpayPaymentUrl(request, packageId, price, orderInfo, renew);
        return ResponseEntity.ok(url);
    }

    @Operation(summary = "Callback từ VNPAY", description = "VNPAY redirect về khi thanh toán xong. Xác thực và cập nhật trạng thái giao dịch.")
    @GetMapping("/return/{type}")
    public ResponseEntity<String> vnpayReturn(
            @PathVariable String type, // purchase hoặc renew
            HttpServletRequest request
    ) {
        Map<String, String[]> params = request.getParameterMap();
        boolean valid = vnpayPaymentService.verifyVnpayReturn(params);
        String txnRef = request.getParameter("vnp_TxnRef");
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        Transaction transaction = transactionRepository.findById(Integer.valueOf(txnRef)).orElse(null);
        if (valid && "00".equals(vnp_ResponseCode) && transaction != null) {
            vnpayPaymentService.updateTransactionStatus(txnRef, Transaction.Status.completed);
            // Tự động kích hoạt gói cho user
            UserPackage userPackage = paymentIntegrationService.activateUserPackage(
                transaction.getUser().getId(),
                transaction.getServicePackage().getId(),
                transaction.getAmount(),
                transaction.getDescription()
            );
            return ResponseEntity.ok("Thanh toán thành công! Gói đã được kích hoạt.");
        } else {
            vnpayPaymentService.updateTransactionStatus(txnRef, Transaction.Status.failed);
            return ResponseEntity.ok("Thanh toán thất bại hoặc không hợp lệ!");
        }
    }
}
