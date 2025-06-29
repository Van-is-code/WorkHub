package gr3.workhub.service;

import gr3.workhub.entity.Admin;
import gr3.workhub.repository.AdminRepository;
import gr3.workhub.security.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    public String login(String email, String rawPassword) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtTokenGenerator.generateToken(admin.getEmail(), admin.getRole().name(), admin.getId());
    }

    public Admin createAdmin(String fullname, String email, String password, String phone, Admin.Role role) {
        if (adminRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        Admin admin = new Admin();
        admin.setFullname(fullname);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setPhone(phone);
        admin.setRole(role);
        admin.setActive(true); // Verified immediately
        admin.setCreatedAt(java.time.LocalDateTime.now());
        return adminRepository.save(admin);
    }

    public Admin getAdminById(Integer id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public List<Admin> getAdminsByRole(Admin.Role role) {
        return adminRepository.findByRole(role);
    }

    public Admin updateAdmin(Integer id, String fullname, String phone) {
        Admin admin = getAdminById(id);
        admin.setFullname(fullname);
        admin.setPhone(phone);
        return adminRepository.save(admin);
    }

    public void deleteAdmin(Integer id) {
        adminRepository.deleteById(id);
    }
}