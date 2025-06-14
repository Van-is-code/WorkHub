package gr3.workhub.controller;

import gr3.workhub.dto.MessageDTO;
import gr3.workhub.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Message", description = "Quản lý tin nhắn giữa người dùng qua REST và WebSocket.")
@RestController
@CrossOrigin

@RequestMapping("/workhub/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(
            summary = "Gửi tin nhắn (REST)",
            description = "Gửi một tin nhắn mới từ người gửi tới người nhận thông qua HTTP POST."
    )
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(
            @Parameter(description = "ID người gửi") @RequestParam Integer senderId,
            @Parameter(description = "ID người nhận") @RequestParam Integer receiverId,
            @RequestBody String content) {
        return ResponseEntity.ok(messageService.sendMessage(senderId, receiverId, content));
    }

    @Operation(
            summary = "Lấy tin nhắn của người dùng",
            description = "Trả về toàn bộ tin nhắn của một người dùng theo userId."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessagesForUser(
            @Parameter(description = "ID người dùng cần lấy tin nhắn") @PathVariable Integer userId) {
        return ResponseEntity.ok(messageService.getMessagesForUser(userId));
    }

    @Operation(
            summary = "Gửi tin nhắn qua WebSocket",
            description = """
            Gửi tin nhắn sử dụng WebSocket STOMP.
            - Client gửi tới `/app/send-message`
            - Server broadcast về `/topic/messages`
            """
    )
    @MessageMapping("/send-message") // Maps to "/app/send-message"
    @SendTo("/topic/messages")       // Broadcasts to "/topic/messages"
    public MessageDTO sendMessageWebSocket(MessageDTO messageDTO) {
        return messageService.sendMessage(
                messageDTO.getSenderId(),
                messageDTO.getReceiverId(),
                messageDTO.getContent()
        );
    }
}
