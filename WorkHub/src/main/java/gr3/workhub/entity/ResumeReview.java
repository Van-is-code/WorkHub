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
@Table(name = "resume_reviews")
@Schema(description = "Đánh giá hồ sơ (CV) của ứng viên bởi nhà tuyển dụng")
public class ResumeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của đánh giá", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    @Schema(description = "CV được đánh giá")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @Schema(description = "Ứng viên sở hữu CV được đánh giá")
    private User candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    @Schema(description = "Nhà tuyển dụng thực hiện đánh giá")
    private User recruiter;

    @Column(columnDefinition = "INT CHECK (rating BETWEEN 1 AND 5)")
    @Schema(description = "Điểm đánh giá từ 1 đến 5", example = "4")
    private Integer rating;

    @Lob
    @Schema(description = "Nhận xét chi tiết về hồ sơ", example = "Ứng viên có kỹ năng phù hợp, cần cải thiện phần giới thiệu bản thân.")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Thời điểm tạo đánh giá", example = "2025-05-26T14:22:00")
    private LocalDateTime createdAt;
}
