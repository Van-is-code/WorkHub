package gr3.workhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Integer senderId;          // Refers to User (sender)
    private Integer receiverId;        // Refers to User (receiver)
    private String content;
    private LocalDateTime sentAt;
}