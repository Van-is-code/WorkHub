package gr3.workhub.controller;

import gr3.workhub.dto.CompanyProfileDTO;
import gr3.workhub.entity.CompanyProfile;
import gr3.workhub.service.CompanyProfileService;
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

@Tag(
        name = "<❌ Đang lỗi tạm thời bỏ qua API này> Company Profile",
        description = "API quản lý hồ sơ công ty, bao gồm tạo mới, lấy chi tiết và danh sách tất cả công ty."
)
@RestController
@CrossOrigin

@RequestMapping("/workhub/api/v1/companies")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    @Operation(
            summary = "Tạo hồ sơ công ty",
            description = "Tạo mới hồ sơ công ty dựa trên thông tin người dùng và dữ liệu đầu vào."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo hồ sơ công ty thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc thiếu thông tin"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/user/{userId}")
    public ResponseEntity<CompanyProfile> createCompany(
            @Parameter(description = "ID của người dùng (recruiter)", required = true)
            @PathVariable Integer userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin hồ sơ công ty cần tạo", required = true
            )
            @RequestBody CompanyProfileDTO dto) {
        return ResponseEntity.ok(companyProfileService.createCompanyProfile(userId, dto));
    }

    @Operation(
            summary = "Lấy hồ sơ công ty theo ID",
            description = "Truy xuất thông tin chi tiết của một công ty dựa trên ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thành công hồ sơ công ty"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công ty với ID tương ứng")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyProfile> getCompanyById(
            @Parameter(description = "ID của công ty cần lấy", required = true)
            @PathVariable Integer id) {
        return ResponseEntity.ok(companyProfileService.getCompanyProfileById(id));
    }

    @Operation(
            summary = "Lấy tất cả hồ sơ công ty",
            description = "Truy xuất danh sách tất cả công ty đã đăng ký hệ thống."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thành công danh sách công ty")
    @GetMapping
    public ResponseEntity<List<CompanyProfile>> getAllCompanies() {
        return ResponseEntity.ok(companyProfileService.getAllCompanyProfiles());
    }


    @Operation(
            summary = "Quản trị viên: Cập nhật trạng thái công ty",
            description = "Cập nhật trạng thái (active, pending, inactive) của hồ sơ công ty theo ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công ty")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @Parameter(description = "ID công ty", required = true) @PathVariable Integer id,
            @Parameter(description = "Trạng thái mới", required = true) @RequestParam CompanyProfile.Status status) {
        companyProfileService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Quản trị viên: Cập nhật loại kiểm định công ty",
            description = "Cập nhật loại kiểm định (none, prestige) của hồ sơ công ty theo ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật loại kiểm định thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công ty")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/inspection")
    public ResponseEntity<Void> updateInspection(
            @Parameter(description = "ID công ty", required = true) @PathVariable Integer id,
            @Parameter(description = "Loại kiểm định mới", required = true) @RequestParam CompanyProfile.Inspection inspection) {
        companyProfileService.updateInspection(id, inspection);
        return ResponseEntity.ok().build();
    }


}
