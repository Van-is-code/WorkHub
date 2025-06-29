package gr3.workhub.service;

import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PaymentIntegrationService {
    private final UserRepository userRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final TransactionRepository transactionRepository;
    private final UserPackageRepository userPackageRepository;
    private final UserBenefitsRepository userBenefitsRepository;
    private final TokenService tokenService;
    private final PaymentValidationService paymentValidationService;
    private static final Logger log = LoggerFactory.getLogger(PaymentIntegrationService.class);

    @Transactional
    public UserPackage handleSuccessfulPayment(HttpServletRequest request, Integer packageId, double price, String description) {
        LocalDateTime now = LocalDateTime.now();
        Integer userId = tokenService.extractUserIdFromRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));

        // Sử dụng PaymentValidationService để kiểm tra và ngăn chặn duplicate
        PaymentValidationService.PaymentValidationResult validation = paymentValidationService.validatePurchase(user, servicePackage);

        // Nếu có userpackage active, không cho phép tạo mới
        if (validation.getExistingUserPackage() != null && validation.getExistingUserPackage().getStatus() == UserPackage.Status.active) {
            throw new IllegalStateException(validation.getMessage());
        }

        // 1. Save transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setServicePackage(servicePackage);
        transaction.setAmount(price);
        transaction.setStatus(Transaction.Status.completed);
        transaction.setDescription(description);
        transaction = transactionRepository.save(transaction);

        // 2. Save UserPackage - kiểm tra xem đã có userpackage chưa (có thể là expired)
        UserPackage existingUserPackage = validation.getExistingUserPackage();
        UserPackage userPackage;

        if (existingUserPackage != null) {
            // Update existing userpackage (có thể là expired)
            log.info("Updating existing userpackage for user {} and package {}", userId, packageId);
            existingUserPackage.setStatus(UserPackage.Status.active);
            existingUserPackage.setExpirationDate(now.plusDays(servicePackage.getDuration()));
            existingUserPackage.setPrice(price);
            existingUserPackage.setDescription(description);
            userPackage = userPackageRepository.save(existingUserPackage);
        } else {
            // Create new userpackage
            log.info("Creating new userpackage for user {} and package {}", userId, packageId);
            int duration = servicePackage.getDuration();
            LocalDateTime expirationDate = now.plusDays(duration);

            userPackage = new UserPackage();
            userPackage.setUser(user);
            userPackage.setServicePackage(servicePackage);
            userPackage.setPrice(price);
            userPackage.setStatus(UserPackage.Status.active);
            userPackage.setDescription(description);
            userPackage.setExpirationDate(expirationDate);

            userPackage = userPackageRepository.save(userPackage);
        }

        // 3. Upsert UserBenefits for single feature
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

    @Transactional
    public UserPackage handleSuccessfulRenewal(HttpServletRequest request, Integer packageId, double price, String description) {
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

        // 5. Update UserBenefits for single feature
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

    @Transactional
    public UserPackage activateUserPackage(Integer userId, Integer packageId, double price, String description) {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));

        // Sử dụng PaymentValidationService để kiểm tra và ngăn chặn duplicate
        PaymentValidationService.PaymentValidationResult validation = paymentValidationService.validatePurchase(user, servicePackage);

        // Kiểm tra xem đã có transaction pending cho user và package này chưa
        Transaction existingTransaction = validation.getExistingTransaction();
        if (existingTransaction == null) {
            throw new IllegalStateException("Không tìm thấy transaction pending cho user và package này");
        }

        // Update transaction status to completed
        existingTransaction.setStatus(Transaction.Status.completed);
        transactionRepository.save(existingTransaction);

        // Kiểm tra xem đã có userpackage cho user và package này chưa (có thể là expired)
        UserPackage existingUserPackage = validation.getExistingUserPackage();
        UserPackage userPackage;

        if (existingUserPackage != null) {
            // Nếu đã có userpackage (có thể là expired), update thay vì tạo mới
            log.info("Updating existing userpackage for user {} and package {}", userId, packageId);
            existingUserPackage.setStatus(UserPackage.Status.active);
            existingUserPackage.setExpirationDate(now.plusDays(servicePackage.getDuration()));
            existingUserPackage.setPrice(price);
            existingUserPackage.setDescription(description);
            userPackage = userPackageRepository.save(existingUserPackage);
        } else {
            // Tạo userpackage mới nếu chưa có
            log.info("Creating new userpackage for user {} and package {}", userId, packageId);
            int duration = servicePackage.getDuration();
            LocalDateTime expirationDate = now.plusDays(duration);

            userPackage = new UserPackage();
            userPackage.setUser(user);
            userPackage.setServicePackage(servicePackage);
            userPackage.setPrice(price);
            userPackage.setStatus(UserPackage.Status.active);
            userPackage.setDescription(description);
            userPackage.setExpirationDate(expirationDate);

            userPackage = userPackageRepository.save(userPackage);
        }

        // Update UserBenefits
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

    @Transactional
    public UserPackage handlePaymentCompletionFromTransaction(Integer transactionId) {
        LocalDateTime now = LocalDateTime.now();

        // Get transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Kiểm tra transaction đã completed chưa
        if (transaction.getStatus() != Transaction.Status.completed) {
            throw new IllegalStateException("Transaction chưa được completed");
        }

        User user = transaction.getUser();
        ServicePackage servicePackage = transaction.getServicePackage();

        log.info("🔄 Handling payment completion for transaction: {}, user: {}, package: {}",
                transactionId, user.getId(), servicePackage.getId());

        // Kiểm tra xem đã có userpackage active cho user và package này chưa
        List<UserPackage> existingUserPackages = userPackageRepository.findByUserAndServicePackage(user, servicePackage);
        UserPackage existingActivePackage = existingUserPackages.stream()
                .filter(up -> up.getStatus() == UserPackage.Status.active)
                .findFirst()
                .orElse(null);

        if (existingActivePackage != null) {
            log.warn("⚠️ User {} already has active package {} for service package {}",
                    user.getId(), existingActivePackage.getId(), servicePackage.getId());
            return existingActivePackage; // Trả về package đã có thay vì tạo mới
        }

        // Kiểm tra xem đã có userpackage cho user và package này chưa (có thể là expired)
        UserPackage existingUserPackage = existingUserPackages.isEmpty() ? null : existingUserPackages.get(0);
        UserPackage userPackage;

        if (existingUserPackage != null) {
            // Update existing userpackage (có thể là expired)
            log.info("Updating existing userpackage for user {} and package {}", user.getId(), servicePackage.getId());
            existingUserPackage.setStatus(UserPackage.Status.active);
            existingUserPackage.setExpirationDate(now.plusDays(servicePackage.getDuration()));
            existingUserPackage.setPrice(transaction.getAmount());
            existingUserPackage.setDescription(transaction.getDescription());
            userPackage = userPackageRepository.save(existingUserPackage);
        } else {
            // Create new userpackage
            log.info("Creating new userpackage for user {} and package {}", user.getId(), servicePackage.getId());
            int duration = servicePackage.getDuration();
            LocalDateTime expirationDate = now.plusDays(duration);

            userPackage = new UserPackage();
            userPackage.setUser(user);
            userPackage.setServicePackage(servicePackage);
            userPackage.setPrice(transaction.getAmount());
            userPackage.setStatus(UserPackage.Status.active);
            userPackage.setDescription(transaction.getDescription());
            userPackage.setExpirationDate(expirationDate);

            userPackage = userPackageRepository.save(userPackage);
        }

        // Update UserBenefits
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

        log.info("✅ Payment completion handled successfully for transaction: {}, userPackage: {}",
                transactionId, userPackage.getId());

        return userPackage;
    }
}