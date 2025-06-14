package gr3.workhub.service;

import gr3.workhub.entity.Skill;
import gr3.workhub.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Integer skillId, Skill updatedSkill) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        skill.setName(updatedSkill.getName());
        skill.setDescription(updatedSkill.getDescription());

        return skillRepository.save(skill);
    }

    public void deleteSkill(Integer skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        skillRepository.delete(skill);
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
}