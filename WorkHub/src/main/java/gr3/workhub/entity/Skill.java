package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skills")
@Schema(description = "Kỹ năng liên quan đến công việc hoặc hồ sơ ứng viên.")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của kỹ năng", example = "1")
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Tên kỹ năng", example = "Java")
    private String name;

    @Schema(description = "Mô tả chi tiết về kỹ năng", example = "Lập trình hướng đối tượng với Java.")
    private String description;
}
