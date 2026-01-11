package gr3.workhub.repository;

import gr3.workhub.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserPackageRepository extends JpaRepository<UserPackage, Integer> {
    List<UserPackage> findByUserId(Integer userId);
    Optional<UserPackage> findByUserIdAndServicePackageId(Integer userId, Integer servicePackageId);
    List<UserPackage> findAllByExpirationDateBefore(LocalDateTime date);
}