// src/main/java/gr3/workhub/repository/AdminLogRepository.java
package gr3.workhub.repository;

import gr3.workhub.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminLogRepository extends JpaRepository<AdminLog, Integer> {
    @Query("SELECT l FROM AdminLog l WHERE l.admin.role = :role")
    List<AdminLog> findByAdminRole(String role);
}