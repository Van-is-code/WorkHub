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
@Table(name = "user_packages")
@Schema(description = "Thông tin về gói dịch vụ mà người dùng đã mua")
public class UserPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của gói người dùng", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Người dùng đã mua gói này")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    @Schema(description = "Gói dịch vụ mà người dùng đã mua")
    private ServicePackage servicePackage;

    @CreationTimestamp
    @Column(name = "purchase_date", nullable = false, updatable = false)
    @Schema(description = "Ngày mua gói dịch vụ", example = "2024-11-15T10:00:00")
    private LocalDateTime purchaseDate;

    @Column(name = "renewal_date")
    @Schema(description = "Ngày gia hạn gần nhất", example = "2025-05-15T00:00:00")
    private LocalDateTime renewalDate;

    @Column(name = "expiration_date", nullable = false)
    @Schema(description = "Ngày hết hạn của gói dịch vụ", example = "2025-06-15T00:00:00")
    private LocalDateTime expirationDate;

    @Column
    @Schema(description = "Giá gói dịch vụ tại thời điểm mua", example = "499.99")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('active', 'expired') DEFAULT 'active'")
    @Schema(description = "Trạng thái của gói dịch vụ", example = "active")
    private Status status = Status.active;

    @Lob
    @Schema(description = "Mô tả chi tiết về giao dịch hoặc quyền lợi đi kèm", example = "Gói 6 tháng, hỗ trợ đăng tin khẩn")
    private String description;

    @Schema(description = "Trạng thái hiện tại của gói người dùng")
    public enum Status {
        active,   // Gói còn hiệu lực
        expired   // Gói đã hết hạn
    }
}
