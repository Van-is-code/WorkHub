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
@Table(name = "reviews")
@Schema(description = "Đánh giá của người dùng dành cho công ty")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của đánh giá", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @Schema(description = "Thông tin công ty được đánh giá")
    private CompanyProfile company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Người dùng thực hiện đánh giá")
    private User user;

    @Column(columnDefinition = "INT CHECK (rating BETWEEN 1 AND 5)")
    @Schema(description = "Điểm đánh giá từ 1 đến 5", example = "4")
    private Integer rating;

    @Lob
    @Schema(description = "Nội dung nhận xét")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Thời điểm đánh giá được tạo", example = "2025-05-26T10:00:00")
    private LocalDateTime createdAt;
}
