package gr3.workhub.repository;

import gr3.workhub.entity.ServiceFeature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceFeatureRepository extends JpaRepository<ServiceFeature, Integer> {
}