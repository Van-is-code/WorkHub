// src/main/java/gr3/workhub/dto/AppliedJobsDTO.java
package gr3.workhub.dto;

import java.time.LocalDateTime;

public class AppliedJobsDTO {
    private Integer applicationId;
    private SimpleJobDTO job;
    private String resumeTitle;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime interviewTime;

    public AppliedJobsDTO(Integer applicationId, SimpleJobDTO job, String resumeTitle, String status, LocalDateTime appliedAt, LocalDateTime interviewTime) {
        this.applicationId = applicationId;
        this.job = job;
        this.resumeTitle = resumeTitle;
        this.status = status;
        this.appliedAt = appliedAt;
        this.interviewTime = interviewTime;
    }

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public SimpleJobDTO getJob() { return job; }
    public void setJob(SimpleJobDTO job) { this.job = job; }

    public String getResumeTitle() { return resumeTitle; }
    public void setResumeTitle(String resumeTitle) { this.resumeTitle = resumeTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalDateTime interviewTime) { this.interviewTime = interviewTime; }
}