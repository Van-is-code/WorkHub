//package gr3.workhub.config;
//
//import gr3.workhub.entity.Admin;
//import gr3.workhub.entity.User;
//import gr3.workhub.repository.AdminRepository;
//import gr3.workhub.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class DataSeeder {
//
//    private final AdminRepository adminRepository;
//    private final UserRepository userRepository;
//
//    @Bean
//    public CommandLineRunner seedData() {
//        return args -> {
//            // Create fake admin account
//            if (adminRepository.findByEmail("admin@example.com").isEmpty()) {
//                Admin admin = new Admin();
//                admin.setFullname("Admin User");
//                admin.setEmail("admin@example.com");
//                admin.setPassword("{noop}admin123");
//                admin.setRole(Admin.Role.super_admin);
//                admin.setCreatedAt(java.time.LocalDateTime.parse("2023-10-01T00:00:00"));
//                admin.setActive(true);
//                adminRepository.save(admin);
//            }
//
//            // Create fake user accounts
//            if (userRepository.findByEmail("candidate@example.com").isEmpty()) {
//                User candidate = new User();
//                candidate.setFullname("Candidate User");
//                candidate.setEmail("candidate@example.com");
//                candidate.setPassword("{noop}can123");
//                candidate.setRole(User.Role.candidate);
//                candidate.setStatus(User.Status.verified);
//                candidate.setCreatedAt(java.time.LocalDateTime.parse("2023-10-01T00:00:00"));
//                userRepository.save(candidate);
//            }
//
//            if (userRepository.findByEmail("recruiter@example.com").isEmpty()) {
//                User recruiter = new User();
//                recruiter.setFullname("Recruiter User");
//                recruiter.setEmail("recruiter@example.com");
//                recruiter.setPassword("{noop}rec123");
//                recruiter.setRole(User.Role.recruiter);
//                recruiter.setStatus(User.Status.verified);
//                recruiter.setCreatedAt(java.time.LocalDateTime.parse("2023-10-01T00:00:00"));
//                userRepository.save(recruiter);
//            }
//        };
//    }
//}