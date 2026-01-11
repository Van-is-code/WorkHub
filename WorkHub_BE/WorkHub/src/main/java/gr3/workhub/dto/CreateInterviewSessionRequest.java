package gr3.workhub.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateInterviewSessionRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String recruiterId; // UUID as String
    private Integer jobId;
}