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
@Schema(description = "Thông tin phiên phỏng vấn")
public class InterviewSession {
    @Id
    @GeneratedValue
    @Schema(description = "ID phiên phỏng vấn", example = "c0a8012e-7e6d-11ec-90d6-0242ac120003")
    private UUID id;

    @ManyToOne
    @Schema(description = "Nhà tuyển dụng tạo phiên phỏng vấn")
    private User recruiter;

    @Schema(description = "Tiêu đề phiên phỏng vấn", example = "Phỏng vấn kỹ thuật")
    private String title;

    @Schema(description = "Thời gian bắt đầu", example = "2024-06-15T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Thời gian kết thúc", example = "2024-06-15T10:00:00")
    private LocalDateTime endTime;

    @Schema(description = "ID phòng 100ms.live", example = "room_12345")
    private String roomId;

    @Schema(description = "Trạng thái phiên (ACTIVE, ENDED, CANCELLED)", example = "ACTIVE")
    private String status;

    @Schema(description = "Thời gian tạo phiên", example = "2024-06-14T08:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Mã tham gia cho ứng viên", example = "CAND123")
    private String codeCandidate;

    @Schema(description = "Mã tham gia cho nhà tuyển dụng", example = "REC456")
    private String codeRecruiter;

    @Schema(description = "Token của ứng viên", example = "c0a8012e-7e6d-11ec-90d6-0242ac120003")
    private UUID tokenCandidate;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

}