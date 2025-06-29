package gr3.workhub.repository;

import gr3.workhub.entity.CompanyProfile;
import gr3.workhub.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

    @Query("SELECT j FROM Job j " +
            "WHERE (:userId IS NULL OR j.recruiter.id = :userId) " +
            "AND (:categoryId IS NULL OR j.category.id = :categoryId) " +
            "AND (:typeId IS NULL OR j.type.id = :typeId) " +
            "AND (:positionId IS NULL OR j.position.id = :positionId) " +
            "AND (:salaryRange IS NULL OR j.salaryRange = :salaryRange) " +
            "AND (:skillId IS NULL OR :skillId IN (SELECT s.id FROM j.skills s))")
    List<Job> findJobsByCriteria(@Param("userId") Integer userId,
                                 @Param("categoryId") Integer categoryId,
                                 @Param("typeId") Integer typeId,
                                 @Param("positionId") Integer positionId,
                                 @Param("salaryRange") String salaryRange,
                                 @Param("skillId") Integer skillId);

    List<Job> findByPostAt(Job.PostAt postAt);

    List<Job> findByTitleContainingIgnoreCase(String title);
    List<Job> findByDeadlineBeforeAndStatus(LocalDateTime deadline, Job.DeadlineStatus status);
}