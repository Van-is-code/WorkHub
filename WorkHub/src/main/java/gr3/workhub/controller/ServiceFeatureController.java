package gr3.workhub.controller;

import gr3.workhub.entity.ServiceFeature;
import gr3.workhub.service.ServiceFeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "✅ Service Feature", description = "Quản lý các tính năng của gói dịch vụ (feature của gói)")
@RestController
@CrossOrigin

@RequestMapping("/workhub/api/v1/service-features")
@RequiredArgsConstructor
public class ServiceFeatureController {

    private final ServiceFeatureService serviceFeatureService;

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng > Lấy tất cả tính năng dịch vụ",
            description = "Trả về danh sách toàn bộ tính năng có thể được gắn với gói dịch vụ."
    )
    @GetMapping
    public ResponseEntity<List<ServiceFeature>> getAllServiceFeatures() {
        return ResponseEntity.ok(serviceFeatureService.getAllServiceFeatures());
    }

    @Operation(
            summary = " <EndPoint cho trang của nhà tuyển dụng > Lấy thông tin tính năng theo ID",
            description = "Trả về chi tiết của một tính năng dịch vụ dựa vào ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ServiceFeature> getServiceFeatureById(
            @Parameter(description = "ID của tính năng") @PathVariable Integer id) {
        return ResponseEntity.ok(serviceFeatureService.getServiceFeatureById(id));
    }

    @Operation(
            summary = "<EndPoint cho trang của admin > Tạo mới tính năng dịch vụ",
            description = "Thêm một tính năng mới để sử dụng trong các gói dịch vụ."
    )
    @PostMapping
    public ResponseEntity<ServiceFeature> createServiceFeature(
            @Parameter(description = "Thông tin chi tiết của tính năng") @RequestBody ServiceFeature serviceFeature) {
        return ResponseEntity.ok(serviceFeatureService.createServiceFeature(serviceFeature));
    }

    @Operation(
            summary = "<EndPoint cho trang của admin > Cập nhật tính năng dịch vụ",
            description = "Chỉnh sửa thông tin của một tính năng đã có."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ServiceFeature> updateServiceFeature(
            @Parameter(description = "ID của tính năng cần cập nhật") @PathVariable Integer id,
            @Parameter(description = "Dữ liệu cập nhật") @RequestBody ServiceFeature serviceFeature) {
        return ResponseEntity.ok(serviceFeatureService.updateServiceFeature(id, serviceFeature));
    }

    @Operation(
            summary = "<EndPoint cho trang của admin > Xoá tính năng dịch vụ",
            description = "Xoá một tính năng ra khỏi hệ thống (có thể không sử dụng được cho các gói nữa)."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceFeature(
            @Parameter(description = "ID của tính năng cần xoá") @PathVariable Integer id) {
        serviceFeatureService.deleteServiceFeature(id);
        return ResponseEntity.noContent().build();
    }
}
