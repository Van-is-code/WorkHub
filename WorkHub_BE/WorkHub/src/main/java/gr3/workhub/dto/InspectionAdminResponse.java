package gr3.workhub.dto;

import gr3.workhub.entity.CompanyProfile;
import gr3.workhub.entity.Inspection;
import gr3.workhub.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspectionAdminResponse {
    private Integer id;
    private String fullName;
    private String phone;
    private String email;
    private String companyName;
    private String logo;
    private LocalDateTime sentAt;
    private LocalDateTime reviewedAt;
    private CompanyProfile.Status companyStatus;
    private byte[] businessLicense;
    private String taxCode;
    private String subLicense;

    public InspectionAdminResponse(Integer id, String fullName, String phone, String email, String companyName,
                                   String logo, LocalDateTime sentAt, LocalDateTime reviewedAt,
                                   CompanyProfile.Status companyStatus, byte[] businessLicense,
                                   String taxCode, String subLicense) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.companyName = companyName;
        this.logo = logo;
        this.sentAt = sentAt;
        this.reviewedAt = reviewedAt;
        this.companyStatus = companyStatus;
        this.businessLicense = businessLicense;
        this.taxCode = taxCode;
        this.subLicense = subLicense;
    }


    public static InspectionAdminResponse from(Inspection i) {
        User sender = i.getSender();
        CompanyProfile cp = i.getCompanyProfile();
        String logoUrl = cp.getLogoUrl();
        return new InspectionAdminResponse(
                i.getId(),
                sender.getFullname(),
                sender.getPhone(),
                sender.getEmail(),
                cp.getName(),
                logoUrl,
                i.getSentAt(),
                i.getReviewedAt(),
                cp.getStatus(),
                i.getBusinessLicense(),
                i.getTaxCode(),
                i.getSubLicense()
        );
    }
}