package gr3.workhub.service;

import gr3.workhub.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JobSpecification {

    public Specification<Job> getJobs(
            String title, String location, Long minSalary, Long maxSalary,
            Integer categoryId, Integer typeId, Integer positionId, Integer skillId) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (title != null && !title.isEmpty()) {
                // Chuyển cả cột "title" và dữ liệu nhập vào về chữ thường
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (location != null && !location.isEmpty()) {
                // Chuyển cả cột "location" và dữ liệu nhập vào về chữ thường
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }

            if (minSalary != null) {
                // This is a simplified example. You might need to parse the salaryRange column.
                // Assuming salaryRange is a simple numeric string for now.
                // You might need a more complex logic if it's a range like "1000-2000".
                try {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salaryRange"), minSalary.toString()));
                } catch (NumberFormatException e) {
                    // Ignore if salary is not a number
                }
            }

            if (maxSalary != null) {
                // Similar to minSalary, this is a simplified example.
                try {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("salaryRange"), maxSalary.toString()));
                } catch (NumberFormatException e) {
                    // Ignore if salary is not a number
                }
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (typeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("type").get("id"), typeId));
            }

            if (positionId != null) {
                predicates.add(criteriaBuilder.equal(root.get("position").get("id"), positionId));
            }

            if (skillId != null) {
                Join<Job, Skill> skillJoin = root.join("skills");
                predicates.add(criteriaBuilder.equal(skillJoin.get("id"), skillId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}