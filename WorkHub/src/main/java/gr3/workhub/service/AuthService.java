package gr3.workhub.service;

import gr3.workhub.entity.User;
import gr3.workhub.entity.Admin;
import gr3.workhub.repository.UserRepository;
import gr3.workhub.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<?> login(String email, String rawPassword, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return adminRepository.findByEmail(email)
                    .filter(admin -> passwordEncoder.matches(rawPassword, admin.getPassword()));
        } else if ("USER".equalsIgnoreCase(role)) {
            return userRepository.findByEmail(email)
                    .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
        }
        return Optional.empty();
    }
}