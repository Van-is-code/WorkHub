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
@Table(name = "resume_views")
@Schema(description = "Lượt xem hồ sơ của ứng viên bởi nhà tuyển dụng")
public class ResumeView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của lượt xem", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    @Schema(description = "Hồ sơ (CV) được xem")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    @Schema(description = "Nhà tuyển dụng đã xem hồ sơ")
    private User recruiter;

    @CreationTimestamp
    @Column(name = "viewed_at", nullable = false, updatable = false)
    @Schema(description = "Thời điểm hồ sơ được xem", example = "2025-05-26T09:45:00")
    private LocalDateTime viewedAt;
}
