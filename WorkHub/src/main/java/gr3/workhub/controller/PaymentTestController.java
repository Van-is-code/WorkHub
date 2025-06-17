package gr3.workhub.controller;

import gr3.workhub.entity.UserPackage;
import gr3.workhub.service.PaymentTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "✅Payment Test", description = "API mô phỏng thanh toán gói dịch vụ cho người dùng (test/dev)")
@RestController
@CrossOrigin
@RequestMapping("/workhub/api/v1/payments/test")
@RequiredArgsConstructor
public class PaymentTestController {

    private final PaymentTestService paymentTestService;

    @Operation(
            summary = "Mô phỏng thanh toán",
            description = "Giả lập thanh toán cho một người dùng mua gói dịch vụ, trả về thông tin gói đã mua."
    )
    @PostMapping("/simulate")
    public ResponseEntity<UserPackage> simulatePayment(
            HttpServletRequest request,
            @Parameter(description = "ID gói dịch vụ") @RequestParam Integer packageId,
            @Parameter(description = "Giá tiền thanh toán") @RequestParam double price,
            @Parameter(description = "Mô tả thanh toán (mặc định: Test payment)", example = "Mua gói tiêu chuẩn")
            @RequestParam(required = false, defaultValue = "Test payment") String description) {

        UserPackage userPackage = paymentTestService.simulatePayment(request, packageId, price, description);
        return ResponseEntity.ok(userPackage);
    }

    @PostMapping("/renew")
    public ResponseEntity<UserPackage> renewUserPackage(
            HttpServletRequest request,
            @Parameter(description = "ID gói dịch vụ") @RequestParam Integer packageId,
            @Parameter(description = "Giá tiền gia hạn") @RequestParam double price,
            @Parameter(description = "Mô tả gia hạn (mặc định: Gia hạn gói)", example = "Gia hạn gói tiêu chuẩn")
            @RequestParam(required = false, defaultValue = "Gia hạn gói") String description) {

        UserPackage userPackage = paymentTestService.renewUserPackage(request, packageId, price, description);
        return ResponseEntity.ok(userPackage);
    }
}