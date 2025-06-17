package gr3.workhub.controller;

import gr3.workhub.dto.NotificationDTO;
import gr3.workhub.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "Quản lý thông báo cho người dùng, bao gồm gửi và đánh dấu đã đọc.")
@RestController
@CrossOrigin

@RequestMapping("/workhub/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(
            summary = "Gửi thông báo đến người dùng",
            description = "Tạo mới một thông báo và gửi thông qua WebSocket đến user (qua topic `/topic/notifications/{userId}`)."
    )
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @Parameter(description = "ID người nhận thông báo") @RequestParam Integer userId,
            @RequestBody String message) {
        NotificationDTO notification = notificationService.createNotification(userId, message);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
        return ResponseEntity.ok(notification);
    }

    @Operation(
            summary = "Lấy danh sách thông báo của người dùng",
            description = "Trả về danh sách NotificationDTO của một người dùng."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForUser(
            @Parameter(description = "ID người dùng") @PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @Operation(
            summary = "Đánh dấu một thông báo là đã đọc",
            description = "Cập nhật trạng thái của một notification thành đã đọc (isRead = true)."
    )
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @Parameter(description = "ID thông báo cần đánh dấu đã đọc") @PathVariable Integer notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Gửi thông báo qua WebSocket (nội bộ dùng)",
            description = "Dành cho các service nội bộ hoặc client dùng WebSocket gửi thông báo. Endpoint không dùng HTTP mà qua `/ws/send-notification` (MessageMapping)."
    )
    @MessageMapping("/send-notification")
    public void sendNotificationWebSocket(NotificationDTO notificationDTO) {
        NotificationDTO saved = notificationService.createNotification(notificationDTO.getUserId(), notificationDTO.getMessage());
        messagingTemplate.convertAndSend("/topic/notifications/" + notificationDTO.getUserId(), saved);
    }
}
