// src/main/java/gr3/workhub/repository/CompanyProfileRepository.java
package gr3.workhub.repository;

import gr3.workhub.entity.CompanyProfile;
import gr3.workhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Integer> {
    List<CompanyProfile> findByRecruiter(User recruiter);
    List<CompanyProfile> findByNameContainingIgnoreCase(String name);
}