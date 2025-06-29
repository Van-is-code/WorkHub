package gr3.workhub.repository;

import gr3.workhub.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SavedJobRepository extends JpaRepository<SavedJob, Integer> {
    List<SavedJob> findByCandidateId(Integer candidateId);
    SavedJob findByCandidateIdAndJobId(Integer candidateId, Integer jobId);
    @Transactional
    @Modifying
    void deleteByJobId(Integer jobId);
}