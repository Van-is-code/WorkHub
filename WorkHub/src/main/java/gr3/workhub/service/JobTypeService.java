package gr3.workhub.service;

import gr3.workhub.entity.JobType;
import gr3.workhub.repository.JobTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobTypeService {

    private final JobTypeRepository jobTypeRepository;

    public JobType createJobType(JobType jobType) {
        return jobTypeRepository.save(jobType);
    }

    public JobType updateJobType(Integer id, JobType jobType) {
        JobType existing = jobTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobType not found"));
        existing.setName(jobType.getName());
        existing.setDescription(jobType.getDescription());
        return jobTypeRepository.save(existing);
    }

    public void deleteJobType(Integer id) {
        jobTypeRepository.deleteById(id);
    }

    public List<JobType> getAllJobTypes() {
        return jobTypeRepository.findAll();
    }

    public JobType getJobTypeById(Integer id) {
        return jobTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobType not found"));
    }
}