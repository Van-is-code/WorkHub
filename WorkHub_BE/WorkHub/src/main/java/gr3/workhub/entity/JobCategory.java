package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_categories")
@Schema(description = "Danh mục ngành nghề công việc")
public class JobCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của danh mục ngành nghề", example = "1")
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Tên danh mục ngành nghề", example = "Công nghệ thông tin")
    private String name;

    @Schema(description = "Mô tả chi tiết về ngành nghề", example = "Ngành liên quan đến lập trình, quản trị hệ thống, an ninh mạng,...")
    private String description;
}
