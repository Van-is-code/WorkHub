package gr3.workhub.repository;

import gr3.workhub.entity.UserPackage;
import gr3.workhub.entity.User;
import gr3.workhub.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface UserPackageRepository extends JpaRepository<UserPackage, Integer> {
    List<UserPackage> findByUserId(Integer userId);
    Optional<UserPackage> findByUserIdAndServicePackageId(Integer userId, Integer servicePackageId);
    List<UserPackage> findAllByExpirationDateBefore(LocalDateTime date);
    List<UserPackage> findByUserAndServicePackage(User user, ServicePackage servicePackage);
    List<UserPackage> findByUser(User user);

    @Query("SELECT SUM(sp.price) FROM UserPackage up JOIN up.servicePackage sp")
    BigDecimal getTotalRevenue();

    @Query("SELECT sp.name, COUNT(up.id) FROM UserPackage up JOIN up.servicePackage sp GROUP BY sp.name")
    List<Object[]> getPackagePurchaseCounts();

//    // Total revenue from active packages
//    @Query("SELECT SUM(up.price) FROM UserPackage up WHERE up.status = 'active'")
//    Long getTotalRevenue();
//
//    // Count users by package for active packages
//    @Query("SELECT up.servicePackage.id, COUNT(up.user.id) FROM UserPackage up WHERE up.status = 'active' GROUP BY up.servicePackage.id")
//    List<Object[]> countUsersByPackage();
//
//    // Count total buyers for active packages
//    @Query("SELECT COUNT(DISTINCT up.user.id) FROM UserPackage up WHERE up.status = 'active'")
//    Long countTotalBuyers();
}