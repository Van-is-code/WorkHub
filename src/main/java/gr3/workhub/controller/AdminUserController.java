package gr3.workhub.controller;

import gr3.workhub.entity.User;
import gr3.workhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1/admin")
public class AdminUserController {
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/candidates")
    public ResponseEntity<List<User>> getAllCandidates() {
        List<User> candidates = userRepository.findByRole(User.Role.candidate);
        return ResponseEntity.ok(candidates);
    }
}
