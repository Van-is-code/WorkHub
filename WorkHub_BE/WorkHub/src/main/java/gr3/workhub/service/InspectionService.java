package gr3.workhub.service;

import gr3.workhub.dto.InspectionRequest;
import gr3.workhub.entity.*;
import gr3.workhub.repository.*;
import gr3.workhub.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;



@Service
public class InspectionService {
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyProfileRepository companyProfileRepository;
    @Autowired
    private TokenService tokenService;
;



    public void updateInspectionStatus(Integer inspectionId, Inspection.InspectionStatus status) {
        Inspection inspection = inspectionRepository.findById(inspectionId).orElseThrow();
        inspection.setInspectionStatus(status);
        inspection.setReviewedAt(java.time.LocalDateTime.now());
        inspectionRepository.save(inspection);

        CompanyProfile company = inspection.getCompanyProfile();
        company.setInspectionStatus(CompanyProfile.InspectionStatus.valueOf(status.name()));
        companyProfileRepository.save(company);
    }

    public Inspection createInspection(HttpServletRequest request, InspectionRequest inspectionRequest) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        User sender = userRepository.findById(userId).orElseThrow();
        CompanyProfile company = companyProfileRepository.findById(inspectionRequest.getCompanyProfileId()).orElseThrow();

        Inspection inspection = new Inspection();
        inspection.setSender(sender);
        inspection.setCompanyProfile(company);
        inspection.setBusinessLicense(inspectionRequest.getBusinessLicense());
        inspection.setTaxCode(inspectionRequest.getTaxCode());
        inspection.setSubLicense(inspectionRequest.getSubLicense());
        inspection.setInspectionStatus(Inspection.InspectionStatus.pending);

        company.setInspectionStatus(CompanyProfile.InspectionStatus.pending);
        companyProfileRepository.save(company);

        return inspectionRepository.save(inspection);
    }

    public List<Inspection> getInspectionsByUser(HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        return inspectionRepository.findBySenderId(userId);
    }

    public List<Inspection> getInspectionsByStatus(Inspection.InspectionStatus status) {
        return inspectionRepository.findByInspectionStatus(status);
    }
}