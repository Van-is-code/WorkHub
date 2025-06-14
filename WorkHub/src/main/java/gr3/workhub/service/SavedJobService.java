package gr3.workhub.service;

import gr3.workhub.entity.SavedJob;
import gr3.workhub.entity.User;
import gr3.workhub.entity.Job;
import gr3.workhub.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;

    public SavedJob saveJob(Integer userId, Integer jobId) {
        SavedJob savedJob = new SavedJob();
        savedJob.setCandidate(new User(userId));
        savedJob.setJob(new Job(jobId));
        return savedJobRepository.save(savedJob);
    }

    public List<SavedJob> getSavedJobsForUser(Integer userId) {
        return savedJobRepository.findByCandidateId(userId);
    }
}