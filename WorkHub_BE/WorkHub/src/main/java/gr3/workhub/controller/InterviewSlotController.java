package gr3.workhub.controller;

import gr3.workhub.dto.InterviewScheduleDTO;
import gr3.workhub.dto.InterviewSlotResponseDTO;
import gr3.workhub.entity.InterviewSlot;
import gr3.workhub.service.InterviewSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @PostMapping
    public ResponseEntity<InterviewSlot> registerSlot(
            @Parameter(description = "ID phiên phỏng vấn") @RequestParam String sessionId,
            @Parameter(description = "ID ứng viên") @RequestParam(required = false) String candidateId,
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

    @GetMapping("/by-job/{jobId}")
    public ResponseEntity<List<InterviewSlotResponseDTO>> getSlotsByJob(@PathVariable Integer jobId) {
        List<InterviewSlot> slots = slotService.getSlotsByJob(jobId);
        List<InterviewSlotResponseDTO> dtos = slots.stream().map(slot -> {
            InterviewSlotResponseDTO dto = new InterviewSlotResponseDTO();
            dto.setId(slot.getId());
            dto.setStartTime(slot.getStartTime());
            dto.setBooked(slot.getCandidate() != null); // Nếu đã có candidate thì đã được đặt
            dto.setCandidateName(slot.getCandidate() != null ? slot.getCandidate().getFullname() : null); // Thêm dòng này
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng> Tạo slot phỏng vấn cho job (không cần session/candidate)",
            description = "Tạo mới một khung giờ phỏng vấn cho job, chỉ cần jobId, startTime, endTime."
    )
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @PostMapping("/by-job")
    public ResponseEntity<?> createSlotForJob(
            @RequestParam Integer jobId,
            @RequestParam String startTime,
            @RequestParam(required = false) String endTime
    ) {
        InterviewSlot slot = slotService.createSlotForJob(jobId, startTime, endTime);
        // Trả về DTO đơn giản, tránh trả về entity có vòng lặp
        return ResponseEntity.ok(new gr3.workhub.dto.InterviewSlotResponseDTO() {{
            setId(slot.getId());
            setStartTime(slot.getStartTime());
            setBooked(slot.getCandidate() != null);
        }});
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable UUID slotId) {
        slotService.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}