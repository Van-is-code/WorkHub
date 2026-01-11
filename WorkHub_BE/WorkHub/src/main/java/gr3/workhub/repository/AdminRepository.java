package gr3.workhub.repository;

import gr3.workhub.entity.Admin;
import gr3.workhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByEmail(String email);
    List<Admin> findByRole(Admin.Role role);
}