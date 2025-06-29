// src/main/java/gr3/workhub/repository/UserPackageHistoryRepository.java
package gr3.workhub.repository;

import gr3.workhub.entity.UserPackageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserPackageHistoryRepository extends JpaRepository<UserPackageHistory, Integer> {
    List<UserPackageHistory> findAllByUserId(Integer userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserPackageHistory uph WHERE uph.user.id = :userId")
    int deleteAllByUserId(Integer userId);
}