package gr3.workhub.repository;

import gr3.workhub.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;



public interface JobPositionRepository extends JpaRepository<JobPosition, Integer> {
}
