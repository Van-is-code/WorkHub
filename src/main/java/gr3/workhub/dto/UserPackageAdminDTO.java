package gr3.workhub.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPackageAdminDTO {
    private Integer id;
    private Integer userId;
    private String fullname;
    private String email;
    private String role;
    private Integer servicePackageId;
    private String servicePackageName;
    private String servicePackageDescription;
    private Double price;
    private String status;
    private LocalDateTime purchaseDate;
    private LocalDateTime expirationDate;
}
