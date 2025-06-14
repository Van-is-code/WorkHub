package gr3.workhub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inspections")
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User who submits the inspection
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User sender;

    // Company profile to be inspected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_profile_id", nullable = false)
    private CompanyProfile companyProfile;

    // Legal documents
    @Lob
    @Column(name = "business_license", nullable = false)
    private byte[] businessLicense; // GPKD file

    @Column(name = "tax_code", nullable = false)
    private String taxCode; // Mã số thuế

    @Column(name = "sub_license")
    private String subLicense; // Giấy phép con (nullable)

    @Column(name = "inspection_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InspectionStatus inspectionStatus = InspectionStatus.pending;
    public enum InspectionStatus {
        pending, approved, rejected
    }


    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private java.time.LocalDateTime sentAt;

    @Column(name = "reviewed_at")
    private java.time.LocalDateTime reviewedAt;

}