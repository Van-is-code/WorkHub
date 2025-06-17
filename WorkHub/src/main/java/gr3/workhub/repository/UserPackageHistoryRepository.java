// src/main/java/gr3/workhub/repository/UserPackageHistoryRepository.java
package gr3.workhub.repository;

import gr3.workhub.entity.UserPackageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserPackageHistoryRepository extends JpaRepository<UserPackageHistory, Integer> {
    List<UserPackageHistory> findAllByUserId(Integer userId);
}