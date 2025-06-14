package gr3.workhub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company_profiles")
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @Column(nullable = false)
    private String name;

    private String industry;

    private String location;

    private String description;

    private String website;

    private String logo;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status", nullable = false)
    private InspectionStatus inspectionStatus = InspectionStatus.pending;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Inspection inspection = Inspection.none;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum InspectionStatus {
        none, pending, approved, rejected
    }

    public enum Inspection {
        none, prestige
    }

    public enum Status {
        active, pending, inactive
    }
}