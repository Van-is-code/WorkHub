package gr3.workhub.controller;

import gr3.workhub.entity.Job;
import gr3.workhub.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "<EndPoint cho trang của ứng viên> Lấy tất cả công việc", description = "Trả về danh sách toàn bộ công việc đang đăng tuyển")

    @GetMapping()
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Lấy danh sách công việc của nhà tuyển dụng theo id của nhà tuyển dụng", description = "Trả về danh sách job do recruiter đăng")
    @PreAuthorize("hasRole('RECRUITER') ")
    @GetMapping("/{userId}")
    public ResponseEntity<List<Job>> getJobsByRecruiter(
            @Parameter(description = "ID của recruiter") @PathVariable Integer userId) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(userId));
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Tạo job mới theo id của nhà tuyển dụng ( userid )", description = "Recruiter hoặc Admin tạo job mới")
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/{userId}")
    public ResponseEntity<Job> createJob(
            @Parameter(description = "ID của người tạo (recruiter)") @PathVariable Integer userId,
            @RequestBody Job job) {
        return ResponseEntity.ok(jobService.createJobByUserId(userId, job));
    }

    @Operation(summary = "<EndPoint cho trang của ứng viên > Hiển thị tin theo postAt ( khu vực hiển thị tin )", description = "Trả về danh sách công việc theo postAt")
    @GetMapping("/postat/{postAt}")
    public ResponseEntity<List<Job>> getJobsByPostAt(
            @Parameter(description = "Vị trí hiển thị: proposal, urgent,standard") @PathVariable Job.PostAt postAt) {
        List<Job> jobs = jobService.getJobsByPostAt(postAt);
        return ResponseEntity.ok(jobs);
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Cập nhật công việc", description = "Cập nhật job đã đăng bởi recruiter hoặc admin")
    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<Job> updateJob(
            @Parameter(description = "ID của recruiter") @PathVariable Integer userId,
            @Parameter(description = "ID của công việc") @PathVariable Integer id,
            @RequestBody Job job) {
        Job updatedJob = jobService.updateJobByUserId(userId, id, job);
        return ResponseEntity.ok(updatedJob);
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Xóa công việc", description = "Xóa bài đăng công việc bởi recruiter hoặc admin")
    @PreAuthorize("hasRole('RECRUITER')")
    @DeleteMapping("/{userId}/{jobId}")
    public ResponseEntity<Void> deleteJobByUserId(
            @Parameter(description = "ID của recruiter") @PathVariable Integer userId,
            @Parameter(description = "ID của công việc") @PathVariable Integer jobId) {
        jobService.deleteJobByUserId(userId, jobId);
        return ResponseEntity.noContent().build();
    }
}
