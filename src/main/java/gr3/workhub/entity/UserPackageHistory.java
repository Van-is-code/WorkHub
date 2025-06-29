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
@Table(name = "user_package_histories")
@Schema(description = "Lịch sử các gói dịch vụ mà người dùng đã mua hoặc gia hạn")
public class UserPackageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của lịch sử gói người dùng", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Người dùng liên quan đến lịch sử gói này")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    @Schema(description = "Gói dịch vụ liên quan đến lịch sử này")
    private ServicePackage servicePackage;

    @Column(name = "purchase_date")
    @Schema(description = "Ngày mua gói dịch vụ", example = "2024-11-15T10:00:00")
    private LocalDateTime purchaseDate;

    @Column(name = "renewal_date")
    @Schema(description = "Ngày gia hạn gần nhất", example = "2025-05-15T00:00:00")
    private LocalDateTime renewalDate;

    @Column(name = "expiration_date")
    @Schema(description = "Ngày hết hạn của gói dịch vụ", example = "2025-06-15T00:00:00")
    private LocalDateTime expirationDate;

    @Column
    @Schema(description = "Giá gói dịch vụ tại thời điểm mua", example = "499.99")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('active', 'expired') DEFAULT 'active'")
    @Schema(description = "Trạng thái của gói dịch vụ", example = "active")
    private UserPackage.Status status = UserPackage.Status.active;

    @Lob
    @Schema(description = "Mô tả chi tiết về giao dịch hoặc quyền lợi đi kèm", example = "Gói 6 tháng, hỗ trợ đăng tin khẩn")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Schema(description = "Thời điểm tạo bản ghi lịch sử", example = "2024-11-15T10:00:00")
    private LocalDateTime createdAt;
}