package gr3.workhub.controller;

import gr3.workhub.entity.Skill;
import gr3.workhub.service.SkillService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin

@RestController
@RequestMapping("/workhub/api/v1/skill")
@RequiredArgsConstructor
@Tag(name = "✅ Skill", description = "Quản lý các kỹ năng (skill) của ứng viên và nhà tuyển dụng")
public class SkillController {

    private final SkillService skillService;

    // Create a new skill
    @PostMapping
//    @PreAuthorize("hasRole('ROLE_super_admin')")
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        Skill createdSkill = skillService.createSkill(skill);
        return ResponseEntity.ok(createdSkill);
    }

    // Update an existing skill
    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_super_admin')")
    public ResponseEntity<Skill> updateSkill(@PathVariable Integer id, @RequestBody Skill updatedSkill) {
        Skill skill = skillService.updateSkill(id, updatedSkill);
        return ResponseEntity.ok(skill);
    }

    // Delete a skill
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_super_admin')")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    // Get all skills
    @GetMapping
//    @PreAuthorize("hasRole('ROLE_candidate') or hasRole('ROLE_RECRUITER') or hasRole('ROLE_super_admin')")
    public ResponseEntity<List<Skill>> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }
}