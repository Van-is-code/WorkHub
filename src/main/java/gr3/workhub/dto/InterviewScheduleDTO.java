package gr3.workhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InterviewScheduleDTO {
    private String jobTitle;
    private String sessionTitle;
    private LocalDateTime startTime;
}