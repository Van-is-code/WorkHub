package gr3.workhub.service;

import gr3.workhub.entity.Application;
import gr3.workhub.entity.Resume;
import gr3.workhub.entity.Skill;
import gr3.workhub.entity.User;
import gr3.workhub.entity.ResumeView;
import gr3.workhub.repository.ApplicationRepository;
import gr3.workhub.repository.ResumeRepository;
import gr3.workhub.repository.SkillRepository;
import gr3.workhub.repository.UserRepository;
import gr3.workhub.repository.ResumeViewRepository;
import gr3.workhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ApplicationRepository applicationRepository;
    private final UserBenefitsService userBenefitsService;
    private final ResumeViewRepository resumeViewRepository;

    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    public Resume createResume(Resume resume, HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        resume.setUser(user);
        resume.setCreatedAt(LocalDateTime.now());

        List<Skill> skills = resume.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList());
        resume.setSkills(skills);

        return resumeRepository.save(resume);
    }

    public Resume toggleIsGenerated(Integer resumeId, HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        Resume resume = resumeRepository.findByIdAndUser_Id(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or unauthorized"));
        resume.setIsGenerated(!Boolean.TRUE.equals(resume.getIsGenerated()));
        return resumeRepository.save(resume);
    }

    public Resume updateResume(Integer resumeId, Resume resume, HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        Resume existingResume = resumeRepository.findByIdAndUser_Id(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or unauthorized"));

        List<Skill> skills = resume.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList());

        existingResume.setTitle(resume.getTitle());
        existingResume.setContent(resume.getContent());
        existingResume.setFile(resume.getFile());
        existingResume.setSkills(skills);

        return resumeRepository.save(existingResume);
    }

    public void deleteResume(Integer resumeId, HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);

        Resume resume = resumeRepository.findByIdAndUser_Id(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or unauthorized"));

        resumeRepository.delete(resume);
    }

    public String getResumeFileAsImage(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        byte[] file = resume.getFile();
        if (file == null) return null;
        // Default to PNG, adjust if you store other image types
        String mimeType = "image/png";
        return "data:" + mimeType + ";base64," + java.util.Base64.getEncoder().encodeToString(file);
    }

    public List<Resume> getResumesByUserId(HttpServletRequest request) {
        Integer userId = tokenService.extractUserIdFromRequest(request);
        return resumeRepository.findAll().stream()
                .filter(resume -> resume.getUser().getId().equals(userId))
                .toList();
    }

    public String getResumeFileBase64(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        return java.util.Base64.getEncoder().encodeToString(resume.getFile());
    }

    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }

    public Resume createResumeForUser(Resume resume, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        resume.setUser(user);
        resume.setCreatedAt(LocalDateTime.now());
        if (resume.getSkills() != null) {
            List<Skill> skills = resume.getSkills().stream()
                    .map(skill -> skillRepository.findById(skill.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                    .collect(Collectors.toList());
            resume.setSkills(skills);
        }
        return resumeRepository.save(resume);
    }

    @Transactional
    public String getResumeFileBase64WithQuotaCheck(Integer resumeId, jakarta.servlet.http.HttpServletRequest request) {
        Integer userId;
        String role;
        try {
            userId = tokenService.extractUserIdFromRequest(request);
            role = tokenService.extractUserRoleFromRequest(request);
        } catch (Exception e) {
            log.error("[DEBUG] Không xác thực được người dùng khi xem CV!", e);
            throw new RuntimeException("Không xác thực được người dùng.");
        }
        log.info("[DEBUG] VÀO HÀM getResumeFileBase64WithQuotaCheck: userId={}, resumeId={}, role={}", userId, resumeId, role);
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        if ("ADMIN".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role)) {
            // Admin luôn được phép xem CV
            return java.util.Base64.getEncoder().encodeToString(resume.getFile());
        }
        if ("CANDIDATE".equalsIgnoreCase(role)) {
            // Chỉ cho xem CV của chính mình
            if (!resume.getUser().getId().equals(userId)) {
                log.info("[DEBUG] CANDIDATE KHÔNG PHẢI CHỦ CV: userId={}, resumeUserId={}", userId, resume.getUser().getId());
                throw new RuntimeException("Bạn không có quyền xem CV này.");
            }
            return java.util.Base64.getEncoder().encodeToString(resume.getFile());
        } else if ("RECRUITER".equalsIgnoreCase(role)) {
            final Integer recruiterId = userId;
            java.util.List<Application> apps = applicationRepository.findByResumeId(resumeId);
            log.info("[DEBUG] recruiterId={}, resumeId={}, apps.size={}", recruiterId, resumeId, apps.size());
            for (Application app : apps) {
                Integer jobId = app.getJob() != null ? app.getJob().getId() : null;
                Integer appRecruiterId = (app.getJob() != null && app.getJob().getRecruiter() != null) ? app.getJob().getRecruiter().getId() : null;
                log.info("[DEBUG] AppId={}, JobId={}, RecruiterId={}", app.getId(), jobId, appRecruiterId);
            }
            Application application = apps.stream()
                .filter(app -> app.getJob() != null && app.getJob().getRecruiter().getId().equals(recruiterId))
                .findFirst()
                .orElse(null);
            if (application == null || application.getJob() == null) {
                log.info("[DEBUG] Không tìm thấy application phù hợp cho recruiterId này!");
                throw new RuntimeException("Bạn không phải chủ job này hoặc ứng viên chưa apply vào job của bạn.");
            }
            // Kiểm tra recruiter đã từng xem CV này chưa
            boolean hasViewed = resumeViewRepository.existsByResume_IdAndRecruiter_Id(resumeId, recruiterId);
            if (!hasViewed) {
                // Kiểm tra quota như cũ
                java.util.List<gr3.workhub.entity.UserBenefits> allBenefits = userBenefitsService.getAllBenefitsByUserId(userId);
                gr3.workhub.entity.UserBenefits userBenefits = allBenefits.stream()
                    .filter(ub -> ub.getCvLimit() != null && ub.getCvLimit() > 0
                            && ub.getUserPackage() != null && ub.getUserPackage().getExpirationDate() != null && ub.getUserPackage().getExpirationDate().isAfter(java.time.LocalDateTime.now())
                    )
                    .findFirst().orElse(null);
                if (userBenefits == null) {
                    log.info("[DEBUG] recruiterId={} đã hết quota xem CV!", recruiterId);
                    throw new RuntimeException("Bạn đã xem hết số lượng CV cho phép của các gói dịch vụ.");
                }
                log.info("[DEBUG] recruiterId={} còn quota, cvLimit={} trước khi trừ", recruiterId, userBenefits.getCvLimit());
                userBenefits.setCvLimit(userBenefits.getCvLimit() - 1);
                userBenefitsService.saveOrUpdateUserBenefits(
                    userId,
                    userBenefits.getUserPackage().getId(),
                    userBenefits.getPostAt(),
                    userBenefits.getJobPostLimit(),
                    userBenefits.getCvLimit(),
                    userBenefits.getDescription()
                );
                log.info("[DEBUG] recruiterId={} đã trừ quota, cvLimit={} sau khi trừ", recruiterId, userBenefits.getCvLimit());
                // Lưu lượt xem mới
                ResumeView view = new ResumeView();
                view.setResume(resume);
                view.setRecruiter(userRepository.findById(recruiterId).orElseThrow());
                resumeViewRepository.save(view);
            }
            return java.util.Base64.getEncoder().encodeToString(resume.getFile());
        } else {
            log.info("[DEBUG] Vai trò không hợp lệ: role={}", role);
            throw new RuntimeException("Bạn không có quyền xem CV này.");
        }
    }
}