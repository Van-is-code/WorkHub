package gr3.workhub.controller;

import gr3.workhub.entity.UserPackage;
import gr3.workhub.service.UserPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "✅User Package", description = "Theo dõi các gói dịch vụ mà người dùng đã mua hoặc đang sử dụng")
@RestController
@CrossOrigin

@RequestMapping("/workhub/api/v1/user-packages")
@RequiredArgsConstructor
public class UserPackageController {

    private final UserPackageService userPackageService;

    @Operation(
            summary = "<EndPoint cho trang của admin > Lấy tất cả UserPackage",
            description = "Dành cho admin để kiểm tra toàn bộ các gói dịch vụ đã được mua hoặc gán cho người dùng."
    )
    @GetMapping
    public ResponseEntity<List<UserPackage>> getAllUserPackages() {
        return ResponseEntity.ok(userPackageService.getAllUserPackages());
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng > Lấy các gói của một người dùng theo userId",
            description = "Trả về danh sách các gói (UserPackage) mà người dùng đã mua/được cấp."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPackage>> getUserPackagesByUserId(
            @Parameter(description = "ID của người dùng") @PathVariable Integer userId) {
        return ResponseEntity.ok(userPackageService.getUserPackagesByUserId(userId));
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng > Lấy chi tiết một UserPackage theo ID",
            description = "Trả về chi tiết gói đã được gán cho người dùng."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserPackage> getUserPackageById(
            @Parameter(description = "ID của UserPackage") @PathVariable Integer id) {
        return ResponseEntity.ok(userPackageService.getUserPackageById(id));
    }

    // Đã comment: Nếu cần cho phép tạo UserPackage (manual hoặc payment), mở lại phần này.
    /*
    @Operation(
        summary = "Tạo mới một UserPackage (gán gói cho người dùng)",
        description = "Gán thủ công một gói cho người dùng (admin hoặc hệ thống gọi từ service payment)."
    )
    @PostMapping("/{userId}")
    public ResponseEntity<UserPackage> createUserPackage(
            @Parameter(description = "ID người dùng") @PathVariable Integer userId,
            @RequestBody UserPackage userPackage) {
        return ResponseEntity.ok(userPackageService.createUserPackageByUserId(userId, userPackage));
    }
    */

    @Operation(
            summary = "Cập nhật thông tin gói của người dùng",
            description = "Thay đổi thông tin gói dịch vụ người dùng đang có (VD: thay đổi hạn sử dụng, trạng thái...)."
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserPackage> updateUserPackage(
            @Parameter(description = "ID của UserPackage") @PathVariable Integer id,
            @RequestBody UserPackage userPackage) {
        return ResponseEntity.ok(userPackageService.updateUserPackage(id, userPackage));
    }

    @Operation(
            summary = "Xoá một UserPackage",
            description = "Xoá gói đã gán cho người dùng (cẩn thận, thao tác này thường chỉ admin thực hiện)."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserPackage(
            @Parameter(description = "ID của UserPackage cần xoá") @PathVariable Integer id) {
        userPackageService.deleteUserPackage(id);
        return ResponseEntity.noContent().build();
    }
}
