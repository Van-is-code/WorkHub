// src/main/java/gr3/workhub/dto/AppliedJobsDTO.java
package gr3.workhub.dto;

import java.time.LocalDateTime;

public class AppliedJobsDTO {
    private String jobTitle;
    private String salaryRange;
    private byte[] resumeFile;
    private String status;
    private LocalDateTime appliedAt;

    public AppliedJobsDTO(String jobTitle, String salaryRange, byte[] resumeFile, String status, LocalDateTime appliedAt) {
        this.jobTitle = jobTitle;
        this.salaryRange = salaryRange;
        this.resumeFile = resumeFile;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    // Getters and setters
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public byte[] getResumeFile() { return resumeFile; }
    public void setResumeFile(byte[] resumeFile) { this.resumeFile = resumeFile; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}