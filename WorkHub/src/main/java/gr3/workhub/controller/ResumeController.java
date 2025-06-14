package gr3.workhub.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import gr3.workhub.entity.Resume;
import gr3.workhub.repository.SkillRepository;
import gr3.workhub.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "✅Resume", description = "Quản lý hồ sơ (CV) của ứng viên")
@RestController
@CrossOrigin

@RequestMapping("/workhub/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final SkillRepository skillRepository;

    @Operation(
            summary = "<EndPoint cho trang của ứng viên>Tạo hồ sơ mới",
            description = "Tải lên file CV và thông tin kèm theo để tạo hồ sơ mới cho người dùng."
    )
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/{userId}")
    public ResponseEntity<Resume> createResume(
            @Parameter(description = "File CV (pdf, doc,...)") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Tiêu đề hồ sơ") @RequestParam("title") String title,
            @Parameter(description = "Nội dung mô tả hồ sơ") @RequestParam("content") String content,
            @Parameter(description = "Danh sách ID kỹ năng") @RequestParam("skillIds") List<Integer> skillIds,
            @Parameter(description = "ID người dùng") @PathVariable Integer userId) throws IOException {

        Resume resume = new Resume();
        resume.setFile(file.getBytes());
        resume.setTitle(title);
        resume.setContent(content);
        resume.setSkills(skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skillId)))
                .collect(Collectors.toList()));
        Resume createdResume = resumeService.createResume(resume, userId);
        return ResponseEntity.ok(createdResume);
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên>Cập nhật hồ sơ",
            description = "Cập nhật file, tiêu đề, nội dung và kỹ năng của một hồ sơ hiện có."
    )
    @PreAuthorize("hasRole('CANDIDATE')")
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<Resume> updateResume(
            @Parameter(description = "ID hồ sơ") @PathVariable Integer id,
            @Parameter(description = "File CV mới") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Tiêu đề mới") @RequestParam("title") String title,
            @Parameter(description = "Nội dung mới") @RequestParam("content") String content,
            @Parameter(description = "Danh sách kỹ năng mới") @RequestParam("skillIds") List<Integer> skillIds,
            @Parameter(description = "ID người dùng") @PathVariable Integer userId) throws IOException {

        Resume resume = new Resume();
        resume.setFile(file.getBytes());
        resume.setTitle(title);
        resume.setContent(content);
        resume.setSkills(skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skillId)))
                .collect(Collectors.toList()));
        Resume updatedResume = resumeService.updateResume(id, resume, userId);
        return ResponseEntity.ok(updatedResume);
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên>Xoá hồ sơ",
            description = "Xoá một hồ sơ dựa trên ID hồ sơ và ID người dùng."
    )
    @PreAuthorize("hasRole('CANDIDATE')")
    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteResume(
            @Parameter(description = "ID hồ sơ") @PathVariable Integer id,
            @Parameter(description = "ID người dùng") @PathVariable Integer userId) {
        resumeService.deleteResume(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên>Lấy danh sách hồ sơ của người dùng",
            description = "Trả về tất cả hồ sơ thuộc về một người dùng cụ thể."
    )
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/{userId}")
    public ResponseEntity<List<Resume>> getResumesByUserId(
            @Parameter(description = "ID người dùng") @PathVariable Integer userId) {
        List<Resume> resumes = resumeService.getResumesByUserId(userId);
        if (resumes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resumes);
    }

    @Operation(
            summary = "<EndPoint cho trang của nhà tuyển dụng>Lấy tất cả hồ sơ",
            description = "Dành cho nhà tuyển dụng hoặc admin để lấy danh sách tất cả hồ sơ."
    )
    @GetMapping
    public ResponseEntity<List<Resume>> getAllResumes() {
        List<Resume> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }

    @Operation(
            summary = "Toggle isGenerated status of a resume",
            description = "Switch isGenerated between true and false for a specific resume and user."
    )
    @PreAuthorize("hasRole('CANDIDATE')")
    @PatchMapping("/{userId}/{id}/toggle-is-generated")
    public ResponseEntity<Resume> toggleIsGenerated(
            @PathVariable Integer userId,
            @PathVariable Integer id) {
        Resume updated = resumeService.toggleIsGenerated(id, userId);
        return ResponseEntity.ok(updated);
    }



    @Operation(
            summary = "Get resume file as Base64",
            description = "Returns the resume file (CV) as a Base64-encoded string for the given resumeId. Usable by both candidate and recruiter."
    )
    @GetMapping("/{resumeId}/file-base64")
    public ResponseEntity<String> getResumeFileBase64(@PathVariable Integer resumeId) {
        String base64 = resumeService.getResumeFileBase64(resumeId);
        return ResponseEntity.ok().body(base64);
    }
}
