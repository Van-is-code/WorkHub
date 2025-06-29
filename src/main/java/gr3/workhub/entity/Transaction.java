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
@Table(name = "transactions")
@Schema(description = "Giao dịch mua gói dịch vụ của người dùng.")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của giao dịch", example = "1001")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Người dùng thực hiện giao dịch")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    @Schema(description = "Gói dịch vụ đã được mua")
    private ServicePackage servicePackage;

    @Schema(description = "Số tiền đã thanh toán", example = "199.99")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('pending', 'completed', 'failed') DEFAULT 'pending'")
    @Schema(description = "Trạng thái của giao dịch", example = "pending")
    private Status status = Status.pending;

    @Lob
    @Schema(description = "Ghi chú hoặc mô tả thêm cho giao dịch", example = "Thanh toán qua VNPay cho gói 1 tháng.")
    private String description;

    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false, updatable = false)
    @Schema(description = "Ngày giờ giao dịch được tạo", example = "2024-05-26T14:30:00")
    private LocalDateTime transactionDate;

    @Schema(description = "Enum thể hiện trạng thái của giao dịch.")
    public enum Status {
        pending,
        completed,
        failed
    }
}
