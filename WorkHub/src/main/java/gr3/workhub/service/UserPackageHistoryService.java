package gr3.workhub.service;

import gr3.workhub.entity.UserPackage;
import gr3.workhub.entity.UserPackageHistory;
import gr3.workhub.repository.UserPackageHistoryRepository;
import gr3.workhub.repository.UserPackageRepository;
import gr3.workhub.repository.UserBenefitsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct; // Add this import

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPackageHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(UserPackageHistoryService.class);

    private final UserPackageRepository userPackageRepository;
    private final UserPackageHistoryRepository userPackageHistoryRepository;
    private final UserBenefitsRepository userBenefitRepository;

//    @PostConstruct
//    public void startBackgroundThread() {
//        Thread thread = new Thread(() -> {
//            while (true) {
//                try {
//                    moveExpiredPackagesToHistory();
//                    Thread.sleep(5000); // Sleep 5 seconds
//                } catch (InterruptedException e) {
//                    logger.warn("Background thread interrupted, exiting.");
//                    Thread.currentThread().interrupt();
//                    break;
//                } catch (Exception ex) {
//                    logger.error("Error in background thread: ", ex);
//                }
//            }
//        });
//        thread.setDaemon(true);
//        thread.start();
//    }

    public void moveExpiredPackagesToHistory() {
        logger.info("Scheduled task started: Checking for expired user packages...");
        LocalDateTime now = LocalDateTime.now();
        List<UserPackage> expiredPackages = userPackageRepository
                .findAllByExpirationDateBefore(now.minusDays(5));

        if (expiredPackages.isEmpty()) {
            logger.info("No expired user packages found for moving to history.");
        } else {
            logger.info("Found {} expired user packages. Moving to history...", expiredPackages.size());
            for (UserPackage userPackage : expiredPackages) {
                // Delete related UserBenefit records
                userBenefitRepository.deleteAllByUserPackageId(userPackage.getId().longValue());

                UserPackageHistory history = new UserPackageHistory();
                history.setUser(userPackage.getUser());
                history.setServicePackage(userPackage.getServicePackage());
                history.setPurchaseDate(userPackage.getPurchaseDate());
                history.setRenewalDate(userPackage.getRenewalDate());
                history.setExpirationDate(userPackage.getExpirationDate());
                history.setPrice(userPackage.getPrice());
                history.setStatus(userPackage.getStatus());
                history.setDescription(userPackage.getDescription());
                userPackageHistoryRepository.save(history);
                userPackageRepository.delete(userPackage);
                logger.info("Moved UserPackage (id: {}) of user (id: {}) to history and deleted from active packages.",
                        userPackage.getId(), userPackage.getUser().getId());
            }
        }
        logger.info("Scheduled task finished.");
    }

    public List<UserPackageHistory> getAllHistories() {
        return userPackageHistoryRepository.findAll();
    }

    public List<UserPackageHistory> getHistoriesByUserId(Integer userId) {
        return userPackageHistoryRepository.findAllByUserId(userId);
    }
}