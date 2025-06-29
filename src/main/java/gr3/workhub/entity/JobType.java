package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_types")
@Schema(description = "Loại hình công việc trong hệ thống (Toàn thời gian, Bán thời gian, Tự do, v.v.)")
public class JobType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của loại công việc", example = "1")
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Tên loại công việc", example = "Full-time")
    private String name;

    @Lob
    @Schema(description = "Mô tả chi tiết về loại công việc", example = "Làm việc toàn thời gian từ thứ 2 đến thứ 6, thời gian làm việc cố định...")
    private String description;
}
