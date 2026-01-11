package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saved_jobs")
@Schema(description = "Thông tin công việc đã được ứng viên lưu lại")
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của bản ghi lưu công việc", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @Schema(description = "Người dùng (ứng viên) đã lưu công việc")
    private User candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    @Schema(description = "Công việc được lưu bởi ứng viên")
    private Job job;

    @CreationTimestamp
    @Column(name = "saved_at", nullable = false, updatable = false)
    @Schema(description = "Thời điểm công việc được lưu", example = "2025-05-26T08:30:00")
    private LocalDateTime savedAt;
}
