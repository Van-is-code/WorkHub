// src/main/java/gr3/workhub/repository/TransactionRepository.java
package gr3.workhub.repository;

import gr3.workhub.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}