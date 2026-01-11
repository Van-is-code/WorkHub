package gr3.workhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Integer userId;            // Refers to User
    private Integer packageId;         // Refers to ServicePackage
    private Double amount;
    private String status;             // Enum: pending, completed, failed
    private String description;
    private LocalDateTime transactionDate;
}