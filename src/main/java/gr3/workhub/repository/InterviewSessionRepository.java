package gr3.workhub.repository;

import gr3.workhub.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
    Optional<InterviewSession> findByTokenCandidate(UUID tokenCandidate);
    Optional<InterviewSession> findTopByRecruiter_IdOrderByCreatedAtDesc(Integer recruiterId);
    List<InterviewSession> findByRecruiter_Id(Integer recruiter);
    @Transactional
    @Modifying
    void deleteByJob_Id(Integer jobId);
}