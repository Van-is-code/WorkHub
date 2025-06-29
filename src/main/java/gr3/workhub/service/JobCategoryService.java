package gr3.workhub.service;

import gr3.workhub.entity.JobCategory;
import gr3.workhub.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCategoryService {

    private final JobCategoryRepository jobCategoryRepository;

    public JobCategory createJobCategory(JobCategory jobCategory) {
        return jobCategoryRepository.save(jobCategory);
    }

    public JobCategory updateJobCategory(Integer id, JobCategory jobCategory) {
        JobCategory existing = jobCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobCategory not found"));
        existing.setName(jobCategory.getName());
        existing.setDescription(jobCategory.getDescription());
        return jobCategoryRepository.save(existing);
    }

    public void deleteJobCategory(Integer id) {
        jobCategoryRepository.deleteById(id);
    }

    public List<JobCategory> getAllJobCategories() {
        return jobCategoryRepository.findAll();
    }

    public JobCategory getJobCategoryById(Integer id) {
        return jobCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobCategory not found"));
    }
}