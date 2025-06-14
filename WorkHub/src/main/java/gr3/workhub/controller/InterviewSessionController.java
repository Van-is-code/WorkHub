package gr3.workhub.controller;

import gr3.workhub.dto.CreateInterviewSessionRequest;
import gr3.workhub.entity.InterviewSession;
import gr3.workhub.service.InterviewSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workhub/api/v1/interview-sessions")
public class InterviewSessionController {
    private final InterviewSessionService sessionService;

    @PostMapping
    public ResponseEntity<InterviewSession> createSession(@RequestBody CreateInterviewSessionRequest req, HttpServletRequest request) {
        InterviewSession session = sessionService.createSession(req, request);
        return ResponseEntity.ok(session);
    }


    @GetMapping
    public List<InterviewSession> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/by-recruiter/{recruiter}")
    public List<InterviewSession> getSessionsByRecruiter(@PathVariable("recruiter") Integer recruiter) {
        return sessionService.getSessionsByRecruiter(recruiter);
    }


    // Update session status
    @PatchMapping("/inactive/{id}/status")
    public ResponseEntity<InterviewSession> updateStatus(@PathVariable("id") UUID id) {
        InterviewSession updated = sessionService.updateSessionStatus(id);
        return ResponseEntity.ok(updated);
    }

    // Update session status
    @PatchMapping("/active/{id}/status")
    public ResponseEntity<InterviewSession> activeStatus(@PathVariable("id") UUID id) {
        InterviewSession updated = sessionService.activeSessionStatus(id);
        return ResponseEntity.ok(updated);
    }

    // Update candidate token
    @PatchMapping("/{id}/candidate-token")
    public ResponseEntity<InterviewSession> updateCandidateToken(@PathVariable("id") UUID id) {
        InterviewSession updated = sessionService.updateCandidateToken(id);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/join/{token}")
    public RedirectView joinByToken(@PathVariable String token) {
        try {
            String redirectUrl = sessionService.getRedirectUrlByToken(UUID.fromString(token));
            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            // Redirect to an error page or return a message
            return new RedirectView("/error/invalid-session");
        }
    }
}