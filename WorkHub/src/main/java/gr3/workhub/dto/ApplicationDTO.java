package gr3.workhub.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationDTO {
    private String jobTitle;
    private String userFullname;
    private String userEmail;
    private String userPhone;
    private String status;
    private LocalDateTime appliedAt;
    private byte[] resumeFile;

    public ApplicationDTO(String jobTitle, String userFullname, String userEmail, String userPhone,
                          String status, LocalDateTime appliedAt, byte[] resumeFile) {
        this.jobTitle = jobTitle;
        this.userFullname = userFullname;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.status = status;
        this.appliedAt = appliedAt;
        this.resumeFile = resumeFile;
    }
}