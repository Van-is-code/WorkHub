package gr3.workhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_packages")
@Schema(description = "Gói dịch vụ dành cho nhà tuyển dụng, chứa thông tin giá, thời hạn và trạng thái.")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của gói dịch vụ", example = "1")
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Tên gói dịch vụ", example = "Gói Tiêu chuẩn")
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Giá của gói dịch vụ", example = "499000.00")
    private BigDecimal price;

    @Column(name = "job_post_limit", nullable = false)
    @Schema(description = "Số lượng tin tuyển dụng có thể đăng", example = "10")
    private Integer jobPostLimit = 5;

    @Column(name = "cv_limit", nullable = false)
    @Schema(description = "Số lượng CV có thể xem trong gói", example = "20")
    private Integer cvLimit = 5;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('proposal', 'urgent', 'standard') DEFAULT 'standard'")
    @Schema(description = "Loại độ ưu tiên hiển thị tin tuyển dụng", example = "urgent")
    private PostAt postAt = PostAt.standard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = " loại gói", example = "renewable")
    private RenewalPolicy renewalPolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = " loại gói", example = "renewable")
    private Type type;

    @Column(nullable = false)
    @Schema(description = "Thời hạn sử dụng gói (tính bằng ngày)", example = "30")
    private Integer duration;

    @Schema(description = "Mô tả chi tiết về gói dịch vụ", example = "Gói bao gồm 5 tin tuyển dụng và 10 lượt xem CV.")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Trạng thái hiện tại của gói", example = "active")
    private Status status = Status.active;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Thời điểm tạo gói dịch vụ", example = "2025-05-26T10:00:00")
    private LocalDateTime createdAt;

    // Mối quan hệ 1:N từ ServicePackage -> UserPackage
    @OneToMany(mappedBy = "servicePackage", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<UserPackage> userPackages;

    public enum Status {
        active, suspended, inactive
    }
    public enum Type {
        main,
        postat,
        cvlimit,
        jobpostlimit // tương lai có thể dùng
    }

    public enum RenewalPolicy {
        renewable,
        non_renewable
    }

    @Schema(description = "Loại hiển thị bài đăng của gói dịch vụ")
    public enum PostAt {
        @Schema(description = "Bài đăng đề xuất hiển thị nổi bật")
        proposal(3),

        @Schema(description = "Bài đăng khẩn cấp cần tuyển gấp")
        urgent(2),

        @Schema(description = "Bài đăng thông thường")
        standard(1);

        private final int level;

        PostAt(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    public boolean isMainPackage() {
        return this.type == Type.main ;
    }

    public boolean isCvLimitPackage() {
        return this.type == Type.cvlimit;
    }

    public boolean isJobPostLimitPackage() {
        return this.type == Type.jobpostlimit;
    }

    public int getLevel() {
        return this.postAt != null ? this.postAt.getLevel() : 0;
    }

    public boolean isPostAtPackage() {
        return this.postAt != null;
    }
}
