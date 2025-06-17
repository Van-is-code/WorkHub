package gr3.workhub.service;

import gr3.workhub.entity.Resume;
import gr3.workhub.entity.Skill;
import gr3.workhub.entity.User;
import gr3.workhub.repository.ResumeRepository;
import gr3.workhub.repository.SkillRepository;
import gr3.workhub.repository.UserRepository;
import gr3.workhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

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
}