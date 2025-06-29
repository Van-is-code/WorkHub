package gr3.workhub.controller;

import gr3.workhub.entity.JobType;
import gr3.workhub.service.JobTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin

@RestController
@RequestMapping("/workhub/api/v1/job-types")
@RequiredArgsConstructor
@Tag(name = "✅ Job Type", description = "Quản lý các loại công việc (job type) trong hệ thống")
public class JobTypeController {

    private final JobTypeService jobTypeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RECRUITER', 'recruiter')")
    @GetMapping
    public ResponseEntity<List<JobType>> getAllJobTypes() {
        return ResponseEntity.ok(jobTypeService.getAllJobTypes());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<JobType> getJobTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(jobTypeService.getJobTypeById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<JobType> createJobType(@RequestBody JobType jobType) {
        return ResponseEntity.ok(jobTypeService.createJobType(jobType));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<JobType> updateJobType(@PathVariable Integer id, @RequestBody JobType jobType) {
        return ResponseEntity.ok(jobTypeService.updateJobType(id, jobType));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobType(@PathVariable Integer id) {
        jobTypeService.deleteJobType(id);
        return ResponseEntity.noContent().build();
    }
}