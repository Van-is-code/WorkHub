package gr3.workhub.controller;

import gr3.workhub.entity.ServicePackage;
import gr3.workhub.service.ServicePackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "✅Service Package", description = "Quản lý các gói dịch vụ (mua điểm, dịch vụ VIP, v.v.)")
@RestController
@CrossOrigin

@RequestMapping("/workhub/api/v1/service-packages")
@RequiredArgsConstructor
public class ServicePackageController {

    private final ServicePackageService servicePackageService;

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng>Lấy danh sách tất cả các gói dịch vụ",
            description = "Trả về toàn bộ các gói dịch vụ đang được cung cấp (cho admin, người dùng xem trước khi thanh toán)."
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @GetMapping
    public ResponseEntity<List<ServicePackage>> getAllServicePackages() {
        return ResponseEntity.ok(servicePackageService.getAllServicePackages());
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng>Lấy chi tiết gói dịch vụ theo ID",
            description = "Trả về thông tin chi tiết của một gói dịch vụ cụ thể, dùng khi người dùng nhấn vào 1 gói cụ thể."
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @GetMapping("/{id}")
    public ResponseEntity<ServicePackage> getServicePackageById(
            @Parameter(description = "ID của gói dịch vụ") @PathVariable Integer id) {
        return ResponseEntity.ok(servicePackageService.getServicePackageById(id));
    }

    @Operation(
            summary = "<EndPoint cho trang của Admin>Tạo mới gói dịch vụ",
            description = "Tạo một gói dịch vụ mới (chỉ dành cho admin)."
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @PostMapping
    public ResponseEntity<ServicePackage> createServicePackage(
            @Parameter(description = "Thông tin gói dịch vụ mới") @RequestBody ServicePackage servicePackage) {
        return ResponseEntity.ok(servicePackageService.createServicePackage(servicePackage));
    }

    @Operation(
            summary = "<EndPoint cho trang của admin>Cập nhật gói dịch vụ",
            description = "Chỉnh sửa thông tin của một gói dịch vụ đã có (chỉ admin)."
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicePackage> updateServicePackage(
            @Parameter(description = "ID của gói dịch vụ cần cập nhật") @PathVariable Integer id,
            @Parameter(description = "Dữ liệu cập nhật") @RequestBody ServicePackage servicePackage) {
        return ResponseEntity.ok(servicePackageService.updateServicePackage(id, servicePackage));
    }

    @Operation(
            summary = "<EndPoint cho trang của admin>Xoá gói dịch vụ",
            description = "Xoá một gói dịch vụ khỏi hệ thống (chỉ dành cho admin)."
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicePackage(
            @Parameter(description = "ID của gói dịch vụ cần xoá") @PathVariable Integer id) {
        servicePackageService.deleteServicePackage(id);
        return ResponseEntity.noContent().build();
    }
}
