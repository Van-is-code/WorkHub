package gr3.workhub.service;

import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobTypeRepository jobTypeRepository;
    private final JobPositionRepository jobPositionRepository;
    private final SkillRepository skillRepository;
    private final UserBenefitsRepository userBenefitsRepository; // Add this

    public List<Job> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        jobs.forEach(job -> {
            Hibernate.initialize(job.getRecruiter());
            Hibernate.initialize(job.getCategory());
            Hibernate.initialize(job.getType());
            Hibernate.initialize(job.getPosition());
            Hibernate.initialize(job.getSkills());
        });
        return jobs;
    }



    public List<Job> getJobsByRecruiter(Integer userId) {
        return jobRepository.findJobsByCriteria(userId, null, null, null, null, null);
    }

    public Job createJobByUserId(Integer userId, Job job) {
        User recruiter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        Job.PostAt requestedPostAt = job.getPostAt() != null ? job.getPostAt() : Job.PostAt.standard;

        // Fetch UserBenefits for the correct postAt
        UserBenefits userBenefits = userBenefitsRepository
                .findByUserAndUserPackageAndPostAt(
                        recruiter,
                        null, // If you have current UserPackage, pass it here; else adjust repository method
                        UserBenefits.PostAt.valueOf(requestedPostAt.name())
                )
                .orElseThrow(() -> new IllegalArgumentException("User benefits not found for this post type"));

        // Validate permission
        if (!canPostAt(userBenefits.getPostAt(), requestedPostAt)) {
            throw new IllegalArgumentException("You are not allowed to post at this level: " + requestedPostAt);
        }

        // Check and decrement jobPostLimit
        if (userBenefits.getJobPostLimit() == null || userBenefits.getJobPostLimit() <= 0) {
            throw new IllegalArgumentException("You have reached your job post limit.");
        }
        userBenefits.setJobPostLimit(userBenefits.getJobPostLimit() - 1);
        userBenefitsRepository.save(userBenefits);

        JobCategory category = jobCategoryRepository.findById(job.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        JobType type = jobTypeRepository.findById(job.getType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Type not found"));

        JobPosition position = jobPositionRepository.findById(job.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found"));

        List<Skill> skills = job.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList());

        job.setRecruiter(recruiter);
        job.setCategory(category);
        job.setType(type);
        job.setPosition(position);
        job.setSkills(skills);
        job.setPostAt(requestedPostAt);

        return jobRepository.save(job);
    }

    public Job updateJobByUserId(Integer userId, Integer jobId, Job job) {
        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!existingJob.getRecruiter().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to update this job");
        }

        // Fetch UserBenefits for permission check
        UserBenefits userBenefits = userBenefitsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User benefits not found"));

        Job.PostAt requestedPostAt = job.getPostAt() != null ? job.getPostAt() : Job.PostAt.standard;

        // Validate permission
        if (!canPostAt(userBenefits.getPostAt(), requestedPostAt)) {
            throw new IllegalArgumentException("You are not allowed to post at this level: " + requestedPostAt);
        }

        JobCategory category = jobCategoryRepository.findById(job.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        JobType type = jobTypeRepository.findById(job.getType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Type not found"));

        JobPosition position = jobPositionRepository.findById(job.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found"));

        List<Skill> skills = job.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList());

        existingJob.setTitle(job.getTitle());
        existingJob.setDescription(job.getDescription());
        existingJob.setCategory(category);
        existingJob.setType(type);
        existingJob.setPosition(position);
        existingJob.setSalaryRange(job.getSalaryRange());
        existingJob.setSkills(skills);
        existingJob.setPostAt(requestedPostAt);

        return jobRepository.save(existingJob);
    }

    public List<Job> getJobsByPostAt(Job.PostAt postAt) {
        List<Job> jobs = jobRepository.findByPostAt(postAt);
        jobs.forEach(job -> {
            Hibernate.initialize(job.getRecruiter());
            Hibernate.initialize(job.getCategory());
            Hibernate.initialize(job.getType());
            Hibernate.initialize(job.getPosition());
            Hibernate.initialize(job.getSkills());
        });
        return jobs;
    }

    public void deleteJobByUserId(Integer userId, Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!job.getRecruiter().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to delete this job");
        }

        jobRepository.delete(job);
    }

    // Helper for permission check
    private boolean canPostAt(UserBenefits.PostAt allowed, Job.PostAt requested) {
        switch (allowed) {
            case proposal:
                return true;
            case urgent:
                return requested == Job.PostAt.urgent || requested == Job.PostAt.standard;
            case standard:
                return requested == Job.PostAt.standard;
            default:
                return false;
        }
    }
}