package gr3.workhub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_package_id", nullable = false)
    private ServicePackage servicePackage;

    private BigDecimal amount;
    private String method; // e.g. "banking", "momo", "vnpay"
    private String status; // e.g. "pending", "success", "failed"
    private LocalDateTime createdAt;
}
