package gr3.workhub.controller;

import gr3.workhub.dto.InterviewSlotCreateDTO;
import gr3.workhub.dto.JobResponse;
import gr3.workhub.entity.Job;
import gr3.workhub.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "✅Job Management", description = "API quản lý bài đăng tuyển dụng (job)")
@RestController
@CrossOrigin
@RequestMapping("/workhub/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "<EndPoint cho trang của ứng viên> Lấy tất cả công việc", description = "Trả về danh sách toàn bộ công việc đang đăng tuyển, có thể lọc theo nhiều tiêu chí")
    @GetMapping()
    public ResponseEntity<List<JobResponse>> getAllJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) Integer positionId,
            @RequestParam(required = false) Integer skillId
    ) {
        List<JobResponse> jobs = jobService.getFilteredJobs(title, location, minSalary, maxSalary, categoryId, typeId, positionId, skillId);
        return ResponseEntity.ok(jobs);
    }


    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Lấy danh sách công việc của nhà tuyển dụng",
            description = "Trả về danh sách job do recruiter đăng. Lấy userId từ JWT token trong header Authorization"
    )
    @GetMapping("/recruiter")
    public ResponseEntity<List<JobResponse>> getJobsByRecruiter(HttpServletRequest request) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(request));
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Tạo job mới",
            description = "Recruiter tạo job mới. Lấy userId từ JWT token trong header Authorization"
    )
    @PostMapping
    public ResponseEntity<Job> createJob(
            HttpServletRequest request,
            @RequestBody Job job) {
        return ResponseEntity.ok(jobService.createJobByUserId(request, job, null));
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Tạo job mới kèm slot phỏng vấn",
            description = "Recruiter tạo job mới và các khung giờ phỏng vấn. Lấy userId từ JWT token trong header Authorization"
    )
    @PostMapping("/with-slots")
    public ResponseEntity<Job> createJobWithSlots(
            HttpServletRequest request,
            @RequestBody Job job,
            @RequestParam(required = false) List<InterviewSlotCreateDTO> slots) {
        return ResponseEntity.ok(jobService.createJobByUserId(request, job, slots));
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Hiển thị tin theo postAt ( khu vực hiển thị tin )",
            description = "Trả về danh sách công việc theo postAt"
    )
    @GetMapping("/postat/{postAt}")
    public ResponseEntity<List<JobResponse>> getJobsByPostAt(
            @Parameter(description = "Vị trí hiển thị: proposal, urgent,standard") @PathVariable Job.PostAt postAt) {
        List<JobResponse> jobs = jobService.getJobsByPostAt(postAt);
        return ResponseEntity.ok(jobs);
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Cập nhật công việc",
            description = "Cập nhật job đã đăng bởi recruiter hoặc admin. Lấy userId từ JWT token trong header Authorization"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(
            HttpServletRequest request,
            @Parameter(description = "ID của công việc") @PathVariable Integer id,
            @RequestBody Job job) {
        Job updatedJob = jobService.updateJobByUserId(request, id, job);
        return ResponseEntity.ok(updatedJob);
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Xóa công việc",
            description = "Xóa bài đăng công việc bởi recruiter hoặc admin. Lấy userId từ JWT token trong header Authorization"
    )
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJobByUserId(
            HttpServletRequest request,
            @Parameter(description = "ID của công việc") @PathVariable Integer jobId) {
        jobService.deleteJobByUserId(request, jobId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lấy chi tiết công việc theo id", description = "Trả về chi tiết một công việc theo id")
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Integer id) {
        JobResponse jobResponse = jobService.getJobById(id);
        return ResponseEntity.ok(jobResponse);
    }
}