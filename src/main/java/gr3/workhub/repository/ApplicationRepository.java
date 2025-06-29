package gr3.workhub.repository;

import gr3.workhub.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    List<Application> findByJobId(Integer jobId);
    List<Application> findByCandidateId(Integer candidateId);
    List<Application> findByResumeId(Integer resumeId);
    boolean existsByJobIdAndCandidateId(Integer jobId, Integer candidateId);
    void deleteByInterviewSlot_Id(java.util.UUID slotId);
    void deleteById(Integer id);
    @Transactional
    @Modifying
    void deleteByJobId(Integer jobId);
}
