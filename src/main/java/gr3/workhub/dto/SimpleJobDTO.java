package gr3.workhub.dto;

public class SimpleJobDTO {
    private Integer id;
    private String title;
    private String companyName;
    private String location;
    private String salaryRange;

    public SimpleJobDTO(Integer id, String title, String companyName, String location, String salaryRange) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.salaryRange = salaryRange;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
}
