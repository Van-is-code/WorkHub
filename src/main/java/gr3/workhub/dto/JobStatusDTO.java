package gr3.workhub.dto;

import lombok.Data;

@Data
public class JobStatusDTO {
    private Integer jobId;
    private String status; // e.g. "closed"
}
