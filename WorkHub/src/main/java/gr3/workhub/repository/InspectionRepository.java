package gr3.workhub.repository;

import gr3.workhub.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Integer> {
    List<Inspection> findBySenderId(Integer userId);
    List<Inspection> findByInspectionStatus(Inspection.InspectionStatus status);
}