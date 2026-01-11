package gr3.workhub.controller;

import gr3.workhub.dto.ApplicationDTO;
import gr3.workhub.dto.AppliedJobsDTO;
import gr3.workhub.entity.Application;
import gr3.workhub.service.ApplicationService;
import gr3.workhub.service.ResumeService;
import gr3.workhub.service.TokenService;
import gr3.workhub.service.UserBenefitsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/workhub/api/v1/applications")
@RequiredArgsConstructor
@Tag(name = " ✅ Application APIs", description = "Ở ENDPOINT DOWLOAD FILE CÓ THỂ CHỌN 1 TRONG 2 PHƯƠNG PHÁP TẢI XUỐNG THEO {resumeId} HOẶC {applicationId}, API nộp hồ sơ ứng tuyển và quản lý đơn ứng tuyển")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ResumeService resumeService;
    private final UserBenefitsService userBenefitsService;
    private final TokenService tokenService;

    @Operation(
            summary = "<EndPoint cho trang của ứng viên> Ứng tuyển công việc",
            description = "Ứng viên ứng tuyển vào công việc bằng cách gửi resume đã lưu. " +
                    "Cần truyền vào ID của công việc và ID của resume. Lấy candidateId từ JWT token."
    )

    @PostMapping("/{jobId}")
    public ResponseEntity<Application> applyForJob(
            @Parameter(description = "ID của công việc cần ứng tuyển", example = "3") @PathVariable Integer jobId,
            @Parameter(description = "ID của resume được chọn", example = "1") @RequestParam("resumeId") Integer resumeId,
            HttpServletRequest request) {
        Application application = applicationService.applyForJob(jobId, resumeId, request);
        return ResponseEntity.ok(application);
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên> Ứng tuyển công việc kèm chọn slot phỏng vấn",
            description = "Ứng viên ứng tuyển vào công việc, chọn slot phỏng vấn phù hợp. Truyền jobId, resumeId, slotId."
    )
    @PostMapping("/{jobId}/with-slot")
    public ResponseEntity<ApplicationDTO> applyForJobWithSlot(
            @Parameter(description = "ID của công việc cần ứng tuyển", example = "3") @PathVariable Integer jobId,
            @Parameter(description = "ID của resume được chọn", example = "1") @RequestParam("resumeId") Integer resumeId,
            @Parameter(description = "ID của slot phỏng vấn", example = "uuid") @RequestParam("slotId") String slotId,
            HttpServletRequest request) {
        Application application = applicationService.applyForJobWithSlot(jobId, resumeId, slotId, request);
        ApplicationDTO dto = new ApplicationDTO(
            application.getId(),
            application.getJob().getTitle(),
            application.getCandidate().getFullname(),
            application.getCandidate().getEmail(),
            application.getCandidate().getPhone(),
            application.getStatus() != null ? application.getStatus().toString() : null,
            application.getAppliedAt(),
            application.getResume() != null ? application.getResume().getFile() : null,
            application.getResume() != null ? application.getResume().getId() : null,
            application.getResume() != null ? application.getResume().getContent() : null // truyền content
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{jobId}/resumes")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobId(
            @PathVariable Integer jobId,
            HttpServletRequest request) {
        List<ApplicationDTO> dtos = applicationService.getApplicationsByJobId(jobId, request);
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên và nhà tuyển dụng >Tải xuống hồ sơ (resume) theo resumeId",
            description = "Nhà tuyển dụng có thể tải file hồ sơ PDF mà ứng viên đã gửi kèm theo resumeId."
    )
    @GetMapping("/resumes/{resumeId}/download")
    public ResponseEntity<byte[]> downloadResume(
            @Parameter(description = "ID của resume cần tải", example = "2") @PathVariable Integer resumeId) {
        ApplicationDTO dto = applicationService.getApplicationDTOForResumeDownload(resumeId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.getUserFullname() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(dto.getResumeFile());
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên và nhà tuyển dụng >Tải xuống hồ sơ (resume) theo applicationId",
            description = "Cho phép tải file hồ sơ ứng viên dựa vào ID của đơn ứng tuyển."
    )
    @GetMapping("/{applicationId}/resume/download")
    public ResponseEntity<byte[]> downloadResumeByApplicationId(
            @Parameter(description = "ID của đơn ứng tuyển cần tải hồ sơ", example = "1") @PathVariable Integer applicationId,
            HttpServletRequest request) {
        Integer recruiterId = tokenService.extractUserIdFromRequest(request);
        ApplicationDTO dto = applicationService.getApplicationDTOForResumeDownloadByApplicationId(applicationId, recruiterId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.getUserFullname() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(dto.getResumeFile());
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên > Lấy danh sách công việc đã ứng tuyển của ứng viên",
            description = "Ứng viên có thể xem lại toàn bộ các công việc mình đã nộp hồ sơ. Lấy userId từ JWT token."
    )
    @GetMapping("/appliedJobs")
    public ResponseEntity<List<AppliedJobsDTO>> getApplicationsByUser(HttpServletRequest request) {
        List<AppliedJobsDTO> dtos = applicationService.getAppliedJobsByUser(request);
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng > Cập nhật trạng thái đơn ứng tuyển",
            description = "Nhà tuyển dụng cập nhật trạng thái đơn ứng tuyển (VD: pending, accepted, rejected) thông qua applicationId."
    )
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Void> updateStatus(
            @Parameter(description = "ID của đơn ứng tuyển", example = "1") @PathVariable Integer applicationId,
            @Parameter(description = "Trạng thái mới: pending, accepted, rejected", example = "accepted")
            @RequestParam("status") String status) {
        applicationService.updateStatus(applicationId, status);
        return ResponseEntity.ok().build();
    }
}