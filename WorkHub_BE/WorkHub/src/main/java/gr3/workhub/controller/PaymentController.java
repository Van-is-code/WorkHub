//package gr3.workhub.controller;
//
//import gr3.workhub.service.PaymentGatewayService;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/workhub/api/v1/payments")
//@RequiredArgsConstructor
//public class PaymentController {
//    private final PaymentGatewayService paymentGatewayService;
//
//    @PostMapping("/momo/initiate")
//    public ResponseEntity<String> initiateMomoPayment(@RequestParam String orderId,
//                                                      @RequestParam double amount,
//                                                      @RequestParam String returnUrl) {
//        String response = paymentGatewayService.initiateMomoPayment(orderId, amount, returnUrl);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/vnpay/initiate")
//    public ResponseEntity<String> initiateVnpayPayment(@RequestParam String orderId,
//                                                       @RequestParam double amount,
//                                                       @RequestParam String returnUrl) {
//        String response = paymentGatewayService.initiateVnpayPayment(orderId, amount, returnUrl);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/momo/callback")
//    public ResponseEntity<?> handleMomoCallback(HttpServletRequest request) {
//        // Extract payment status from request and update transaction
//        return ResponseEntity.ok("MOMO payment verified");
//    }
//
//    @PostMapping("/vnpay/callback")
//    public ResponseEntity<?> handleVnpayCallback(HttpServletRequest request) {
//        // Extract payment status from request and update transaction
//        return ResponseEntity.ok("VNPAY payment verified");
//    }
//}