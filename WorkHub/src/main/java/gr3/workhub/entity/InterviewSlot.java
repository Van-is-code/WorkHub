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
public class InterviewSlot {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private InterviewSession interviewSession;

    @ManyToOne
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    private LocalDateTime createdAt;
}