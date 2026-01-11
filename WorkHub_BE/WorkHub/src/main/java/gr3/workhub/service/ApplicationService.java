package gr3.workhub.service;

import gr3.workhub.dto.ApplicationDTO;
import gr3.workhub.dto.AppliedJobsDTO;
import gr3.workhub.dto.SimpleJobDTO;
import gr3.workhub.entity.*;
import gr3.workhub.repository.ApplicationRepository;
import gr3.workhub.repository.JobRepository;
import gr3.workhub.repository.ResumeRepository;
import gr3.workhub.repository.UserRepository;
import gr3.workhub.repository.InterviewSlotRepository;
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
    private final InterviewSlotRepository interviewSlotRepository;
    private final EmailService emailService;


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

    // Apply for a job with interview slot
    public Application applyForJobWithSlot(Integer jobId, Integer resumeId, String slotId, HttpServletRequest request) {
        Integer candidateId = tokenService.extractUserIdFromRequest(request);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
        InterviewSlot slot = interviewSlotRepository.findById(java.util.UUID.fromString(slotId))
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        if (slot.getCandidate() != null) {
            throw new IllegalArgumentException("Slot already booked");
        }
        slot.setCandidate(candidate);
        interviewSlotRepository.save(slot);
        Application application = new Application();
        application.setJob(job);
        application.setResume(resume);
        application.setCandidate(candidate);
        application.setInterviewSlot(slot); // Gán slot vào application
        applicationRepository.save(application);

        // Gửi mail cho candidate với join link 100ms
        InterviewSession session = slot.getInterviewSession();
        if (session != null) {
            String codeCandidate = session.getCodeCandidate();
            String joinLink = codeCandidate != null && !codeCandidate.isEmpty()
                    ? "https://workhub.app.100ms.live/preview/" + codeCandidate
                    : "#";
            String body = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                    "<link href='https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css' rel='stylesheet'>" +
                    "</head>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 40px;'>" +
                    "<div class='container' style='max-width: 600px; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                    "<h1 class='text-center' style='color: #1f41ce; font-weight: bold; margin-bottom: 30px;'>WORKHUB</h1>" +
                    "<h4 class='mb-4'>Interview Invitation</h4>" +
                    "<p>Dear <strong>" + candidate.getFullname() + "</strong>,</p>" +
                    "<p>Your interview for the job position <strong>" + job.getTitle() + "</strong> is scheduled at <strong>" +
                    (session.getStartTime() != null ? session.getStartTime().toString() : "") + "</strong>.</p>" +
                    "<p>You can join the interview by clicking the button below. The link will be active 15 minutes before the scheduled time:</p>" +
                    "<div class='text-center' style='margin: 30px 0;'>" +
                    "<a href='" + joinLink + "' class='btn btn-primary btn-lg' style='background-color: #1f41ce; border: none;'>Join Interview</a>" +
                    "</div>" +
                    "<p>If you have any questions, feel free to reply to this email.</p>" +
                    "<p style='margin-top: 30px;'>Best regards,<br/>The WorkHub Team</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            try {
                org.slf4j.LoggerFactory.getLogger(ApplicationService.class)
                    .info("[DEBUG] Sending interview email to candidate: {} | email: {} | joinLink: {} | codeCandidate: {}", candidate.getFullname(), candidate.getEmail(), joinLink, codeCandidate);
                emailService.sendinterview(candidate.getEmail(), "Interview Schedule", body);
            } catch (Exception e) {
                org.slf4j.LoggerFactory.getLogger(ApplicationService.class)
                    .error("Failed to send interview email to candidate: {} (email: {}), error: {}", candidate.getFullname(), candidate.getEmail(), e.getMessage());
            }
        }
        return application;
    }

    // Get applications for a job (for recruiter)
    public List<ApplicationDTO> getApplicationsByJobId(Integer jobId, HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        List<Application> applications = applicationRepository.findByJobId(jobId);

        UserBenefits userBenefits = userBenefitsService.findByUserId(userId);
        Integer cvLimit = userBenefits != null ? userBenefits.getCvLimit() : null;

        List<ApplicationDTO> dtos = applications.stream()
                .map(app -> new ApplicationDTO(
                        app.getId(),
                        app.getJob().getTitle(),
                        app.getResume().getUser().getFullname(),
                        app.getResume().getUser().getEmail(),
                        app.getResume().getUser().getPhone(),
                        app.getStatus().name(),
                        app.getAppliedAt(),
                        null, // Không trả về file trực tiếp
                        app.getResume().getId(), // Trả về resumeId
                        app.getResume().getContent() // Trả về mô tả CV
                ))
                .toList();

        // BỎ GIỚI HẠN QUOTA CV
        return dtos;
    }

    // Get jobs applied by the current candidate
    public List<AppliedJobsDTO> getAppliedJobsByUser(HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        List<Application> applications = applicationRepository.findByCandidateId(userId);
        return applications.stream()
                .map(app -> new AppliedJobsDTO(
                        app.getId(),
                        new SimpleJobDTO(
                                app.getJob().getId(),
                                app.getJob().getTitle(),
                                app.getJob().getRecruiter().getFullname(),
                                app.getJob().getLocation(),
                                app.getJob().getSalaryRange()
                        ),
                        app.getResume().getTitle(),
                        app.getStatus().name(),
                        app.getAppliedAt(),
                        app.getInterviewSlot() != null ? app.getInterviewSlot().getStartTime() : null
                ))
                .toList();
    }

    // Get jobs applied by the specific user (for admin)
    public List<AppliedJobsDTO> getAppliedJobsByUserId(Integer userId) {
        List<Application> applications = applicationRepository.findByCandidateId(userId);
        return applications.stream()
                .map(app -> new AppliedJobsDTO(
                        app.getId(),
                        new SimpleJobDTO(
                                app.getJob().getId(),
                                app.getJob().getTitle(),
                                app.getJob().getRecruiter().getFullname(),
                                app.getJob().getLocation(),
                                app.getJob().getSalaryRange()
                        ),
                        app.getResume().getTitle(),
                        app.getStatus().name(),
                        app.getAppliedAt(),
                        app.getInterviewSlot() != null ? app.getInterviewSlot().getStartTime() : null
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
    public ApplicationDTO getApplicationDTOForResumeDownloadByApplicationId(Integer applicationId, Integer recruiterId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));
        Resume resume = app.getResume();
        String fullName = resume.getUser().getFullname();

        // Lấy tất cả benefit còn hạn/quota
        List<UserBenefits> allBenefits = userBenefitsService.getAllBenefitsByUserId(recruiterId);
        UserBenefits userBenefits = allBenefits.stream()
            .filter(ub -> ub.getCvLimit() != null && ub.getCvLimit() > 0
                    && ub.getUserPackage() != null && ub.getUserPackage().getExpirationDate() != null && ub.getUserPackage().getExpirationDate().isAfter(java.time.LocalDateTime.now())
            )
            .findFirst().orElse(null);
        if (userBenefits == null) {
            throw new IllegalArgumentException("Bạn đã xem hết số lượng CV cho phép của các gói dịch vụ.");
        }
        userBenefits.setCvLimit(userBenefits.getCvLimit() - 1);
        userBenefitsService.saveOrUpdateUserBenefits(
            recruiterId,
            userBenefits.getUserPackage().getId(),
            userBenefits.getPostAt(),
            userBenefits.getJobPostLimit(),
            userBenefits.getCvLimit(),
            userBenefits.getDescription()
        );

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

    public void addUserToJob(Integer jobId, Integer candidateId, Integer resumeId) {
        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidateId)) {
            throw new IllegalArgumentException("User already applied to this job");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        Application application = new Application();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setResume(resume);
        applicationRepository.save(application);
    }

    public void deleteUserFromJob(Integer jobId, Integer applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));
        if (!application.getJob().getId().equals(jobId)) {
            throw new IllegalArgumentException("Application does not belong to this job");
        }
        applicationRepository.deleteById(applicationId);
    }


}