package gr3.workhub.scheduler;

import gr3.workhub.entity.InterviewSlot;
import gr3.workhub.repository.InterviewSlotRepository;
import gr3.workhub.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class InterviewSlotMailScheduler {
    @Autowired
    private InterviewSlotRepository slotRepo;
    @Autowired
    private EmailService emailService;

    // Chạy mỗi phút, gửi mail cho slot bắt đầu đúng thời điểm
    @Scheduled(cron = "0 * * * * *")
    public void sendMailAtSlotStart() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        List<InterviewSlot> slots = slotRepo.findByStartTime(now);
        for (InterviewSlot slot : slots) {
            if (slot.getCandidate() != null) {
                String joinLink = "http://localhost:8080/workhub/api/v1/interview-sessions/join/" + slot.getInterviewSession().getTokenCandidate();
                String body = "<html><body>Bạn có lịch phỏng vấn cho công việc: <b>" + slot.getJob().getTitle() + "</b>.<br>" +
                        "Tham gia phỏng vấn tại: <a href='" + joinLink + "'>Link Meet</a></body></html>";
                emailService.sendinterview(slot.getCandidate().getEmail(), "Đến giờ phỏng vấn - WorkHub", body);
            }
        }
    }
}
