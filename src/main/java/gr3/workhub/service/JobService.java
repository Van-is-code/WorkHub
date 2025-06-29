package gr3.workhub.service;

import gr3.workhub.dto.JobResponse;
import gr3.workhub.dto.InterviewSlotCreateDTO;
import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import gr3.workhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

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
    private final SavedJobRepository savedJobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final JobSpecification jobSpecification;
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
        List<CompanyProfile> cpList = companyProfileRepository.findByRecruiter(job.getRecruiter());
        CompanyProfile cp = cpList.isEmpty() ? null : cpList.get(0);
        String companyName = cp != null ? cp.getName() : null;
        String companyLogo = (cp != null && cp.getLogoUrl() != null && !cp.getLogoUrl().isEmpty())
                ? cp.getLogoUrl()
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

    public Job createJobByUserId(HttpServletRequest request, Job job, List<InterviewSlotCreateDTO> slots) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        String userRole = tokenService.extractUserRoleFromRequest(request);
        User recruiter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        // Chỉ recruiter mới kiểm tra user_benefits và job_post_limit
        if (!"admin".equalsIgnoreCase(userRole)) {
            // Lấy tất cả userBenefits của recruiter
            List<UserBenefits> allBenefits = userBenefitsRepository.findAll();
            UserBenefits matched = allBenefits.stream()
                    .filter(ub -> ub.getUser().getId().equals(recruiter.getId())
                            && (ub.getJobPostLimit() != null && ub.getJobPostLimit() > 0)
                            && (ub.getUserPackage() != null && ub.getUserPackage().getExpirationDate() != null && ub.getUserPackage().getExpirationDate().isAfter(java.time.LocalDateTime.now()))
                    )
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Bạn không còn quota đăng tin, vui lòng mua thêm gói dịch vụ."));

            matched.setJobPostLimit(matched.getJobPostLimit() - 1);
            userBenefitsRepository.save(matched);
        }

        JobCategory category = jobCategoryRepository.findById(job.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        JobType type = jobTypeRepository.findById(job.getType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Type not found"));
        JobPosition position = jobPositionRepository.findById(job.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found"));
        job.setRecruiter(recruiter);
        job.setCategory(category);
        job.setType(type);
        job.setPosition(position);
        job.setCreatedAt(java.time.LocalDateTime.now());
        Job savedJob = jobRepository.save(job);
        // Tạo các slot phỏng vấn nếu có
        if (slots != null) {
            for (InterviewSlotCreateDTO slotDto : slots) {
                InterviewSlot slot = new InterviewSlot();
                slot.setJob(savedJob);
                slot.setStartTime(slotDto.getStartTime());
                slot.setCreatedAt(java.time.LocalDateTime.now());
                // Có thể bổ sung endTime, trạng thái, ...
                savedJob.getInterviewSlots().add(slot);
            }
            jobRepository.save(savedJob);
        }
        return savedJob;
    }

    public Job updateJobByUserId(HttpServletRequest request, Integer jobId, Job job) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        String userRole = tokenService.extractUserRoleFromRequest(request);
        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Cho phép admin toàn quyền cập nhật job
        if (!"admin".equalsIgnoreCase(userRole) && !existingJob.getRecruiter().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to update this job");
        }

        // Chỉ recruiter mới cần kiểm tra user_benefits và postAt
        if (!"admin".equalsIgnoreCase(userRole)) {
            UserBenefits userBenefits = userBenefitsRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User benefits not found"));

            Job.PostAt requestedPostAt = job.getPostAt() != null ? job.getPostAt() : Job.PostAt.standard;

            if (!canPostAt(userBenefits.getPostAt(), requestedPostAt)) {
                throw new IllegalArgumentException("You are not allowed to post at this level: " + requestedPostAt);
            }
        }

        Job.PostAt requestedPostAt = job.getPostAt() != null ? job.getPostAt() : Job.PostAt.standard;

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
        // Gọi logic xóa đầy đủ
        deleteJobById(jobId);
    }

    public void deleteJobById(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        User recruiter = job.getRecruiter();

        // Xóa saved_jobs
        savedJobRepository.deleteByJobId(jobId);
        // Lấy tất cả slot liên quan đến job
        List<InterviewSlot> slots = interviewSlotRepository.findByJobId(jobId);
        // Xóa tất cả application liên quan đến job (bao gồm cả application có slot liên quan)
        applicationRepository.deleteByJobId(jobId);
        for (InterviewSlot slot : slots) {
            applicationRepository.deleteByInterviewSlot_Id(slot.getId());
        }
        // Xóa interview slot theo jobId
        for (InterviewSlot slot : slots) {
            interviewSlotRepository.deleteById(slot.getId());
        }
        // Xóa interview session theo jobId
        interviewSessionRepository.deleteByJob_Id(jobId);
        // Xóa job
        jobRepository.deleteById(jobId);

        // Cộng lại jobPostLimit cho recruiter (nếu không phải admin)
        if (recruiter != null && recruiter.getRole() != null && recruiter.getRole().name().equalsIgnoreCase("recruiter")) {
            // Tìm benefit còn hạn/quota nhỏ nhất (ưu tiên benefit quota thấp nhất)
            List<UserBenefits> allBenefits = userBenefitsRepository.findAll();
            UserBenefits benefit = allBenefits.stream()
                    .filter(ub -> ub.getUser().getId().equals(recruiter.getId())
                            && (ub.getUserPackage() != null && ub.getUserPackage().getExpirationDate() != null && ub.getUserPackage().getExpirationDate().isAfter(java.time.LocalDateTime.now()))
                    )
                    .sorted((a, b) -> Integer.compare(a.getJobPostLimit() != null ? a.getJobPostLimit() : 0, b.getJobPostLimit() != null ? b.getJobPostLimit() : 0))
                    .findFirst().orElse(null);
            if (benefit != null) {
                benefit.setJobPostLimit((benefit.getJobPostLimit() != null ? benefit.getJobPostLimit() : 0) + 1);
                userBenefitsRepository.save(benefit);
            }
        }
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

    public List<JobResponse> searchJobs(String query) {
        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCase(query);
        jobs.forEach(this::initializeJobRelations);
        return jobs.stream().map(this::toJobResponse).collect(Collectors.toList());
    }

    public List<JobResponse> getFilteredJobs(
            String title, String location, Long minSalary, Long maxSalary,
            Integer categoryId, Integer typeId, Integer positionId, Integer skillId) {

        Specification<Job> spec = jobSpecification.getJobs(title, location, null, null, categoryId, typeId, positionId, skillId);
        List<Job> jobs = jobRepository.findAll(spec);
        jobs.forEach(this::initializeJobRelations);

        // Lọc lương bằng Java
        List<Job> filtered = jobs.stream().filter(job -> {
            if (job.getSalaryRange() == null || job.getSalaryRange().isEmpty()) return false;
            String salaryStr = job.getSalaryRange().replaceAll("[^0-9\\-]", ""); // "1000-1500"
            String[] parts = salaryStr.split("-");
            long min = parts.length > 0 ? Long.parseLong(parts[0]) : 0;
            long max = parts.length > 1 ? Long.parseLong(parts[1]) : min;
            if (minSalary != null && max < minSalary) return false;
            if (maxSalary != null && min > maxSalary) return false;
            return true;
        }).collect(Collectors.toList());

        return filtered.stream().map(this::toJobResponse).collect(Collectors.toList());
    }


    public JobResponse getJobById(Integer id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        initializeJobRelations(job);
        return toJobResponse(job);
    }

    public Job createJobByAdmin(Job job) {
        // Lấy recruiter từ id được gửi lên
        User recruiter = userRepository.findById(job.getRecruiter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));
        JobCategory category = jobCategoryRepository.findById(job.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        JobType type = jobTypeRepository.findById(job.getType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Type not found"));
        JobPosition position = jobPositionRepository.findById(job.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found"));
        List<Skill> skills = job.getSkills() != null ? job.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList()) : null;
        job.setRecruiter(recruiter);
        job.setCategory(category);
        job.setType(type);
        job.setPosition(position);
        job.setSkills(skills);
        return jobRepository.save(job);
    }

    public Job updateJobByAdmin(Integer jobId, Job job) {
        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        User recruiter = userRepository.findById(job.getRecruiter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));
        JobCategory category = jobCategoryRepository.findById(job.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        JobType type = jobTypeRepository.findById(job.getType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Type not found"));
        JobPosition position = jobPositionRepository.findById(job.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found"));
        List<Skill> skills = job.getSkills() != null ? job.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList()) : null;
        existingJob.setTitle(job.getTitle());
        existingJob.setDescription(job.getDescription());
        existingJob.setSalaryRange(job.getSalaryRange());
        existingJob.setExperience(job.getExperience());
        existingJob.setLocation(job.getLocation());
        existingJob.setRecruiter(recruiter);
        existingJob.setCategory(category);
        existingJob.setType(type);
        existingJob.setPosition(position);
        existingJob.setSkills(skills);
        return jobRepository.save(existingJob);
    }

    public Job closeJobByUserId(HttpServletRequest request, Integer jobId) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        String userRole = tokenService.extractUserRoleFromRequest(request);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if (!"admin".equalsIgnoreCase(userRole) && !job.getRecruiter().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to close this job");
        }
        job.setStatus("closed");
        return jobRepository.save(job);
    }
}