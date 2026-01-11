package gr3.workhub.controller;

import gr3.workhub.entity.JobPosition;
import gr3.workhub.service.JobPositionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin

@RestController
@RequestMapping("/workhub/api/v1/job-positions")
@RequiredArgsConstructor
@Tag(name = "✅ Job Position", description = "Quản lý các vị trí công việc (job position) trong hệ thống.")
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RECRUITER', 'recruiter')")
    @GetMapping
    public ResponseEntity<List<JobPosition>> getAllJobPositions() {
        return ResponseEntity.ok(jobPositionService.getAllJobPositions());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<JobPosition> getJobPositionById(@PathVariable Integer id) {
        return ResponseEntity.ok(jobPositionService.getJobPositionById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<JobPosition> createJobPosition(@RequestBody JobPosition jobPosition) {
        return ResponseEntity.ok(jobPositionService.createJobPosition(jobPosition));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<JobPosition> updateJobPosition(@PathVariable Integer id, @RequestBody JobPosition jobPosition) {
        return ResponseEntity.ok(jobPositionService.updateJobPosition(id, jobPosition));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosition(@PathVariable Integer id) {
        jobPositionService.deleteJobPosition(id);
        return ResponseEntity.noContent().build();
    }
}