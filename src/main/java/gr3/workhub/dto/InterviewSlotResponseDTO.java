package gr3.workhub.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class InterviewSlotResponseDTO {
    private UUID id;
    private LocalDateTime startTime;
    private boolean booked;
    private String candidateName; // Thêm trường này

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
}
