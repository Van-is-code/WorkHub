package gr3.workhub.dto;

import lombok.Data;

@Data
public class InspectionRequest {
    private Integer companyProfileId;
    private byte[] businessLicense;
    private String taxCode;
    private String subLicense;
}