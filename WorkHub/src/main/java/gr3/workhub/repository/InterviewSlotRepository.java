package gr3.workhub.repository;

import gr3.workhub.entity.InterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Integer> {
    List<InterviewSlot> findByCandidateId(Integer candidateId);
}