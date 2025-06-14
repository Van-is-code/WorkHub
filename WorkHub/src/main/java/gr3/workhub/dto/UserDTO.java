package gr3.workhub.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    public String fullname;
    public String email;
    public String phone;
    public String avatar;
}