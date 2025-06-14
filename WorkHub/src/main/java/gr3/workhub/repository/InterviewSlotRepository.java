package gr3.workhub.repository;

import gr3.workhub.entity.InterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, UUID> {
    List<InterviewSlot> findByInterviewSessionId(UUID sessionId);
}