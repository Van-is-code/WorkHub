package gr3.workhub.repository;

import gr3.workhub.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, Integer> {
}