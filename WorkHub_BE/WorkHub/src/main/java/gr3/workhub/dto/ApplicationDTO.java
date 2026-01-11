package gr3.workhub.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationDTO {
    private Integer id;
    private String jobTitle;
    private String userFullname;
    private String userEmail;
    private String userPhone;
    private String status;
    private LocalDateTime appliedAt;
    private byte[] resumeFile;
    private Integer resumeId;
    private String resumeContent; // Thêm trường mô tả CV

    public ApplicationDTO(Integer id, String jobTitle, String userFullname, String userEmail, String userPhone,
                          String status, LocalDateTime appliedAt, byte[] resumeFile, Integer resumeId, String resumeContent) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.userFullname = userFullname;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.status = status;
        this.appliedAt = appliedAt;
        this.resumeFile = resumeFile;
        this.resumeId = resumeId;
        this.resumeContent = resumeContent;
    }
    public ApplicationDTO(String jobTitle, String userFullname, String userEmail, String userPhone,
                          String status, LocalDateTime appliedAt, byte[] resumeFile, Integer resumeId, String resumeContent) {
        this(null, jobTitle, userFullname, userEmail, userPhone, status, appliedAt, resumeFile, resumeId, resumeContent);
    }
    public ApplicationDTO(String jobTitle, String userFullname, String userEmail, String userPhone,
                          String status, LocalDateTime appliedAt, byte[] resumeFile) {
        this(null, jobTitle, userFullname, userEmail, userPhone, status, appliedAt, resumeFile, null, null);
    }
}