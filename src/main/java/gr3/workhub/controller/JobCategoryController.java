package gr3.workhub.controller;

import gr3.workhub.entity.JobCategory;
import gr3.workhub.service.JobCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/workhub/api/v1/job-categories")
@RequiredArgsConstructor
@Tag(name = "✅ Job Category", description = "Quản lý các danh mục công việc (job categories) cho hệ thống")
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RECRUITER', 'recruiter')")
    @GetMapping
    public ResponseEntity<List<JobCategory>> getAllJobCategories() {
        return ResponseEntity.ok(jobCategoryService.getAllJobCategories());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<JobCategory> getJobCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(jobCategoryService.getJobCategoryById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<JobCategory> createJobCategory(@RequestBody JobCategory jobCategory) {
        return ResponseEntity.ok(jobCategoryService.createJobCategory(jobCategory));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<JobCategory> updateJobCategory(@PathVariable Integer id, @RequestBody JobCategory jobCategory) {
        return ResponseEntity.ok(jobCategoryService.updateJobCategory(id, jobCategory));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobCategory(@PathVariable Integer id) {
        jobCategoryService.deleteJobCategory(id);
        return ResponseEntity.noContent().build();
    }
}