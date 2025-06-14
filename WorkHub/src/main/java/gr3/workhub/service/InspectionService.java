package gr3.workhub.service;

import gr3.workhub.dto.InspectionRequest;
import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspectionService {
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    // src/main/java/gr3/workhub/service/InspectionService.java
    public void updateInspectionStatus(Integer inspectionId, Inspection.InspectionStatus status) {
        Inspection inspection = inspectionRepository.findById(inspectionId).orElseThrow();
        inspection.setInspectionStatus(status);
        inspection.setReviewedAt(java.time.LocalDateTime.now());
        inspectionRepository.save(inspection);

        CompanyProfile company = inspection.getCompanyProfile();
        // Map Inspection.InspectionStatus to CompanyProfile.InspectionStatus
        company.setInspectionStatus(CompanyProfile.InspectionStatus.valueOf(status.name()));
        companyProfileRepository.save(company);
    }

    public Inspection createInspection(Integer userId, InspectionRequest request) {
        User sender = userRepository.findById(userId).orElseThrow();
        CompanyProfile company = companyProfileRepository.findById(request.getCompanyProfileId()).orElseThrow();

        Inspection inspection = new Inspection();
        inspection.setSender(sender);
        inspection.setCompanyProfile(company);
        inspection.setBusinessLicense(request.getBusinessLicense());
        inspection.setTaxCode(request.getTaxCode());
        inspection.setSubLicense(request.getSubLicense());
        inspection.setInspectionStatus(Inspection.InspectionStatus.pending);

        // Set company profile inspectionStatus to pending
        company.setInspectionStatus(CompanyProfile.InspectionStatus.pending);
        companyProfileRepository.save(company);

        return inspectionRepository.save(inspection);
    }

    public List<Inspection> getInspectionsByUser(Integer userId) {
        return inspectionRepository.findBySenderId(userId);
    }

    public List<Inspection> getInspectionsByStatus(Inspection.InspectionStatus status) {
        return inspectionRepository.findByInspectionStatus(status);
    }
}