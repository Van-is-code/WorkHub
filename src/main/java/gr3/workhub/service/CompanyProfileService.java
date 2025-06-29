package gr3.workhub.service;


import gr3.workhub.dto.CompanyProfileDTO;
import gr3.workhub.entity.CompanyProfile;
import gr3.workhub.entity.User;
import gr3.workhub.repository.CompanyProfileRepository;
import gr3.workhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// src/main/java/gr3/workhub/service/CompanyProfileService.java
@Service
@RequiredArgsConstructor
public class CompanyProfileService {
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;

    public CompanyProfile createCompanyProfile(Integer userId, CompanyProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CompanyProfile company = new CompanyProfile();
        company.setRecruiter(user);
        company.setName(dto.getName());
        company.setIndustry(dto.getIndustry());
        company.setLocation(dto.getLocation());
        company.setDescription(dto.getDescription());
        company.setWebsite(dto.getWebsite());
        company.setLogoUrl(dto.getLogoUrl());
        company.setInspectionStatus(CompanyProfile.InspectionStatus.none);
        company.setInspection(CompanyProfile.Inspection.valueOf(dto.getInspection()));
        company.setStatus(CompanyProfile.Status.valueOf(dto.getStatus()));
        company.setCreatedAt(LocalDateTime.now());
        return companyProfileRepository.save(company);
    }
    public CompanyProfile getCompanyProfileById(Integer id) {
        return companyProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
    }

    // src/main/java/gr3/workhub/service/CompanyProfileService.java
    public void updateStatus(Integer id, CompanyProfile.Status status) {
        CompanyProfile company = getCompanyProfileById(id);
        company.setStatus(status);
        companyProfileRepository.save(company);
    }

    public void updateInspection(Integer id, CompanyProfile.Inspection inspection) {
        CompanyProfile company = getCompanyProfileById(id);
        company.setInspection(inspection);
        companyProfileRepository.save(company);
    }

    public List<CompanyProfile> getAllCompanyProfiles() {
        return companyProfileRepository.findAll();
    }
    public CompanyProfile updateCompanyProfile(Integer id, CompanyProfileDTO dto) {
        CompanyProfile company = getCompanyProfileById(id);
        if (dto.getRecruiterId() != null) {
            User recruiter = userRepository.findById(dto.getRecruiterId())
                    .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));
            company.setRecruiter(recruiter);
        }
        if (dto.getName() != null) company.setName(dto.getName());
        if (dto.getIndustry() != null) company.setIndustry(dto.getIndustry());
        if (dto.getLocation() != null) company.setLocation(dto.getLocation());
        if (dto.getDescription() != null) company.setDescription(dto.getDescription());
        if (dto.getWebsite() != null) company.setWebsite(dto.getWebsite());
        if (dto.getLogoUrl() != null) company.setLogoUrl(dto.getLogoUrl());
        if (dto.getInspection() != null) company.setInspection(CompanyProfile.Inspection.valueOf(dto.getInspection()));
        if (dto.getStatus() != null) company.setStatus(CompanyProfile.Status.valueOf(dto.getStatus()));
        if (dto.getInspectionStatus() != null) company.setInspectionStatus(CompanyProfile.InspectionStatus.valueOf(dto.getInspectionStatus()));
        return companyProfileRepository.save(company);
    }

    public void deleteCompanyProfile(Integer id) {
        companyProfileRepository.deleteById(id);
    }

    public List<CompanyProfile> getCompanyProfilesByRecruiter(User recruiter) {
        return companyProfileRepository.findByRecruiter(recruiter);
    }
}