package gr3.workhub.entity;

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

    @OneToMany(mappedBy = "servicePackage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Schema(description = "Danh sách các tính năng đi kèm gói dịch vụ")
    private java.util.List<ServiceFeature> features = new java.util.ArrayList<>();

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

    @Schema(description = "Trạng thái của gói dịch vụ")
    public enum Status {
        active, suspended, inactive
    }
}
