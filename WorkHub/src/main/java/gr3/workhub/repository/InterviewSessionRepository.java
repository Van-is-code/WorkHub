package gr3.workhub.repository;

import gr3.workhub.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
    Optional<InterviewSession> findByTokenCandidate(UUID tokenCandidate);
    List<InterviewSession> findByRecruiter_Id(Integer recruiter);
}