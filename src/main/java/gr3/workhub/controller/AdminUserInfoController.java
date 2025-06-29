package gr3.workhub.controller;

import gr3.workhub.entity.Resume;
import gr3.workhub.entity.User;
import gr3.workhub.entity.Job;
import gr3.workhub.dto.AppliedJobsDTO;
import gr3.workhub.repository.ResumeRepository;
import gr3.workhub.repository.UserRepository;
import gr3.workhub.repository.JobRepository;
import gr3.workhub.repository.SavedJobRepository;
import gr3.workhub.entity.SavedJob;
import gr3.workhub.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1")
public class AdminUserInfoController {
    @Autowired private ResumeRepository resumeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private ApplicationService applicationService;
    @Autowired private SavedJobRepository savedJobRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/resumes/{userId}")
    public ResponseEntity<List<Resume>> getResumesByUser(@PathVariable Integer userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        return ResponseEntity.ok(resumes);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/saved-jobs/{userId}")
    public ResponseEntity<List<Job>> getSavedJobsByUser(@PathVariable Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        // Giả sử User có quan hệ savedJobs hoặc bạn có repository phù hợp
        // Nếu không, cần bổ sung repository để lấy danh sách job đã lưu theo userId
        return ResponseEntity.ok(List.of()); // TODO: thay bằng logic thực tế
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/applications/{userId}")
    public ResponseEntity<List<AppliedJobsDTO>> getApplicationsByUser(@PathVariable Integer userId) {
        List<AppliedJobsDTO> apps = applicationService.getAppliedJobsByUserId(userId);
        return ResponseEntity.ok(apps);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/resumes/user/{userId}")
    public ResponseEntity<List<Resume>> getResumesByUserDirect(@PathVariable Integer userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        return ResponseEntity.ok(resumes);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER') or hasRole('recruiter') or hasRole('USER') or hasRole('user')")
    @GetMapping("/applications/user/{userId}")
    public ResponseEntity<List<AppliedJobsDTO>> getApplicationsByUserDirect(@PathVariable Integer userId) {
        List<AppliedJobsDTO> apps = applicationService.getAppliedJobsByUserId(userId);
        return ResponseEntity.ok(apps);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/saved-jobs/user/{userId}")
    public ResponseEntity<List<Job>> getSavedJobsByUserDirect(@PathVariable Integer userId) {
        List<SavedJob> savedJobs = savedJobRepository.findByCandidateId(userId);
        List<Job> jobs = savedJobs.stream().map(SavedJob::getJob).toList();
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable Integer userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}
