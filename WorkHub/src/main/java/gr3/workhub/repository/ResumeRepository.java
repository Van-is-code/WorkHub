package gr3.workhub.repository;

import gr3.workhub.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    Optional<Resume> findByIdAndUser_Id(Integer id, Integer userId);

}