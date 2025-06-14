package gr3.workhub.repository;

import gr3.workhub.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findBySenderIdOrReceiverId(Integer senderId, Integer receiverId);
}