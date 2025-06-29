// src/main/java/gr3/workhub/service/AdminLogService.java
package gr3.workhub.service;

import gr3.workhub.entity.Admin;
import gr3.workhub.entity.AdminLog;
import gr3.workhub.repository.AdminLogRepository;
import gr3.workhub.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminLogService {
    private final AdminLogRepository adminLogRepository;
    private final AdminRepository adminRepository;

    public void logAction(Integer adminId, String action, String description) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        AdminLog log = new AdminLog();
        log.setAdmin(admin);
        log.setAction(action);
        log.setDescription(description);
        log.setCreatedAt(LocalDateTime.now());
        adminLogRepository.save(log);
    }

    public List<AdminLog> getAllLogs() {
        return adminLogRepository.findAll();
    }

    public List<AdminLog> getLogsByAdminRole(String role) {
        return adminLogRepository.findByAdminRole(role);
    }
}