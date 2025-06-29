package gr3.workhub.dto;

import gr3.workhub.entity.Job;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class JobResponse {
    private Integer id;
    private String title;
    private String description;
    private String companyName;
    private String companyLogo;
    private String category;
    private String type;
    private String position;
    private String salaryRange;
    private String experience;
    private String location;
    private java.time.LocalDateTime deadline;
    private List<String> skills;
    private Integer recruiterId;
    private String recruiterFullname;
    private String recruiterUsername;
    private Integer categoryId;
    private Integer typeId;
    private Integer positionId;

    public JobResponse(Job job, String companyName, String companyLogo) {
        this.id = job.getId();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.companyName = companyName;
        this.companyLogo = companyLogo;
        this.category = job.getCategory() != null ? job.getCategory().getName() : null;
        this.type = job.getType() != null ? job.getType().getName() : null;
        this.position = job.getPosition() != null ? job.getPosition().getName() : null;
        this.salaryRange = job.getSalaryRange();
        this.experience = job.getExperience();
        this.location = job.getLocation();
        this.deadline = job.getDeadline();
        this.skills = job.getSkills() != null
                ? job.getSkills().stream().map(skill -> skill.getName()).collect(Collectors.toList())
                : null;
        this.recruiterId = job.getRecruiter() != null ? job.getRecruiter().getId() : null;
        this.recruiterFullname = job.getRecruiter() != null ? job.getRecruiter().getFullname() : null;
        this.recruiterUsername = job.getRecruiter() != null ? job.getRecruiter().getEmail() : null; // Nếu muốn lấy username, thay getEmail() bằng getUsername()
        this.categoryId = job.getCategory() != null ? job.getCategory().getId() : null;
        this.typeId = job.getType() != null ? job.getType().getId() : null;
        this.positionId = job.getPosition() != null ? job.getPosition().getId() : null;
    }
}