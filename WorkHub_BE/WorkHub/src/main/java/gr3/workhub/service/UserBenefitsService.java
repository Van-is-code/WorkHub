package gr3.workhub.service;

import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBenefitsService {
    private final UserBenefitsRepository userBenefitsRepository;
    private final UserRepository userRepository;
    private final UserPackageRepository userPackageRepository;

    @Transactional
    public void saveOrUpdateUserBenefits(Integer userId, Integer userPackageId,
                                         UserBenefits.PostAt postAt,
                                         Integer jobPostLimit, Integer cvLimit,
                                         String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserPackage userPackage = userPackageRepository.findById(userPackageId)
                .orElseThrow(() -> new IllegalArgumentException("UserPackage not found"));

        UserBenefits userBenefits = userBenefitsRepository
                .findByUserAndUserPackage(user, userPackage)
                .orElse(new UserBenefits());

        userBenefits.setUser(user);
        userBenefits.setUserPackage(userPackage);
        userBenefits.setPostAt(postAt);
        userBenefits.setJobPostLimit(jobPostLimit);
        userBenefits.setCvLimit(cvLimit);
        userBenefits.setDescription(description);
        userBenefits.setUpdatedAt(LocalDateTime.now());
        userBenefitsRepository.save(userBenefits);
    }

    public UserBenefits findByUserId(Integer userId) {
        // Lấy tất cả UserBenefits của user, chọn bản ghi quota lớn nhất hoặc bản ghi đầu tiên
        return userBenefitsRepository.findAll().stream()
                .filter(ub -> ub.getUser().getId().equals(userId))
                .max(java.util.Comparator.comparingInt(ub -> ub.getJobPostLimit() != null ? ub.getJobPostLimit() : 0))
                .orElse(null);
    }

    public List<UserBenefits> getAllBenefitsByUserId(Integer userId) {
        return userBenefitsRepository.findAll().stream()
            .filter(ub -> ub.getUser().getId().equals(userId))
            .toList();
    }
}