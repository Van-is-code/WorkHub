package gr3.workhub.repository;

import gr3.workhub.entity.InterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, java.util.UUID> {
    List<InterviewSlot> findByCandidateId(Integer candidateId);
    List<InterviewSlot> findByJobId(Integer jobId);
    void deleteByInterviewSession_Id(java.util.UUID sessionId);
    List<InterviewSlot> findByInterviewSession_Id(java.util.UUID sessionId);
    List<InterviewSlot> findByStartTime(java.time.LocalDateTime startTime);
}