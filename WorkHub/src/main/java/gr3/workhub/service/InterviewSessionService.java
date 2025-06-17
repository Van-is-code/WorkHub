package gr3.workhub.service;

import gr3.workhub.dto.CreateInterviewSessionRequest;
import gr3.workhub.entity.InterviewSession;
import gr3.workhub.entity.User;
import gr3.workhub.repository.InterviewSessionRepository;
import gr3.workhub.repository.UserRepository;
import gr3.workhub.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {
    private final InterviewSessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final JwtUtil jwtUtil; // Inject JwtUtil

    @Value("${hms.template.id}")
    private String templateId;

    @Value("${hms.token}")
    private String token;

    public String create100msRoom(String title) {
        String url = "https://api.100ms.live/v2/rooms";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("name", title);
        body.put("description", "Interview room for " + title);
        body.put("template_id", templateId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || responseBody.get("id") == null) {
            throw new IllegalStateException("Failed to create 100ms room. Response: " + responseBody);
        }

        return responseBody.get("id").toString();
    }

    public Map<String, String> getRoomCodesByRoomId(String roomId) {
        String url = "https://api.100ms.live/v2/room-codes/room/" + roomId;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("data") == null) {
            throw new IllegalStateException("Failed to get room codes. Response: " + body);
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("data");
        Map<String, String> codes = new HashMap<>();
        for (Map<String, Object> item : dataList) {
            String role = item.get("role").toString();
            String code = item.get("code").toString();
            codes.put(role, code);
        }

        return codes;
    }

    public InterviewSession createSession(CreateInterviewSessionRequest req, HttpServletRequest request) {
        // Extract JWT from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        String jwt = authHeader.substring(7);

        // Parse recruiter ID from JWT
        String recruiterId = parseRecruiterIdFromJwt(jwt);

        User recruiter = userRepo.findById(Integer.parseInt(recruiterId))
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        String roomId = create100msRoom(req.getTitle());

        InterviewSession session = InterviewSession.builder()
                .recruiter(recruiter)
                .title(req.getTitle())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .roomId(roomId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        session = sessionRepo.save(session);

        Map<String, String> codes = getRoomCodesByRoomId(roomId);
        session.setCodeCandidate(codes.get("candidate"));
        session.setCodeRecruiter(codes.get("recruiter"));

        session.setTokenCandidate(UUID.randomUUID());

        session = sessionRepo.save(session);

        String recruiterLink = "https://workhub.app.100ms.live/preview/" + session.getCodeRecruiter();
        String body = "Your interview session has been created.\n"
                + "Join link: " + recruiterLink + "\n"
                + "Session: " + session.getTitle() + "\n"
                + "Start: " + session.getStartTime();
        emailService.sendinterview(recruiter.getEmail(), "Interview Session Created", body);

        return session;
    }

    // Use JwtUtil to extract recruiter id from JWT
    private String parseRecruiterIdFromJwt(String jwt) {
        org.springframework.security.oauth2.jwt.Jwt decoded = jwtUtil.decodeToken(jwt);
        Object idClaim = decoded.getClaim("id");
        if (idClaim == null) {
            throw new IllegalArgumentException("JWT does not contain recruiter id");
        }
        return idClaim.toString();
    }

    public String getRedirectUrlByToken(UUID token) {
        InterviewSession session = sessionRepo.findByTokenCandidate(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired candidate token"));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new IllegalStateException("Session is not active");
        }

        return "https://workhub.app.100ms.live/preview/" + session.getCodeCandidate();
    }
    public List<InterviewSession> getAllSessions() {
        return sessionRepo.findAll();
    }

    public List<InterviewSession> getSessionsByRecruiter(Integer recruiter) {
        return sessionRepo.findByRecruiter_Id(recruiter);
    }
    // Update session status
    public InterviewSession updateSessionStatus(UUID sessionId) {
        InterviewSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setStatus("INACTIVE");
        return sessionRepo.save(session);
    }  // Update session status
    public InterviewSession activeSessionStatus(UUID sessionId) {
        InterviewSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setStatus("ACTIVE");
        return sessionRepo.save(session);
    }

    // Update candidate token
    public InterviewSession updateCandidateToken(UUID sessionId) {
        InterviewSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setTokenCandidate(UUID.randomUUID());
        return sessionRepo.save(session);
    }

    // Extract recruiter code by recruiter ID
    public String getRecruiterCodeByUserId(Integer userId) {
        InterviewSession session = sessionRepo.findTopByRecruiter_IdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("No session found for recruiter"));
        return session.getCodeRecruiter();
    }

    // Handle redirect logic by recruiter token
    public String getRecruiterRedirectUrlByToken(String jwt) {
        String recruiterId = parseRecruiterIdFromJwt(jwt);
        String recruiterCode = getRecruiterCodeByUserId(Integer.parseInt(recruiterId));
        return "https://workhub.app.100ms.live/preview/" + recruiterCode;
    }
}