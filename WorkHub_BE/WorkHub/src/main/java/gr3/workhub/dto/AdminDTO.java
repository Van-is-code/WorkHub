package gr3.workhub.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    private String fullname;
    private String email;
    private String phone;
    private String role;   // Enum: super_admin, moderator, support
    private Boolean active;
}