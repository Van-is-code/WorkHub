package gr3.workhub.service;

import gr3.workhub.dto.MessageDTO;
import gr3.workhub.entity.Message;
import gr3.workhub.entity.User;
import gr3.workhub.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageDTO sendMessage(Integer senderId, Integer receiverId, String content) {
        Message message = new Message();
        message.setSender(new User(senderId));
        message.setReceiver(new User(receiverId));
        message.setContent(content);
        Message savedMessage = messageRepository.save(message);

        return new MessageDTO(
                savedMessage.getSender().getId(),
                savedMessage.getReceiver().getId(),
                savedMessage.getContent(),
                savedMessage.getSentAt()
        );
    }

    public List<MessageDTO> getMessagesForUser(Integer userId) {
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(userId, userId);
        return messages.stream()
                .map(message -> new MessageDTO(
                        message.getSender().getId(),
                        message.getReceiver().getId(),
                        message.getContent(),
                        message.getSentAt()
                ))
                .collect(Collectors.toList());
    }
}