package gr3.workhub.service;

import gr3.workhub.dto.ResumeReviewDTO;
import gr3.workhub.entity.Resume;
import gr3.workhub.entity.ResumeReview;
import gr3.workhub.entity.User;
import gr3.workhub.repository.ResumeRepository;
import gr3.workhub.repository.ResumeReviewRepository;
import gr3.workhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeReviewService {
    private final ResumeReviewRepository resumeReviewRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeReview createReview(ResumeReviewDTO dto) {
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        User candidate = userRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        User recruiter = userRepository.findById(dto.getRecruiterId())
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        ResumeReview review = new ResumeReview();
        review.setResume(resume);
        review.setCandidate(candidate);
        review.setRecruiter(recruiter);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return resumeReviewRepository.save(review);
    }

    public ResumeReview updateReview(Integer reviewId, ResumeReviewDTO dto) {
        ResumeReview review = resumeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return resumeReviewRepository.save(review);
    }

    public List<ResumeReview> getReviewsByCandidateId(Integer candidateId) {
        return resumeReviewRepository.findByCandidateId(candidateId);
    }

    public List<ResumeReview> getAllReviews() {
        return resumeReviewRepository.findAll();
    }
}