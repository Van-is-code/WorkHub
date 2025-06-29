package gr3.workhub.controller;

import gr3.workhub.entity.ResumeView;
import gr3.workhub.repository.ResumeViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workhub/api/v1/resume-views")
public class ResumeViewController {
    @Autowired
    private ResumeViewRepository repository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<ResumeView>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResumeView> getById(@PathVariable Integer id) {
        Optional<ResumeView> item = repository.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ResumeView> create(@RequestBody ResumeView item) {
        return ResponseEntity.ok(repository.save(item));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResumeView> update(@PathVariable Integer id, @RequestBody ResumeView details) {
        Optional<ResumeView> optional = repository.findById(id);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();
        ResumeView item = optional.get();
        // ...cập nhật các trường cần thiết...
        return ResponseEntity.ok(repository.save(item));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/by-recruiter/{recruiterId}")
    public ResponseEntity<List<Integer>> getViewedResumeIdsByRecruiter(@PathVariable Integer recruiterId) {
        List<ResumeView> views = repository.findAll();
        List<Integer> resumeIds = views.stream()
            .filter(v -> v.getRecruiter().getId().equals(recruiterId))
            .map(v -> v.getResume().getId())
            .distinct()
            .toList();
        return ResponseEntity.ok(resumeIds);
    }
}
