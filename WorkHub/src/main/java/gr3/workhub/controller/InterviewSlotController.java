package gr3.workhub.controller;

import gr3.workhub.entity.InterviewSlot;
import gr3.workhub.service.InterviewSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workhub/api/v1/interview-slots")
@RequiredArgsConstructor
public class InterviewSlotController {
    private final InterviewSlotService slotService;

    @PostMapping
    public ResponseEntity<InterviewSlot> registerSlot(
            @RequestParam String sessionId,
            @RequestParam String candidateId,
            @RequestParam String jobId
    ) {
        InterviewSlot slot = slotService.createSlot(sessionId, candidateId, jobId);
        return ResponseEntity.ok(slot);
    }
}