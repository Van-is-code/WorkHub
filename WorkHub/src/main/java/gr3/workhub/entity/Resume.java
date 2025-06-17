package gr3.workhub.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resumes")
@Schema(description = "Thông tin hồ sơ xin việc (CV) của ứng viên")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của hồ sơ", example = "101")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Người dùng sở hữu hồ sơ")
    private User user;

    @Column(nullable = false)
    @Schema(description = "Tiêu đề của hồ sơ", example = "CV Java Developer")
    private String title;

    @Column(nullable = false)
    @Schema(description = "Mô tả nội dung chính của hồ sơ", example = "Kinh nghiệm: 3 năm Java Spring Boot...")
    private String content;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Schema(description = "File hồ sơ ở dạng nhị phân (PDF, DOC, v.v.)")
    public byte[] file;

    @Column(name = "is_generated", nullable = false)
    @Schema(description = "Đánh dấu hồ sơ này là do hệ thống tạo ra hay không", example = "true")
    private Boolean isGenerated = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Ngày tạo hồ sơ", example = "2025-05-26T08:30:00")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "resume_skills",
            joinColumns = @JoinColumn(name = "resume_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Schema(description = "Danh sách kỹ năng liên kết với hồ sơ")
    private List<Skill> skills = new ArrayList<>();
}
