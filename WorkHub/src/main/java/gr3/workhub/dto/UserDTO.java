package gr3.workhub.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String fullname;
    private String email;
    private String phone;
    private byte[] avatar;
}