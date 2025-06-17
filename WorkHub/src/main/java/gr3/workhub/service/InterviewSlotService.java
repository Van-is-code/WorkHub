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

@Service
@RequiredArgsConstructor
public class InterviewSlotService {
    private final InterviewSlotRepository slotRepo;

    private final JobRepository jobRepo;
    private final UserRepository userRepo;
    private final ApplicationRepository applicationRepo;
    private final EmailService emailService;
    private final InterviewSessionRepository sessionRepo;
    private final RestTemplate restTemplate;
    private final TokenService tokenService;


    public InterviewSlot createSlot(String sessionId, String candidateId, String jobId) {
        boolean applied = applicationRepo.existsByJobIdAndCandidateId(
                Integer.parseInt(jobId), Integer.parseInt(candidateId));
        if (!applied) {
            throw new IllegalArgumentException("Candidate did not apply for this job");
        }

        InterviewSession session = sessionRepo.findById(UUID.fromString(sessionId))
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        User candidate = userRepo.findById(Integer.parseInt(candidateId))
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        Job job = jobRepo.findById(Integer.parseInt(jobId))
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        InterviewSlot slot = new InterviewSlot();
        slot.setInterviewSession(session);
        slot.setCandidate(candidate);
        slot.setJob(job);
        slot.setCreatedAt(LocalDateTime.now());
        slot.setStartTime(session.getStartTime()); // Set startTime from InterviewSession
        slot = slotRepo.save(slot);

        // Use tokenCandidate for the join link
        String joinLink = "http://localhost:8080/workhub/api/v1/interview-sessions/join/" + session.getTokenCandidate();

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

        emailService.sendinterview(candidate.getEmail(), "Interview Schedule", body);

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



}