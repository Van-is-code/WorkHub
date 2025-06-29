package gr3.workhub.dto;

import lombok.Data;

@Data
public class ResumeReviewDTO {
    private Integer resumeId;
    private Integer candidateId;
    private Integer recruiterId;
    private Integer rating;
    private String comment;
}