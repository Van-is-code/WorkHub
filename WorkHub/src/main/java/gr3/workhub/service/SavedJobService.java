package gr3.workhub.service;

import gr3.workhub.entity.SavedJob;
import gr3.workhub.entity.User;
import gr3.workhub.entity.Job;
import gr3.workhub.repository.SavedJobRepository;
import gr3.workhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final TokenService tokenService;



    public SavedJob saveJob(HttpServletRequest request, Integer jobId) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        SavedJob savedJob = new SavedJob();
        savedJob.setCandidate(new User(userId));
        savedJob.setJob(new Job(jobId));
        return savedJobRepository.save(savedJob);
    }

    public List<SavedJob> getSavedJobsForUser(HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        return savedJobRepository.findByCandidateId(userId);
    }
}