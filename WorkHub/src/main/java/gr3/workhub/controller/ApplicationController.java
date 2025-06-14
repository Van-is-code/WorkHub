package gr3.workhub.controller;

import gr3.workhub.dto.ApplicationDTO;
import gr3.workhub.dto.AppliedJobsDTO;
import gr3.workhub.entity.Application;
import gr3.workhub.service.ApplicationService;
import gr3.workhub.service.ResumeService;
import gr3.workhub.service.UserBenefitsService;
import org.springframework.security.oauth2.jwt.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Operation(
            summary = "<EndPoint cho trang của ứng viên> Ứng tuyển công việc",
            description = "Ứng viên ứng tuyển vào công việc bằng cách gửi resume đã lưu. " +
                    "Cần truyền vào ID của công việc, ID của resume, và ID của ứng viên."
    )
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/{jobId}")
    public ResponseEntity<Application> applyForJob(
            @Parameter(description = "ID của công việc cần ứng tuyển", example = "3") @PathVariable Integer jobId,
            @Parameter(description = "ID của resume được chọn", example = "1") @RequestParam("resumeId") Integer resumeId,
            @Parameter(description = "ID của ứng viên", example = "6") @RequestParam("candidateId") Integer candidateId) {
        Application application = applicationService.applyForJob(jobId, resumeId, candidateId);
        return ResponseEntity.ok(application);
    }


    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/{jobId}/resumes")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobId(@PathVariable Integer jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer userId = jwt.getClaim("userId");

        List<ApplicationDTO> dtos = applicationService.getApplicationsByJobId(jobId, userId);
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

    //    không qua DTO
    //public Resume downloadResumeByResumeId(@PathVariable Integer resumeId) {
    //    return applicationService.getResumeForDownload(resumeId);
    //}

    @Operation(
            summary = "<EndPoint cho trang của ứng viên và nhà tuyển dụng >Tải xuống hồ sơ (resume) theo applicationId",
            description = "Cho phép tải file hồ sơ ứng viên dựa vào ID của đơn ứng tuyển."
    )
    @GetMapping("/{applicationId}/resume/download")
    public ResponseEntity<byte[]> downloadResumeByApplicationId(
            @Parameter(description = "ID của đơn ứng tuyển cần tải hồ sơ", example = "1") @PathVariable Integer applicationId) {
        ApplicationDTO dto = applicationService.getApplicationDTOForResumeDownloadByApplicationId(applicationId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.getUserFullname() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(dto.getResumeFile());
    }

    //    không qua DTO
    //public Resume downloadResumeByApplicationId(@PathVariable Integer applicationId) {
    //    return applicationService.getResumeForDownloadByApplicationId(applicationId);
    //}

    @Operation(
            summary = "<EndPoint cho trang của ứng viên > Lấy danh sách công việc đã ứng tuyển của ứng viên",
            description = "Ứng viên có thể xem lại toàn bộ các công việc mình đã nộp hồ sơ theo userId."
    )
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AppliedJobsDTO>> getApplicationsByUser(
            @Parameter(description = "ID của người dùng", example = "6") @PathVariable Integer userId) {
        List<AppliedJobsDTO> dtos = applicationService.getAppliedJobsByUser(userId);
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng > Cập nhật trạng thái đơn ứng tuyển",
            description = "Nhà tuyển dụng cập nhật trạng thái đơn ứng tuyển (VD: pending, accepted, rejected) thông qua applicationId."
    )
    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Void> updateStatus(
            @Parameter(description = "ID của đơn ứng tuyển", example = "1") @PathVariable Integer applicationId,
            @Parameter(description = "Trạng thái mới: pending, accepted, rejected", example = "accepted")
            @RequestParam("status") String status) {
        applicationService.updateStatus(applicationId, status);
        return ResponseEntity.ok().build();
    }
}
