//// src/main/java/gr3/workhub/controller/User/PaymentController.java
//package gr3.workhub.controller.User;
//
//import gr3.workhub.entity.Transaction;
//import gr3.workhub.entity.UserPackage;
//import gr3.workhub.service.PaymentService;
//import gr3.workhub.service.UserPackageService;
//import gr3.workhub.util.VnPayUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@CrossOrigin
//
//@RequestMapping("/workhub/api/v1/payments")
//@RequiredArgsConstructor
//public class PaymentController {
//    private final PaymentService paymentService;
//    private final UserPackageService userPackageService;
//
//    // Payment via VNPAY QR
//    @PostMapping("/vnpay/qr")
//    public ResponseEntity<String> payByVnPayQr(@RequestParam Integer userId,
//                                               @RequestParam Integer packageId,
//                                               @RequestParam Double amount,
//                                               @RequestParam String description) {
//        Transaction transaction = paymentService.createTransaction(userId, packageId, amount, "VNPAY_QR", description);
//        String vnpayUrl = VnPayUtil.generateVnPayUrl(transaction);
//        return ResponseEntity.ok(vnpayUrl);
//    }
//
//    // Update transaction status after payment confirmation
//    @PutMapping("/complete/{transactionId}")
//    public ResponseEntity<Transaction> completePayment(@PathVariable Integer transactionId) {
//        Transaction transaction = paymentService.completeTransaction(transactionId);
//
//        // Save UserPackage after successful payment
//        UserPackage userPackage = new UserPackage();
//        userPackage.setUser(transaction.getUser());
//        userPackage.setServicePackage(transaction.getServicePackage());
//        userPackage.setPrice(transaction.getAmount());
//        userPackage.setStatus(UserPackage.Status.active);
//        userPackage.setDescription(transaction.getDescription());
//        // Set renewalDate and expirationDate as needed
//        userPackageService.createUserPackage(userPackage);
//
//        return ResponseEntity.ok(transaction);
//    }
//
//    @PutMapping("/fail/{transactionId}")
//    public ResponseEntity<Transaction> failPayment(@PathVariable Integer transactionId) {
//        return ResponseEntity.ok(paymentService.failTransaction(transactionId));
//    }
//}