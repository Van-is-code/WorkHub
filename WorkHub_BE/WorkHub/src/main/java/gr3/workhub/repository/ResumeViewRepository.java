package gr3.workhub.repository;

import gr3.workhub.entity.ResumeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeViewRepository extends JpaRepository<ResumeView, Integer> {
    boolean existsByResume_IdAndRecruiter_Id(Integer resumeId, Integer recruiterId);
}
