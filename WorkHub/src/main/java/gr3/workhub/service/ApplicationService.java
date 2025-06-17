package gr3.workhub.service;

import gr3.workhub.dto.ApplicationDTO;
import gr3.workhub.dto.AppliedJobsDTO;
import gr3.workhub.entity.*;
import gr3.workhub.repository.ApplicationRepository;
import gr3.workhub.repository.JobRepository;
import gr3.workhub.repository.ResumeRepository;
import gr3.workhub.repository.UserRepository;
import gr3.workhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;



@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final UserBenefitsService userBenefitsService;
    private final TokenService tokenService   ;


    // Apply for a job
    public Application applyForJob(Integer jobId, Integer resumeId, HttpServletRequest request) {

        Integer candidateId = tokenService.extractUserIdFromRequest(request);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));

        Application application = new Application();
        application.setJob(job);
        application.setResume(resume);
        application.setCandidate(candidate);

        return applicationRepository.save(application);
    }

    // Get applications for a job (for recruiter)
    public List<ApplicationDTO> getApplicationsByJobId(Integer jobId, HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        List<Application> applications = applicationRepository.findByJobId(jobId);

        UserBenefits userBenefits = userBenefitsService.findByUserId(userId);
        Integer cvLimit = userBenefits != null ? userBenefits.getCvLimit() : null;

        List<ApplicationDTO> dtos = applications.stream()
                .map(app -> new ApplicationDTO(
                        app.getJob().getTitle(),
                        app.getResume().getUser().getFullname(),
                        app.getResume().getUser().getEmail(),
                        app.getResume().getUser().getPhone(),
                        app.getStatus().name(),
                        app.getAppliedAt(),
                        app.getResume().getFile()
                ))
                .toList();

        if (cvLimit != null && cvLimit > 0 && dtos.size() > cvLimit) {
            return dtos.subList(0, cvLimit);
        }
        return dtos;
    }

    // Get jobs applied by the current candidate
    public List<AppliedJobsDTO> getAppliedJobsByUser(HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        List<Application> applications = applicationRepository.findByCandidateId(userId);
        return applications.stream()
                .map(app -> new AppliedJobsDTO(
                        app.getJob().getTitle(),
                        app.getJob().getSalaryRange(),
                        app.getResume().getFile(),
                        app.getStatus().name(),
                        app.getAppliedAt()
                ))
                .toList();
    }

    // Update application status (for recruiter)
    public void updateStatus(Integer applicationId, String status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        try {
            app.setStatus(Application.Status.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value");
        }
        applicationRepository.save(app);
    }

    // Download resume by resumeId
    public ApplicationDTO getApplicationDTOForResumeDownload(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        String fullName = resume.getUser().getFullname();
        return new ApplicationDTO(
                null, // jobTitle not needed for download
                fullName,
                resume.getUser().getEmail(),
                resume.getUser().getPhone(),
                null, // status not needed for download
                null, // appliedAt not needed for download
                resume.getFile()
        );
    }

    // Download resume by applicationId
    public ApplicationDTO getApplicationDTOForResumeDownloadByApplicationId(Integer applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));
        Resume resume = app.getResume();
        String fullName = resume.getUser().getFullname();
        return new ApplicationDTO(
                app.getJob().getTitle(),
                fullName,
                resume.getUser().getEmail(),
                resume.getUser().getPhone(),
                app.getStatus().name(),
                app.getAppliedAt(),
                resume.getFile()
        );
    }


}