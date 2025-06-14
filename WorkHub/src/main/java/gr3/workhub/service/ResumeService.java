package gr3.workhub.service;

import gr3.workhub.dto.ApplicationDTO;
import gr3.workhub.entity.Resume;
import gr3.workhub.entity.Skill;
import gr3.workhub.entity.User;
import gr3.workhub.repository.ResumeRepository;
import gr3.workhub.repository.SkillRepository;
import gr3.workhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Base64;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public Resume createResume(Resume resume, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        resume.setUser(user);
        resume.setCreatedAt(LocalDateTime.now());

        List<Skill> skills = resume.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList());
        resume.setSkills(skills);

        // Trường file đã là byte[], không cần xử lý thêm
        return resumeRepository.save(resume);
    }

    public Resume toggleIsGenerated(Integer resumeId, Integer userId) {
        Resume resume = resumeRepository.findByIdAndUser_Id(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or unauthorized"));
        resume.setIsGenerated(!Boolean.TRUE.equals(resume.getIsGenerated()));
        return resumeRepository.save(resume);
    }

    public Resume updateResume(Integer resumeId, Resume resume, Integer userId) {
        Resume existingResume = resumeRepository.findByIdAndUser_Id(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or unauthorized"));

        List<Skill> skills = resume.getSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skill.getId())))
                .collect(Collectors.toList());

        existingResume.setTitle(resume.getTitle());
        existingResume.setContent(resume.getContent());
        existingResume.setFile(resume.getFile()); // Cập nhật file kiểu byte[]
        existingResume.setSkills(skills);

        return resumeRepository.save(existingResume);
    }

    public void deleteResume(Integer resumeId, Integer userId) {
        // Ensure only user with userId = 1 can delete resumes
        if (!userId.equals(1)) {
            throw new IllegalArgumentException("Only the user with ID 1 can delete resumes");
        }

        // Validate resume existence
        Resume resume = resumeRepository.findByIdAndUser_Id(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or unauthorized"));

        // Delete the resume
        resumeRepository.delete(resume);
    }

    public List<Resume> getResumesByUserId(Integer userId) {
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