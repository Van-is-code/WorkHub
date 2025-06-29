package gr3.workhub.repository;

import gr3.workhub.entity.JobType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JobTypeRepository extends JpaRepository<JobType, Integer> {
}