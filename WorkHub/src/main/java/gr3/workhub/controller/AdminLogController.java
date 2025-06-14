// src/main/java/gr3/workhub/controller/Admin/AdminLogController.java
package gr3.workhub.controller;

import gr3.workhub.entity.AdminLog;
import gr3.workhub.service.AdminLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quản trị viên: Nhật ký hoạt động", description = "API lấy nhật ký hoạt động của admin")
@RestController
@RequestMapping("/workhub/api/v1/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {
    private final AdminLogService adminLogService;

    @Operation(summary = "Lấy tất cả nhật ký admin", description = "Lấy toàn bộ nhật ký hoạt động của admin")
    @ApiResponse(responseCode = "200", description = "Lấy thành công")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdminLog>> getAllLogs() {
        return ResponseEntity.ok(adminLogService.getAllLogs());
    }

    @Operation(summary = "Lọc nhật ký theo vai trò admin", description = "Lấy nhật ký hoạt động theo role của admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thành công"),
            @ApiResponse(responseCode = "400", description = "Role không hợp lệ")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role")
    public ResponseEntity<List<AdminLog>> getLogsByRole(
            @Parameter(description = "Vai trò admin", required = true) @RequestParam String role) {
        return ResponseEntity.ok(adminLogService.getLogsByAdminRole(role));
    }
}