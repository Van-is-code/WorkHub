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
@Table(name = "notifications")
@Schema(description = "Thông báo được gửi tới người dùng")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của thông báo", example = "1001")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Người nhận thông báo")
    private User user;

    @Lob
    @Column(nullable = false)
    @Schema(description = "Nội dung thông báo", example = "Bạn có một đơn ứng tuyển mới.")
    private String message;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Schema(description = "Trạng thái đã đọc hay chưa", example = "false")
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Thời gian tạo thông báo", example = "2025-05-27T10:15:30")
    private LocalDateTime createdAt;
}
