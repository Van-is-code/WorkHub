package gr3.workhub.service;

import gr3.workhub.dto.InterviewScheduleDTO;
import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import gr3.workhub.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class InterviewSlotService {
    private static final Logger logger = LoggerFactory.getLogger(InterviewSlotService.class);

    private final InterviewSlotRepository slotRepo;

    private final JobRepository jobRepo;
    private final UserRepository userRepo;
    private final ApplicationRepository applicationRepo;
    private final EmailService emailService;
    private final InterviewSessionRepository sessionRepo;
    private final RestTemplate restTemplate;
    private final TokenService tokenService;


    public InterviewSlot createSlot(String sessionId, String candidateId, String jobId) {
        logger.info("[createSlot] INPUT: sessionId={}, candidateId={}, jobId={}", sessionId, candidateId, jobId);
        InterviewSession session = sessionRepo.findById(UUID.fromString(sessionId))
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        Job job = jobRepo.findById(Integer.parseInt(jobId))
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        InterviewSlot slot = new InterviewSlot();
        slot.setInterviewSession(session);
        slot.setJob(job);
        slot.setCreatedAt(LocalDateTime.now());
        slot.setStartTime(session.getStartTime()); // Set startTime from InterviewSession

        User candidate = null;
        if (candidateId != null && !candidateId.isEmpty()) {
            logger.info("[createSlot] Checking if candidate {} applied for job {}", candidateId, jobId);
            boolean applied = applicationRepo.existsByJobIdAndCandidateId(
                    Integer.parseInt(jobId), Integer.parseInt(candidateId));
            logger.info("[createSlot] Candidate {} applied for job {}: {}", candidateId, jobId, applied);
            if (!applied) {
                logger.warn("[createSlot] Candidate {} did not apply for job {}", candidateId, jobId);
                throw new IllegalArgumentException("Candidate did not apply for this job");
            }
            candidate = userRepo.findById(Integer.parseInt(candidateId))
                    .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
            logger.info("[createSlot] Candidate found: {} (email: {})", candidate.getFullname(), candidate.getEmail());
            slot.setCandidate(candidate);
        } else {
            logger.warn("[createSlot] candidateId is null or empty, will not set candidate for slot");
        }

        slot = slotRepo.save(slot);

        // Reload session to get latest codeCandidate
        session = sessionRepo.findById(session.getId())
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
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
                "<p>Dear <strong>" + (candidate != null ? candidate.getFullname() : "") + "</strong>,</p>" +
                "<p>Your interview for the job position <strong>" + job.getTitle() + "</strong> is scheduled at <strong>" +
                session.getStartTime().toString() + "</strong>.</p>" +
                "<p>You can join the interview by clicking the button below. The link will be active 15 minutes before the scheduled time:</p>" +
                "<div class='text-center' style='margin: 30px 0;'>" +
                "<a href='" + joinLink + "' class='btn btn-primary btn-lg' style='background-color: #1f41ce; border: none;'>Join Interview</a>" +
                "</div>" +
                "<p>If you have any questions, feel free to reply to this email.</p>" +
                "<p style='margin-top: 30px;'>Best regards,<br/>The WorkHub Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        if (candidate != null) {
            logger.info("[DEBUG] Sending interview email to candidate: {} | email: {} | joinLink: {} | codeCandidate: {}", candidate.getFullname(), candidate.getEmail(), joinLink, codeCandidate);
            try {
                emailService.sendinterview(candidate.getEmail(), "Interview Schedule", body);
                logger.info("Interview email sent to candidate: {} (email: {})", candidate.getFullname(), candidate.getEmail());
            } catch (Exception e) {
                logger.error("Failed to send interview email to candidate: {} (email: {}), error: {}", candidate.getFullname(), candidate.getEmail(), e.getMessage());
            }
        } else {
            logger.warn("No candidate found to send interview email.");
        }

        return slot;
    }

    public List<InterviewScheduleDTO> getScheduleByToken(HttpServletRequest request) {
        Integer candidateId = tokenService.extractUserIdFromRequest(request);
        List<InterviewSlot> slots = slotRepo.findByCandidateId(candidateId);
        return slots.stream()
                .map(slot -> new InterviewScheduleDTO(
                        slot.getJob().getTitle(),
                        slot.getInterviewSession().getTitle(),
                        slot.getStartTime()
                ))
                .collect(Collectors.toList());
    }

    public List<InterviewSlot> getSlotsByJob(Integer jobId) {
        return slotRepo.findByJobId(jobId);
    }

    public InterviewSlot createSlotForJob(Integer jobId, String startTime, String endTime) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        InterviewSlot slot = new InterviewSlot();
        slot.setJob(job);
        slot.setStartTime(java.time.LocalDateTime.parse(startTime));
        slot.setCreatedAt(java.time.LocalDateTime.now());
        // Nếu có endTime thì có thể lưu vào một trường khác nếu entity có
        return slotRepo.save(slot);
    }

    // Xóa slot phỏng vấn theo id
    public void deleteSlot(UUID slotId) {
        slotRepo.deleteById(slotId);
    }
}