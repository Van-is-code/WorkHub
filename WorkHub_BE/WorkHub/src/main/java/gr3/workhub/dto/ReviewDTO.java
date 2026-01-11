package gr3.workhub.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Integer companyId;
    private Integer userId;
    private Integer rating;
    private String comment;
}