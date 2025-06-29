package gr3.workhub.service;

import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import gr3.workhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentTestService {
    private final UserRepository userRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final TransactionRepository transactionRepository;
    private final UserPackageRepository userPackageRepository;
    private final UserBenefitsRepository userBenefitsRepository;
    private final TokenService tokenService;



    public UserPackage simulatePayment(HttpServletRequest request, Integer packageId, double price, String description) {
        LocalDateTime now = LocalDateTime.now();
        Integer userId = tokenService.extractUserIdFromRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));

        // 1. Save transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setServicePackage(servicePackage);
        transaction.setAmount(price);
        transaction.setStatus(Transaction.Status.completed);
        transaction.setDescription(description);
        transaction = transactionRepository.save(transaction);

        // 2. Save UserPackage
        LocalDateTime purchaseDateAt2359 = LocalDate.now().atTime(23, 59);
        int duration = servicePackage.getDuration();
        LocalDateTime expirationDate = purchaseDateAt2359.plusDays(duration).toLocalDate().atStartOfDay();

        UserPackage userPackage = new UserPackage();
        userPackage.setUser(user);
        userPackage.setServicePackage(servicePackage);
        userPackage.setPrice(price);
        userPackage.setStatus(UserPackage.Status.active);
        userPackage.setDescription(description);
        userPackage.setExpirationDate(expirationDate);

        userPackage = userPackageRepository.save(userPackage);

        // 3. Upsert UserBenefits for ServicePackage limits (no features table)
        UserBenefits.PostAt postAt = UserBenefits.PostAt.valueOf(servicePackage.getPostAt().name());
        UserBenefits userBenefits = userBenefitsRepository
            .findByUserAndPostAt(user, postAt)
            .orElse(null);

        if (userBenefits == null) {
            userBenefits = new UserBenefits();
            userBenefits.setUser(user);
            userBenefits.setPostAt(postAt);
        }
        userBenefits.setUserPackage(userPackage);
        userBenefits.setJobPostLimit(servicePackage.getJobPostLimit());
        userBenefits.setCvLimit(servicePackage.getCvLimit());
        userBenefits.setDescription(servicePackage.getDescription());
        userBenefits.setUpdatedAt(now);
        userBenefitsRepository.save(userBenefits);

        return userPackage;
    }

    public UserPackage renewUserPackage(HttpServletRequest request, Integer packageId, double price, String description) {
        LocalDateTime now = LocalDateTime.now();
        Integer userId = tokenService.extractUserIdFromRequest(request);

        // 1. Check if user has a package
        UserPackage userPackage = userPackageRepository.findByUserIdAndServicePackageId(userId, packageId)
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa mua bất kì gói nào"));

        LocalDateTime expirationDate = userPackage.getExpirationDate();

        // 2. Check if renewal is allowed (expired or will expire in 5 days)
        if (expirationDate.isAfter(now) && expirationDate.minusDays(5).isAfter(now)) {
            throw new IllegalStateException("Gói này chưa hết hạn, bạn không thể gia hạn.");
        }
        if (expirationDate.isBefore(now.minusDays(5))) {
            throw new IllegalStateException("Đã hết thời gian gia hạn, vui lòng đăng kí gói mới.");
        }

        // 3. Save renewal transaction
        User user = userPackage.getUser();
        ServicePackage servicePackage = userPackage.getServicePackage();

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setServicePackage(servicePackage);
        transaction.setAmount(price);
        transaction.setStatus(Transaction.Status.completed);
        transaction.setDescription("Gia hạn: " + description);
        transactionRepository.save(transaction);

        // 4. Update UserPackage (keep purchaseDate, update renewalDate & expirationDate)
        int duration = servicePackage.getDuration();
        userPackage.setRenewalDate(now);
        userPackage.setExpirationDate(expirationDate.plusDays(duration));
        userPackage.setPrice(price);
        userPackage.setStatus(UserPackage.Status.active);
        userPackage.setDescription(description);
        userPackage = userPackageRepository.save(userPackage);

        // 5. Update UserBenefits as before
        UserBenefits.PostAt postAt = UserBenefits.PostAt.valueOf(servicePackage.getPostAt().name());
        UserBenefits userBenefits = userBenefitsRepository
                .findByUserAndPostAt(user, postAt)
                .orElse(null);

        if (userBenefits == null) {
            userBenefits = new UserBenefits();
            userBenefits.setUser(user);
            userBenefits.setPostAt(postAt);
        }
        userBenefits.setUserPackage(userPackage);
        userBenefits.setJobPostLimit(servicePackage.getJobPostLimit());
        userBenefits.setCvLimit(servicePackage.getCvLimit());
        userBenefits.setDescription(servicePackage.getDescription());
        userBenefits.setUpdatedAt(now);
        userBenefitsRepository.save(userBenefits);


        return userPackage;
    }
}