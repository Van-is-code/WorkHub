package gr3.workhub.controller;

import gr3.workhub.dto.CreateInterviewSessionRequest;
import gr3.workhub.entity.InterviewSession;
import gr3.workhub.service.InterviewSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Tạo phiên phỏng vấn", description = "Tạo mới một phiên phỏng vấn")
    @PostMapping
    public ResponseEntity<InterviewSession> createSession(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin tạo phiên phỏng vấn") @RequestBody CreateInterviewSessionRequest req,
            HttpServletRequest request) {
        InterviewSession session = sessionService.createSession(req, request);
        return ResponseEntity.ok(session);
    }

    @Operation(summary = "Lấy tất cả phiên phỏng vấn", description = "Trả về danh sách tất cả các phiên phỏng vấn")
    @GetMapping
    public List<InterviewSession> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Lấy phiên phỏng vấn theo nhà tuyển dụng", description = "Trả về danh sách phiên phỏng vấn theo ID nhà tuyển dụng")
    @GetMapping("/by-recruiter/{recruiter}")
    public List<InterviewSession> getSessionsByRecruiter(
            @Parameter(description = "ID nhà tuyển dụng") @PathVariable("recruiter") Integer recruiter) {
        return sessionService.getSessionsByRecruiter(recruiter);
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng >Chuyển phiên phỏng vấn sang trạng thái INACTIVE", description = "Cập nhật trạng thái phiên phỏng vấn sang INACTIVE theo ID")
    @PatchMapping("/inactive/{id}/status")
    public ResponseEntity<InterviewSession> updateStatus(
            @Parameter(description = "ID phiên phỏng vấn") @PathVariable("id") UUID id) {
        InterviewSession updated = sessionService.updateSessionStatus(id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Chuyển phiên phỏng vấn sang trạng thái ACTIVE", description = "Cập nhật trạng thái phiên phỏng vấn sang ACTIVE theo ID")
    @PatchMapping("/active/{id}/status")
    public ResponseEntity<InterviewSession> activeStatus(
            @Parameter(description = "ID phiên phỏng vấn") @PathVariable("id") UUID id) {
        InterviewSession updated = sessionService.activeSessionStatus(id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "<EndPoint này không cần call > Cập nhật token cho ứng viên", description = "Cập nhật token cho ứng viên theo ID phiên phỏng vấn")
    @PatchMapping("/{id}/candidate-token")
    public ResponseEntity<InterviewSession> updateCandidateToken(
            @Parameter(description = "ID phiên phỏng vấn") @PathVariable("id") UUID id) {
        InterviewSession updated = sessionService.updateCandidateToken(id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Tham gia phiên phỏng vấn bằng token", description = "Chuyển hướng người dùng đến phòng phỏng vấn dựa trên token")
    @GetMapping("/join/{token}")
    public RedirectView joinByToken(
            @Parameter(description = "Token của phiên phỏng vấn") @PathVariable String token) {
        try {
            String redirectUrl = sessionService.getRedirectUrlByToken(UUID.fromString(token));
            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            return new RedirectView("/error/invalid-session");
        }
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Lấy recruiter code theo user id",
            description = "Trả về recruiter code dựa trên user id trong token"
    )
    @GetMapping("/join-by-recruiter-token")
    public RedirectView joinByRecruiterToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new RedirectView("/error/invalid-session");
        }
        String jwt = authHeader.substring(7);
        try {
            String redirectUrl = sessionService.getRecruiterRedirectUrlByToken(jwt);
            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            return new RedirectView("/error/invalid-session");
        }
    }
}