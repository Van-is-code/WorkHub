package gr3.workhub.service;

import gr3.workhub.dto.NotificationDTO;
import gr3.workhub.entity.Notification;
import gr3.workhub.entity.User;
import gr3.workhub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationDTO createNotification(Integer userId, String message) {
        Notification notification = new Notification();
        notification.setUser(new User(userId));
        notification.setMessage(message);
        Notification savedNotification = notificationRepository.save(notification);

        return new NotificationDTO(
                savedNotification.getUser().getId(),
                savedNotification.getMessage(),
                savedNotification.getIsRead(),
                savedNotification.getCreatedAt()
        );
    }

    public List<NotificationDTO> getNotificationsForUser(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getUser().getId(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public void markNotificationAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}