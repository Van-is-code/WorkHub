// src/main/java/gr3/workhub/repository/CompanyProfileRepository.java
package gr3.workhub.repository;

import gr3.workhub.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Integer> {
}