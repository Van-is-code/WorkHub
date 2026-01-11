package gr3.workhub.repository;

import gr3.workhub.entity.ResumeReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeReviewRepository extends JpaRepository<ResumeReview, Integer> {
    List<ResumeReview> findByCandidateId(Integer candidateId);
}