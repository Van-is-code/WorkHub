package gr3.workhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ResumeMetadata {
    public String fullName;
    public String phone;
    public Integer resumeId;
    public Integer userId;
}