package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs")
@Schema(description = "Thông tin chi tiết của một công việc được đăng tuyển")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID của công việc", example = "101")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    @Schema(description = "Nhà tuyển dụng đã đăng tin")
    private User recruiter;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "Ngành nghề của công việc")
    private JobCategory category;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    @Schema(description = "Loại công việc: toàn thời gian, bán thời gian, v.v.")
    private JobType type;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    @Schema(description = "Vị trí công việc: ví dụ như Junior Developer, Manager")
    private JobPosition position;

    @Column(nullable = false)
    @Schema(description = "Tiêu đề công việc", example = "Java Backend Developer")
    private String title;

    @Lob
    @Column(nullable = false)
    @Schema(description = "Mô tả chi tiết công việc", example = "Phát triển RESTful API cho hệ thống nội bộ")
    private String description;

    @Schema(description = "Mức lương dự kiến", example = "$1000 - $1500")
    private String salaryRange;

    @Column(nullable = false)
    @Schema(description = "Yêu cầu kinh nghiệm", example = "2 năm kinh nghiệm phát triển Java")
    private String experience;

    @Schema(description = "Địa điểm làm việc", example = "Hà Nội, Việt Nam")
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Thời điểm đăng tin", example = "2025-05-26T08:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Hạn nộp hồ sơ", example = "2025-06-10T23:59:59")
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Hình thức đăng bài: tiêu chuẩn, đề xuất, khẩn cấp", example = "standard")
    private PostAt postAt = PostAt.standard;

    @ManyToMany
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Schema(description = "Danh sách kỹ năng yêu cầu cho công việc")
    private List<Skill> skills = new ArrayList<>();

    public enum PostAt {
        @Schema(description = "Bài đăng đề xuất hiển thị nổi bật")
        proposal,

        @Schema(description = "Bài đăng khẩn cấp cần tuyển gấp")
        urgent,

        @Schema(description = "Bài đăng thông thường")
        standard
    }

    public Job(Integer id) {
        this.id = id;
    }
}
