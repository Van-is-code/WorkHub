package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin khung giờ phỏng vấn")
public class InterviewSlot {
    @Id
    @GeneratedValue
    @Schema(description = "ID khung giờ phỏng vấn", example = "c0a8012e-7e6d-11ec-90d6-0242ac120003")
    private UUID id;

    @ManyToOne
    @Schema(description = "Phiên phỏng vấn liên kết")
    private InterviewSession interviewSession;

    @ManyToOne
    @Schema(description = "Ứng viên tham gia phỏng vấn")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @Schema(description = "Công việc liên quan")
    private Job job;

    @Schema(description = "Thời gian bắt đầu", example = "2024-06-15T09:00:00")
    private LocalDateTime startTime;


    @Schema(description = "Thời gian tạo khung giờ", example = "2024-06-15T09:00:00")
    private LocalDateTime createdAt;
}