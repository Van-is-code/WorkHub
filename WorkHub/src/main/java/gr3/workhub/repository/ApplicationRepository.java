package gr3.workhub.repository;

import gr3.workhub.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    List<Application> findByJobId(Integer jobId);
    List<Application> findByCandidateId(Integer candidateId);
    boolean existsByJobIdAndCandidateId(Integer jobId, Integer candidateId);
}
