package gr3.workhub.service;

import gr3.workhub.entity.JobPosition;
import gr3.workhub.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPositionService {

    private final JobPositionRepository jobPositionRepository;

    public JobPosition createJobPosition(JobPosition jobPosition) {
        return jobPositionRepository.save(jobPosition);
    }

    public JobPosition updateJobPosition(Integer id, JobPosition jobPosition) {
        JobPosition existing = jobPositionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosition not found"));
        existing.setName(jobPosition.getName());
        existing.setDescription(jobPosition.getDescription());
        return jobPositionRepository.save(existing);
    }

    public void deleteJobPosition(Integer id) {
        jobPositionRepository.deleteById(id);
    }

    public List<JobPosition> getAllJobPositions() {
        return jobPositionRepository.findAll();
    }

    public JobPosition getJobPositionById(Integer id) {
        return jobPositionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosition not found"));
    }
}