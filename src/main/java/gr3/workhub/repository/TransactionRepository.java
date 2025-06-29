// src/main/java/gr3/workhub/repository/TransactionRepository.java
package gr3.workhub.repository;

import gr3.workhub.entity.Transaction;
import gr3.workhub.entity.User;
import gr3.workhub.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserAndServicePackageAndStatus(User user, ServicePackage servicePackage, Transaction.Status status);
    List<Transaction> findByUser(User user);
}