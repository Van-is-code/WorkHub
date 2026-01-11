package gr3.workhub.controller;

import gr3.workhub.service.PaypalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workhub/api/v1/payments/paypal")
@RequiredArgsConstructor
public class PaypalPaymentController {
    private final PaypalPaymentService paypalPaymentService;

    @PostMapping("/create")
    public ResponseEntity<String> createPaypalOrder(@RequestParam double amount,
                                                    @RequestParam String currency,
                                                    @RequestParam String returnUrl,
                                                    @RequestParam String cancelUrl) {
        String approvalUrl = paypalPaymentService.createOrder(amount, currency, returnUrl, cancelUrl);
        return ResponseEntity.ok(approvalUrl);
    }

    @PostMapping("/capture")
    public ResponseEntity<Boolean> capturePaypalOrder(@RequestParam String orderId) {
        boolean success = paypalPaymentService.captureOrder(orderId);
        return ResponseEntity.ok(success);
    }
}
