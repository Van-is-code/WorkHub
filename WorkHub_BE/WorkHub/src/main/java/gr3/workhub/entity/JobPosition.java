package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_positions")
@Schema(description = "Vị trí công việc trong hệ thống")
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của vị trí công việc", example = "1")
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Tên vị trí công việc", example = "Backend Developer")
    private String name;

    @Lob
    @Schema(description = "Mô tả chi tiết cho vị trí công việc", example = "Phát triển các API phía server, tích hợp với hệ thống cơ sở dữ liệu,...")
    private String description;
}
