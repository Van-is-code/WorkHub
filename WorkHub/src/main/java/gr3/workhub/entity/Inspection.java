package gr3.workhub.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inspections")
@Schema(description = "Thông tin yêu cầu xét duyệt hồ sơ công ty")
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID yêu cầu xét duyệt", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Người gửi yêu cầu xét duyệt")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_profile_id", nullable = false)
    @Schema(description = "Hồ sơ công ty cần xét duyệt")
    private CompanyProfile companyProfile;

    @Lob
    @Column(name = "business_license", nullable = false)
    @Schema(description = "File giấy phép kinh doanh (GPKD)", type = "string", format = "byte")
    private byte[] businessLicense;

    @Column(name = "tax_code", nullable = false)
    @Schema(description = "Mã số thuế", example = "123456789")
    private String taxCode;

    @Column(name = "sub_license")
    @Schema(description = "Giấy phép con (có thể null)", example = "GP123")
    private String subLicense;

    @Column(name = "inspection_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Trạng thái xét duyệt", example = "pending")
    private InspectionStatus inspectionStatus = InspectionStatus.pending;

    public enum InspectionStatus {
        @Schema(description = "Chờ xét duyệt")
        pending,
        @Schema(description = "Đã duyệt")
        approved,
        @Schema(description = "Từ chối")
        rejected
    }

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    @Schema(description = "Thời gian gửi yêu cầu", example = "2024-06-15T10:00:00")
    private java.time.LocalDateTime sentAt;

    @Column(name = "reviewed_at")
    @Schema(description = "Thời gian xét duyệt", example = "2024-06-16T10:00:00")
    private java.time.LocalDateTime reviewedAt;
}