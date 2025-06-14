 package gr3.workhub.repository;

import gr3.workhub.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface UserBenefitsRepository extends JpaRepository<UserBenefits, Integer> {
    Optional<UserBenefits> findByUserAndUserPackage(User user, UserPackage userPackage);
    Optional<UserBenefits> findByUserId(Integer userId);
    Optional<UserBenefits> findByUserAndUserPackageAndPostAt(User user, UserPackage userPackage, UserBenefits.PostAt postAt);
    Optional<UserBenefits> findByUserAndPostAt(User user, UserBenefits.PostAt postAt);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserBenefits ub WHERE ub.userPackage.id = :userPackageId")
    void deleteAllByUserPackageId(Long userPackageId);
}
