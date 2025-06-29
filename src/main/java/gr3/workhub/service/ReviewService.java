package gr3.workhub.service;

import gr3.workhub.dto.ReviewDTO;
import gr3.workhub.entity.CompanyProfile;
import gr3.workhub.entity.Review;
import gr3.workhub.entity.User;
import gr3.workhub.repository.CompanyProfileRepository;
import gr3.workhub.repository.ReviewRepository;
import gr3.workhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;

    public void createReview(ReviewDTO dto) {
        CompanyProfile company = companyProfileRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Review review = new Review();
        review.setCompany(company);
        review.setUser(user);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        reviewRepository.save(review);
    }
}