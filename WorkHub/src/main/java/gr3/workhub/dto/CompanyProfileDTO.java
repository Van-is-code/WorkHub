package gr3.workhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileDTO {

    private Integer recruiterId;       // Refers to User
    private String name;
    private String industry;
    private String location;
    private String description;
    private String website;
    private String logo;
    private String inspectionStatus;   // Enum: none, pending, approved, rejected
    private String inspection;         // Enum: none, prestige
    private String status;             // Enum: active, pending, inactive
    private LocalDateTime createdAt;
}