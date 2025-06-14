package gr3.workhub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSession {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User recruiter;

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String roomId; // 100ms.live room id
    private String status; // ACTIVE, ENDED, CANCELLED
    private LocalDateTime createdAt;
    private String codeCandidate;
    private String codeRecruiter;
    private UUID tokenCandidate;
    }

