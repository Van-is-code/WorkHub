package gr3.workhub.controller;

import gr3.workhub.entity.UserBenefits;
import gr3.workhub.repository.UserBenefitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workhub/api/v1/user-benefits")
public class UserBenefitsController {
    @Autowired
    private UserBenefitsRepository repository;

    @PreAuthorize("hasRole('admin') or hasRole('super_admin')")
    @GetMapping("")
    public ResponseEntity<List<UserBenefits>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PreAuthorize("hasRole('admin') or hasRole('super_admin')")
    @GetMapping("/{id}")
    public ResponseEntity<UserBenefits> getById(@PathVariable Integer id) {
        Optional<UserBenefits> item = repository.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('super_admin')")
    @PostMapping("")
    public ResponseEntity<UserBenefits> create(@RequestBody UserBenefits item) {
        return ResponseEntity.ok(repository.save(item));
    }

    @PreAuthorize("hasRole('super_admin')")
    @PutMapping("/{id}")
    public ResponseEntity<UserBenefits> update(@PathVariable Integer id, @RequestBody UserBenefits details) {
        Optional<UserBenefits> optional = repository.findById(id);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();
        UserBenefits item = optional.get();
        // ...cập nhật các trường cần thiết...
        return ResponseEntity.ok(repository.save(item));
    }

    @PreAuthorize("hasRole('super_admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('admin') or hasRole('super_admin') or hasRole('recruiter') or hasRole('RECRUITER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBenefits>> getByUserId(@PathVariable Integer userId) {
        List<UserBenefits> list = repository.findAll().stream()
            .filter(b -> b.getUser() != null && b.getUser().getId().equals(userId))
            .toList();
        return ResponseEntity.ok(list);
    }
}
