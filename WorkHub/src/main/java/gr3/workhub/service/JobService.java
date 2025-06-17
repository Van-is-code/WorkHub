package gr3.workhub.service;

import gr3.workhub.dto.JobResponse;
import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import gr3.workhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;
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
    private final UserBenefitsRepository userBenefitsRepository;
    private final TokenService tokenService;
    private final CompanyProfileRepository companyProfileRepository;
    // Helper to initialize lazy relations
    private void initializeJobRelations(Job job) {
        Hibernate.initialize(job.getRecruiter());
        Hibernate.initialize(job.getCategory());
        Hibernate.initialize(job.getType());
        Hibernate.initialize(job.getPosition());
        Hibernate.initialize(job.getSkills());
    }

    // Map Job to JobResponse
    private JobResponse toJobResponse(Job job) {
        CompanyProfile cp = companyProfileRepository.findByRecruiter(job.getRecruiter());
        String companyName = cp != null ? cp.getName() : null;
        String companyLogo = (cp != null && cp.getLogo() != null)
                ? "data:image/png;base64," + Base64.getEncoder().encodeToString(cp.getLogo())
                : null;
        return new JobResponse(job, companyName, companyLogo);
    }

    public List<JobResponse> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        jobs.forEach(this::initializeJobRelations);
        return jobs.stream().map(this::toJobResponse).collect(Collectors.toList());
    }


    public List<JobResponse> getJobsByRecruiter(HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        List<Job> jobs = jobRepository.findJobsByCriteria(userId, null, null, null, null, null);
        jobs.forEach(this::initializeJobRelations);
        return jobs.stream().map(this::toJobResponse).collect(Collectors.toList());
    }

    public Job createJobByUserId(HttpServletRequest request, Job job) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        User recruiter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        Job.PostAt requestedPostAt = job.getPostAt() != null ? job.getPostAt() : Job.PostAt.standard;

        UserBenefits userBenefits = userBenefitsRepository
                .findByUserAndUserPackageAndPostAt(
                        recruiter,
                        null,
                        UserBenefits.PostAt.valueOf(requestedPostAt.name())
                )
                .orElseThrow(() -> new IllegalArgumentException("User benefits not found for this post type"));

        if (!canPostAt(userBenefits.getPostAt(), requestedPostAt)) {
            throw new IllegalArgumentException("You are not allowed to post at this level: " + requestedPostAt);
        }

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

    public Job updateJobByUserId(HttpServletRequest request, Integer jobId, Job job) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!existingJob.getRecruiter().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to update this job");
        }

        UserBenefits userBenefits = userBenefitsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User benefits not found"));

        Job.PostAt requestedPostAt = job.getPostAt() != null ? job.getPostAt() : Job.PostAt.standard;

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





    public List<JobResponse> getJobsByPostAt(Job.PostAt postAt) {
        List<Job> jobs = jobRepository.findByPostAt(postAt);
        jobs.forEach(this::initializeJobRelations);
        return jobs.stream().map(this::toJobResponse).collect(Collectors.toList());
    }

    public void deleteJobByUserId(HttpServletRequest request, Integer jobId) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!job.getRecruiter().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to delete this job");
        }

        jobRepository.delete(job);
    }

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