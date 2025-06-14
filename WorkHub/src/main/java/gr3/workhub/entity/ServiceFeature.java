package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_features")
@Schema(description = "Tính năng đi kèm trong gói dịch vụ tuyển dụng")
public class ServiceFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của tính năng", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    @Schema(description = "Gói dịch vụ mà tính năng này thuộc về")
    private ServicePackage servicePackage;

    @Column(nullable = false)
    @Schema(description = "Tên tính năng", example = "Ưu tiên hiển thị công việc")
    private String featureName;

    @Lob
    @Schema(description = "Mô tả chi tiết tính năng", example = "Tính năng giúp tin tuyển dụng hiển thị nổi bật trên trang chủ")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('proposal', 'urgent', 'standard') DEFAULT 'standard'")
    @Schema(description = "Loại độ ưu tiên hiển thị tin tuyển dụng", example = "urgent")
    private PostAt postAt = PostAt.standard;

    @Column(nullable = false)
    @Schema(description = "Số lượng tin tuyển dụng có thể đăng", example = "10")
    private Integer jobPostLimit = 5;

    @Column(nullable = false)
    @Schema(description = "Số lượng CV có thể xem trong gói", example = "20")
    private Integer cvLimit = 5;

    public enum PostAt {
        proposal, urgent, standard
    }
}
