package gr3.workhub.service;

import gr3.workhub.entity.Transaction;
import gr3.workhub.entity.UserPackage;
import gr3.workhub.entity.User;
import gr3.workhub.entity.ServicePackage;
import gr3.workhub.repository.TransactionRepository;
import gr3.workhub.repository.UserPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentValidationService {

    private final TransactionRepository transactionRepository;
    private final UserPackageRepository userPackageRepository;

    /**
     * Kiểm tra và ngăn chặn duplicate transaction
     * Trả về transaction hiện có hoặc null nếu chưa có
     */
    @Transactional(readOnly = true)
    public Transaction findOrPreventDuplicateTransaction(User user, ServicePackage servicePackage, double amount, String description) {
        List<Transaction> existingTransactions = transactionRepository.findByUserAndServicePackageAndStatus(
                user, servicePackage, Transaction.Status.pending);

        if (!existingTransactions.isEmpty()) {
            log.warn("Duplicate transaction detected for user {} and package {}. Returning existing transaction.",
                    user.getId(), servicePackage.getId());
            return existingTransactions.get(0);
        }

        return null;
    }

    /**
     * Kiểm tra và ngăn chặn duplicate userpackage
     * Trả về userpackage hiện có hoặc null nếu chưa có
     */
    @Transactional(readOnly = true)
    public UserPackage findOrPreventDuplicateUserPackage(User user, ServicePackage servicePackage) {
        List<UserPackage> existingUserPackages = userPackageRepository.findByUserAndServicePackage(user, servicePackage);

        log.info("Checking for existing userpackages for user {} and package {}. Found {} packages.",
                user.getId(), servicePackage.getId(), existingUserPackages.size());

        if (!existingUserPackages.isEmpty()) {
            UserPackage existingPackage = existingUserPackages.get(0);
            log.warn("Duplicate userpackage detected for user {} and package {}. Status: {}, Expiration: {}. Returning existing userpackage.",
                    user.getId(), servicePackage.getId(), existingPackage.getStatus(), existingPackage.getExpirationDate());
            return existingPackage;
        }

        log.info("No existing userpackage found for user {} and package {}", user.getId(), servicePackage.getId());
        return null;
    }

    /**
     * Kiểm tra xem user có userpackage active cho package này không
     */
    @Transactional(readOnly = true)
    public UserPackage findActiveUserPackage(User user, ServicePackage servicePackage) {
        List<UserPackage> existingUserPackages = userPackageRepository.findByUserAndServicePackage(user, servicePackage);

        log.info("Checking for active userpackages for user {} and package {}. Found {} total packages.",
                user.getId(), servicePackage.getId(), existingUserPackages.size());

        for (UserPackage userPackage : existingUserPackages) {
            log.info("Checking userpackage ID {} with status: {}", userPackage.getId(), userPackage.getStatus());
            if (userPackage.getStatus() == UserPackage.Status.active) {
                log.info("Found active userpackage for user {} and package {}", user.getId(), servicePackage.getId());
                return userPackage;
            }
        }

        log.info("No active userpackage found for user {} and package {}", user.getId(), servicePackage.getId());
        return null;
    }

    /**
     * Validate purchase và trả về kết quả với existing records
     */
    @Transactional(readOnly = true)
    public PaymentValidationResult validatePurchase(User user, ServicePackage servicePackage) {
        PaymentValidationResult result = new PaymentValidationResult();

        // Kiểm tra userpackage active hiện tại
        UserPackage activeUserPackage = findActiveUserPackage(user, servicePackage);
        if (activeUserPackage != null) {
            result.setValid(false);
            result.setMessage("Bạn đã có gói này đang hoạt động. Vui lòng gia hạn thay vì mua mới.");
            result.setExistingUserPackage(activeUserPackage);
            return result;
        }

        // Kiểm tra userpackage expired (có thể update thay vì tạo mới)
        UserPackage expiredUserPackage = findOrPreventDuplicateUserPackage(user, servicePackage);

        result.setValid(true);
        result.setMessage("Có thể mua gói này");
        result.setExistingUserPackage(expiredUserPackage); // Có thể là expired package để update
        return result;
    }

    /**
     * Validate renewal và trả về kết quả
     */
    @Transactional(readOnly = true)
    public PaymentValidationResult validateRenewal(User user, ServicePackage servicePackage) {
        PaymentValidationResult result = new PaymentValidationResult();

        UserPackage existingUserPackage = findOrPreventDuplicateUserPackage(user, servicePackage);
        if (existingUserPackage == null) {
            result.setValid(false);
            result.setMessage("Bạn chưa mua gói này. Vui lòng mua gói trước khi gia hạn.");
            return result;
        }

        if (existingUserPackage.getStatus() != UserPackage.Status.active) {
            result.setValid(false);
            result.setMessage("Gói này không còn hoạt động. Vui lòng mua gói mới.");
            result.setExistingUserPackage(existingUserPackage);
            return result;
        }

        result.setValid(true);
        result.setMessage("Có thể gia hạn gói này");
        result.setExistingUserPackage(existingUserPackage);
        return result;
    }

    /**
     * Inner class để trả về kết quả validation
     */
    public static class PaymentValidationResult {
        private boolean valid;
        private String message;
        private Transaction existingTransaction;
        private UserPackage existingUserPackage;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Transaction getExistingTransaction() { return existingTransaction; }
        public void setExistingTransaction(Transaction existingTransaction) { this.existingTransaction = existingTransaction; }

        public UserPackage getExistingUserPackage() { return existingUserPackage; }
        public void setExistingUserPackage(UserPackage existingUserPackage) { this.existingUserPackage = existingUserPackage; }
    }
}