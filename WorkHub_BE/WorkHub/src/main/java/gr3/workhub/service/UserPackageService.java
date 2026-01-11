package gr3.workhub.service;

import gr3.workhub.entity.User;
import gr3.workhub.entity.UserPackage;
import gr3.workhub.repository.UserPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserPackageService {

    private static final Logger logger = LoggerFactory.getLogger(UserPackageService.class);

    private final UserPackageRepository userPackageRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private gr3.workhub.repository.ServicePackageRepository servicePackageRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private gr3.workhub.repository.UserRepository userRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private gr3.workhub.repository.UserBenefitsRepository userBenefitsRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private gr3.workhub.repository.UserPackageHistoryRepository userPackageHistoryRepository;

    @Transactional
    public UserPackage createUserPackage(UserPackage userPackage) {
        if (userPackage.getUser() != null) {
            User user = userRepository.findById(userPackage.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (user.getRole() == User.Role.candidate) {
                user.setRole(User.Role.recruiter);
                userRepository.save(user);
            }
            // XÓA toàn bộ UserPackage cũ của user trước khi tạo mới bằng method xóa chuẩn
            List<UserPackage> oldPackages = userPackageRepository.findByUserId(user.getId());
            logger.debug("[UserPackage] Found {} old packages for userId {}", oldPackages.size(), user.getId());
            for (UserPackage oldPkg : oldPackages) {
                deleteUserPackage(oldPkg.getId());
                logger.debug("[UserPackage] Deleted UserPackage id {} for userId {}", oldPkg.getId(), user.getId());
            }
        }
        // Lưu gói mới trước để có id
        UserPackage savedPackage = userPackageRepository.save(userPackage);
        // Tạo UserBenefits cho user với postAt, jobPostLimit, cvLimit từ ServicePackage
        if (savedPackage.getUser() != null && savedPackage.getServicePackage() != null) {
            var user = savedPackage.getUser();
            var servicePackage = servicePackageRepository.findById(savedPackage.getServicePackage().getId())
                    .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));
            var postAt = gr3.workhub.entity.UserBenefits.PostAt.valueOf(servicePackage.getPostAt().name());
            var userBenefits = userBenefitsRepository.findByUserAndPostAt(user, postAt).orElse(null);
            if (userBenefits == null) {
                userBenefits = new gr3.workhub.entity.UserBenefits();
                userBenefits.setUser(user);
                userBenefits.setPostAt(postAt);
            }
            userBenefits.setUserPackage(savedPackage);
            userBenefits.setJobPostLimit(servicePackage.getJobPostLimit());
            userBenefits.setCvLimit(servicePackage.getCvLimit());
            userBenefits.setDescription(servicePackage.getDescription());
            userBenefits.setUpdatedAt(java.time.LocalDateTime.now());
            userBenefitsRepository.save(userBenefits);
        }
        return savedPackage;
    }

    public UserPackage createUserPackageByUserId(Integer userId, UserPackage userPackage) {
        userPackage.setUser(new User(userId)); // Assuming the `User` entity has a constructor with `id`
        return userPackageRepository.save(userPackage);
    }

    public UserPackage updateUserPackage(Integer id, UserPackage userPackage) {
        UserPackage existing = userPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPackage not found"));
        // Cập nhật các trường cơ bản
        existing.setRenewalDate(userPackage.getRenewalDate());
        existing.setExpirationDate(userPackage.getExpirationDate());
        existing.setPrice(userPackage.getPrice());
        existing.setStatus(userPackage.getStatus());
        existing.setDescription(userPackage.getDescription());
        // Cập nhật servicePackage nếu có
        if (userPackage.getServicePackage() != null) {
            existing.setServicePackage(userPackage.getServicePackage());
            // Đồng bộ UserBenefits nếu đổi gói
            User user = existing.getUser();
            var servicePackage = servicePackageRepository.findById(userPackage.getServicePackage().getId())
                    .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));
            var postAt = gr3.workhub.entity.UserBenefits.PostAt.valueOf(servicePackage.getPostAt().name());
            var userBenefits = userBenefitsRepository.findByUserAndPostAt(user, postAt).orElse(null);
            if (userBenefits == null) {
                userBenefits = new gr3.workhub.entity.UserBenefits();
                userBenefits.setUser(user);
                userBenefits.setPostAt(postAt);
            }
            userBenefits.setUserPackage(existing);
            userBenefits.setJobPostLimit(servicePackage.getJobPostLimit());
            userBenefits.setCvLimit(servicePackage.getCvLimit());
            userBenefits.setDescription(servicePackage.getDescription());
            userBenefits.setUpdatedAt(java.time.LocalDateTime.now());
            userBenefitsRepository.save(userBenefits);
        }
        // Cập nhật user nếu có
        if (userPackage.getUser() != null) {
            existing.setUser(userPackage.getUser());
        }
        return userPackageRepository.save(existing);
    }

    public void deleteUserPackage(Integer id) {
        // Xóa UserBenefits liên quan trước (nếu có)
        userBenefitsRepository.deleteAllByUserPackageId(Long.valueOf(id));
        // Xóa toàn bộ lịch sử gói của user trước khi xóa UserPackage
        UserPackage userPackage = userPackageRepository.findById(id).orElse(null);
        if (userPackage != null && userPackage.getUser() != null) {
            userPackageHistoryRepository.deleteAllByUserId(userPackage.getUser().getId());
            // Nếu user không còn gói nào active, chuyển về candidate
            Integer userId = userPackage.getUser().getId();
            List<UserPackage> remain = userPackageRepository.findByUserId(userId);
            boolean hasActive = remain.stream().anyMatch(pkg -> pkg.getStatus() == UserPackage.Status.active && (pkg.getExpirationDate() == null || pkg.getExpirationDate().isAfter(java.time.LocalDateTime.now())));
            if (!hasActive) {
                var user = userRepository.findById(userId).orElse(null);
                if (user != null && user.getRole() == User.Role.recruiter) {
                    user.setRole(User.Role.candidate);
                    userRepository.save(user);
                }
            }
        }
        userPackageRepository.deleteById(id);
    }

    public List<UserPackage> getUserPackagesByUserId(Integer userId) {
        return userPackageRepository.findByUserId(userId);
    }

    public List<UserPackage> getAllUserPackages() {
        return userPackageRepository.findAll();
    }

    public UserPackage getUserPackageById(Integer id) {
        return userPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPackage not found"));
    }

    public UserPackage buyServicePackage(Integer userId, Integer packageId) {
        // Kiểm tra nếu user đã sở hữu gói này và còn hiệu lực thì không cho mua lại
        var now = java.time.LocalDateTime.now();
        var existed = userPackageRepository.findByUserIdAndServicePackageId(userId, packageId)
            .filter(pkg -> pkg.getExpirationDate() == null || pkg.getExpirationDate().isAfter(now))
            .orElse(null);
        if (existed != null) {
            throw new IllegalStateException("Bạn đã sở hữu gói này, không thể mua lại.");
        }
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var servicePackage = servicePackageRepository.findById(packageId).orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));
        // Nếu user là candidate thì chuyển thành recruiter
        if (user.getRole() == User.Role.candidate) {
            user.setRole(User.Role.recruiter);
            userRepository.save(user);
        }
        UserPackage userPackage = new UserPackage();
        userPackage.setUser(user);
        userPackage.setServicePackage(servicePackage);
        userPackage.setPurchaseDate(java.time.LocalDateTime.now());
        userPackage.setRenewalDate(null);
        userPackage.setExpirationDate(java.time.LocalDateTime.now().plusDays(servicePackage.getDuration()));
        userPackage.setPrice(servicePackage.getPrice().doubleValue());
        userPackage.setStatus(UserPackage.Status.active);
        userPackage.setDescription(servicePackage.getDescription());
        UserPackage savedUserPackage = userPackageRepository.save(userPackage);
        // Tạo UserBenefits tương ứng
        gr3.workhub.entity.UserBenefits benefits = new gr3.workhub.entity.UserBenefits();
        benefits.setUser(user);
        benefits.setUserPackage(savedUserPackage);
        benefits.setJobPostLimit(servicePackage.getJobPostLimit());
        benefits.setCvLimit(servicePackage.getCvLimit());
        // Đảm bảo đồng bộ postAt với ServicePackage
        benefits.setPostAt(gr3.workhub.entity.UserBenefits.PostAt.valueOf(servicePackage.getPostAt().name()));
        benefits.setDescription(servicePackage.getDescription());
        benefits.setUpdatedAt(java.time.LocalDateTime.now());
        userBenefitsRepository.save(benefits);
        return savedUserPackage;
    }

    // Hàm fix dữ liệu: tạo lại UserBenefits cho mọi recruiter đã có UserPackage nhưng chưa có UserBenefits
    public void fixUserBenefitsForAllRecruiters() {
        List<UserPackage> allUserPackages = userPackageRepository.findAll();
        for (UserPackage userPackage : allUserPackages) {
            User user = userPackage.getUser();
            gr3.workhub.entity.ServicePackage servicePackage = userPackage.getServicePackage();
            if (user == null || servicePackage == null) continue;
            gr3.workhub.entity.UserBenefits.PostAt postAt = gr3.workhub.entity.UserBenefits.PostAt.valueOf(servicePackage.getPostAt().name());
            gr3.workhub.entity.UserBenefits userBenefits = userBenefitsRepository.findByUserAndPostAt(user, postAt).orElse(null);
            if (userBenefits == null) {
                userBenefits = new gr3.workhub.entity.UserBenefits();
                userBenefits.setUser(user);
                userBenefits.setPostAt(postAt);
            }
            userBenefits.setUserPackage(userPackage);
            userBenefits.setJobPostLimit(servicePackage.getJobPostLimit());
            userBenefits.setCvLimit(servicePackage.getCvLimit());
            userBenefits.setDescription(servicePackage.getDescription());
            userBenefits.setUpdatedAt(java.time.LocalDateTime.now());
            userBenefitsRepository.save(userBenefits);
        }
    }
}