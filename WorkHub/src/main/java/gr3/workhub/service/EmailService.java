package gr3.workhub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Account Activation");
            message.setText("Click the link to activate: http://localhost:8080/workhub/api/v1/activate?token=" + token);
            mailSender.send(message);
            logger.info("Activation email sent to {}", to);
        } catch (MailException e) {
            logger.error("Failed to send activation email to {}: {}", to, e.getMessage());
        }
    }

    public void sendinterview(String to, String subject, String bodyHtml) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyHtml, true); // true => HTML content

            mailSender.send(message);
            logger.info("Interview email sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send interview email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }


    public void sendJoinLinkEmail(String to, String jobTitle, String startTime, String joinLink) {
        String subject = "Interview Join Link";
        String body = "Your interview for job: " + jobTitle +
                " is scheduled at " + startTime + ".\n" +
                "You can join the interview using this link:\n" + joinLink;
        sendinterview(to, subject, body);
    }

    public void sendResetPasswordEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Password Reset");
            message.setText("Click the link to reset your password: http://localhost:8080/workhub/api/v1/reset-password?token=" + token);
            mailSender.send(message);
            logger.info("Password reset email sent to {}", to);
        } catch (MailException e) {
            logger.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }
}