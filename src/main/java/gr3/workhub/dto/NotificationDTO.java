package gr3.workhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Integer userId;            // Refers to User
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}