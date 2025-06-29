package gr3.workhub.controller;

import gr3.workhub.dto.ApplicationDTO;
import gr3.workhub.dto.JobResponse;
import gr3.workhub.entity.Job;
import gr3.workhub.service.ApplicationService;
import gr3.workhub.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1/admin/jobs")
public class AdminJobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<JobResponse>> getAllJobsForAdmin() {
        List<JobResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{jobId}/applications")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobId(@PathVariable Integer jobId, HttpServletRequest request) {
        List<ApplicationDTO> applications = applicationService.getApplicationsByJobId(jobId, request);
        return ResponseEntity.ok(applications);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{jobId}/applications")
    public ResponseEntity<?> addUserToJob(@PathVariable Integer jobId, @RequestBody AddUserToJobRequest request) {
        applicationService.addUserToJob(jobId, request.getCandidateId(), request.getResumeId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{jobId}/applications/{applicationId}")
    public ResponseEntity<?> deleteUserFromJob(@PathVariable Integer jobId, @PathVariable Integer applicationId) {
        applicationService.deleteUserFromJob(jobId, applicationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<JobResponse> createJobByAdmin(@RequestBody Job job) {
        // Lấy recruiterId từ job (do FE gửi lên)
        if (job.getRecruiter() == null || job.getRecruiter().getId() == null) {
            throw new IllegalArgumentException("Recruiter is required");
        }
        Job savedJob = jobService.createJobByAdmin(job);
        return ResponseEntity.ok(new JobResponse(savedJob, null, null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJobByAdmin(@PathVariable Integer jobId) {
        jobService.deleteJobById(jobId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJobByAdmin(@PathVariable Integer jobId, @RequestBody Job job) {
        Job updatedJob = jobService.updateJobByAdmin(jobId, job);
        return ResponseEntity.ok(new JobResponse(updatedJob, null, null));
    }

    static class AddUserToJobRequest {
        private Integer candidateId;
        private Integer resumeId;

        public Integer getCandidateId() {
            return candidateId;
        }

        public Integer getResumeId() {
            return resumeId;
        }

        public void setCandidateId(Integer candidateId) {
            this.candidateId = candidateId;
        }

        public void setResumeId(Integer resumeId) {
            this.resumeId = resumeId;
        }
    }
}
