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
@Table(name = "messages")
@Schema(description = "Tin nhắn giữa người dùng gửi và nhận trong hệ thống WorkHub")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của tin nhắn", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @Schema(description = "Người gửi tin nhắn")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @Schema(description = "Người nhận tin nhắn")
    private User receiver;

    @Lob
    @Column(nullable = false)
    @Schema(description = "Nội dung tin nhắn", example = "Chào bạn, tôi quan tâm đến công việc này!")
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    @Schema(description = "Thời gian tin nhắn được gửi", example = "2024-05-25T14:35:00")
    private LocalDateTime sentAt;
}
