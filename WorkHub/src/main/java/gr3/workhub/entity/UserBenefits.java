package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_benefits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Quyền lợi và hạn mức của người dùng theo gói dịch vụ đã mua.")
public class UserBenefits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của quyền lợi", example = "1")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Người dùng sở hữu quyền lợi này")
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_package_id", nullable = false)
    @Schema(description = "Gói dịch vụ mà quyền lợi này thuộc về")
    private UserPackage userPackage;

    @Column(nullable = false)
    @Schema(description = "Số lượng bài đăng việc làm tối đa cho phép", example = "10")
    private Integer jobPostLimit;

    @Column(name = "cv_limit")
    @Schema(description = "Số lượng CV có thể xem/tải", example = "50")
    private Integer cvLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_at", columnDefinition = "ENUM('proposal', 'urgent', 'standard') DEFAULT 'standard'")
    @Schema(description = "Kiểu hiển thị bài đăng ưu tiên", example = "urgent")
    private PostAt postAt = PostAt.standard;

    @Column(name = "description")
    @Schema(description = "Mô tả chi tiết về quyền lợi", example = "Gói dành cho doanh nghiệp với khả năng hiển thị ưu tiên.")
    private String description;

    @Column(name = "updated_at")
//    @CreationTimestamp
    @Schema(description = "Thời điểm cập nhật quyền lợi gần nhất", example = "2024-12-01T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Loại hiển thị bài đăng của gói dịch vụ")
    public enum PostAt {
        proposal, // Đề xuất nổi bật
        urgent,   // Ưu tiên khẩn cấp
        standard  // Tiêu chuẩn
    }
}
