//package gr3.workhub.service;
//
//import gr3.workhub.entity.*;
//import gr3.workhub.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentService {
//    private final TransactionRepository transactionRepository;
//    private final UserRepository userRepository;
//    private final ServicePackageRepository servicePackageRepository;
//    private final UserPackageRepository userPackageRepository;
//    private final UserBenefitsService userBenefitsService;
//
//    public Transaction createTransaction(Integer userId, Integer packageId, Double amount, String method, String description) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
//                .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));
//
//        Transaction transaction = new Transaction();
//        transaction.setUser(user);
//        transaction.setServicePackage(servicePackage);
//        transaction.setAmount(amount);
//        transaction.setStatus(Transaction.Status.pending);
//        transaction.setDescription(description + " | Method: " + method);
//        transaction.setTransactionDate(LocalDateTime.now());
//
//        return transactionRepository.save(transaction);
//    }
//
//    @Transactional
//    public void handleSuccessfulPayment(Integer userId, Integer packageId, Double price, String description,
//                                        UserBenefits.PostAt postAt, Integer jobPostLimit, Integer cvLimit) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
//                .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));
//
//        // 1. Save UserPackage
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime expiration = now.plusDays(servicePackage.getDuration());
//
//        UserPackage userPackage = new UserPackage();
//        userPackage.setUser(user);
//        userPackage.setServicePackage(servicePackage);
//        userPackage.setPrice(price);
//        userPackage.setStatus(UserPackage.Status.active);
//        userPackage.setDescription(description);
//        userPackage.setRenewalDate(now);
//        userPackage.setExpirationDate(expiration);
//        userPackage = userPackageRepository.save(userPackage);
//
//        // 2. Save or update UserBenefits
//        userBenefitsService.saveOrUpdateUserBenefits(
//                user.getId(),
//                userPackage.getId(),
//                postAt,
//                jobPostLimit,
//                cvLimit,
//                description
//        );
//    }
//
//    public Transaction completeTransaction(Integer transactionId) {
//        Transaction transaction = transactionRepository.findById(transactionId)
//                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
//        transaction.setStatus(Transaction.Status.completed);
//        return transactionRepository.save(transaction);
//    }
//
//    public Transaction failTransaction(Integer transactionId) {
//        Transaction transaction = transactionRepository.findById(transactionId)
//                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
//        transaction.setStatus(Transaction.Status.failed);
//        return transactionRepository.save(transaction);
//    }
//}