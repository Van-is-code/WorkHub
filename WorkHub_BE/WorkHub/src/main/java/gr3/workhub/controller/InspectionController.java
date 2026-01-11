package gr3.workhub.controller;

import gr3.workhub.dto.InspectionAdminResponse;
import gr3.workhub.dto.InspectionRequest;
import gr3.workhub.dto.InspectionStatusResponse;
import gr3.workhub.entity.Inspection;
import gr3.workhub.service.InspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1/inspections")
public class InspectionController {
    @Autowired
    private InspectionService inspectionService;

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Tạo yêu cầu xét duyệt hồ sơ công ty ", description = "Tạo mới một yêu cầu kiểm tra cho ứng viên. Lấy userId từ JWT token.")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @PostMapping
    public Inspection createInspection(
            HttpServletRequest request,
            @RequestBody InspectionRequest inspectionRequest) {
        return inspectionService.createInspection(request, inspectionRequest);
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng > Lấy danh sách hồ sơ công ty của công ty theo nhà tuyển dụng", description = "Trả về danh sách trạng thái kiểm tra của ứng viên. Lấy userId từ JWT token.")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @GetMapping("/recruiter/me")
    public List<InspectionStatusResponse> getRecruiterInspections(HttpServletRequest request) {
        return inspectionService.getInspectionsByUser(request)
                .stream()
                .map(i -> new InspectionStatusResponse(i.getId(), i.getInspectionStatus()))
                .toList();
    }

    @Operation(summary = "<EndPoint cho trang của admin >Lấy danh sách hồ sơ công ty cho admin", description = "Trả về danh sách kiểm tra theo trạng thái cho admin")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @GetMapping("/admin")
    public List<InspectionAdminResponse> getAdminInspections(
            @Parameter(description = "Trạng thái hồ sơ công ty ") @RequestParam Inspection.InspectionStatus status) {
        return inspectionService.getInspectionsByStatus(status)
                .stream()
                .map(InspectionAdminResponse::from)
                .toList();
    }

    @Operation(summary = "<EndPoint cho trang của admin > Cập nhật trạng thái hồ sơ công ty", description = "Cập nhật trạng thái kiểm tra theo inspectionId")
    @PreAuthorize("hasRole('SUPER_ADMIN, MODERATOR, SUPPORT')")
    @PutMapping("/{inspectionId}/status")
    public void updateInspectionStatus(
            @Parameter(description = "ID hồ sơ công ty ") @PathVariable Integer inspectionId,
            @Parameter(description = "Trạng thái mới") @RequestParam Inspection.InspectionStatus status) {
        inspectionService.updateInspectionStatus(inspectionId, status);
    }
}