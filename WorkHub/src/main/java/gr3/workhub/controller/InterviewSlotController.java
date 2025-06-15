package gr3.workhub.controller;

import gr3.workhub.dto.InterviewScheduleDTO;
import gr3.workhub.entity.InterviewSlot;
import gr3.workhub.service.InterviewSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workhub/api/v1/interview-slots")
@RequiredArgsConstructor
public class InterviewSlotController {
    private final InterviewSlotService slotService;

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng  > Đăng ký khung giờ phỏng vấn",
            description = "Tạo mới một khung giờ phỏng vấn cho ứng viên"
    )
    @PostMapping
    public ResponseEntity<InterviewSlot> registerSlot(
            @Parameter(description = "ID phiên phỏng vấn") @RequestParam String sessionId,
            @Parameter(description = "ID ứng viên") @RequestParam String candidateId,
            @Parameter(description = "ID công việc") @RequestParam String jobId
    ) {
        InterviewSlot slot = slotService.createSlot(sessionId, candidateId, jobId);
        return ResponseEntity.ok(slot);
    }


    @GetMapping("/schedule/candidate")
    @Operation(
            summary = " <EndPoint cho trang của ứng viên> Lấy lịch phỏng vấn của ứng viên",
            description = "Trả về danh sách lịch phỏng vấn của ứng viên dựa trên JWT token trong header Authorization"
    )
    public List<InterviewScheduleDTO> getScheduleByCandidate(
            @Parameter(hidden = true) HttpServletRequest request) {
        return slotService.getScheduleByToken(request);
    }
}