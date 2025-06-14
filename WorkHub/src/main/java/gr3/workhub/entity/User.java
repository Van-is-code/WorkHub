package gr3.workhub.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@Schema(description = "Thông tin người dùng trong hệ thống.")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID người dùng", example = "1")
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Họ và tên người dùng", example = "Nguyễn Văn A")
    private String fullname;

    @Column(nullable = false, unique = true)
    @Schema(description = "Địa chỉ email (duy nhất)", example = "user@example.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Mật khẩu đã được mã hóa", example = "hashed_password_here")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Vai trò của người dùng: candidate hoặc recruiter", example = "candidate")
    private Role role;

    @Schema(description = "Số điện thoại liên hệ", example = "0912345678")
    private String phone;

    @Schema(description = "URL ảnh đại diện", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Trạng thái tài khoản", example = "verified")
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Thời điểm người dùng được tạo", example = "2024-05-01T12:00:00")
    private LocalDateTime createdAt;

    // Mối quan hệ 1:N từ User -> Resume
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference  // Đảm bảo resumeList được serialize
    @Schema(description = "Danh sách hồ sơ (CV) do người dùng tạo")
    private List<Resume> resumeList = new ArrayList<>();

    // Constructor tiện dụng nếu chỉ cần truyền ID
    public User(Integer id) {
        this.id = id;
    }

    @Schema(description = "Enum thể hiện vai trò của người dùng")
    public enum Role {
        candidate, // Ứng viên
        recruiter  // Nhà tuyển dụng
    }

    @Schema(description = "Enum thể hiện trạng thái tài khoản")
    public enum Status {
        unverified, // Chưa xác thực
        verified,   // Đã xác thực
        suspended,  // Bị tạm ngưng
        banned      // Bị cấm
    }
}
