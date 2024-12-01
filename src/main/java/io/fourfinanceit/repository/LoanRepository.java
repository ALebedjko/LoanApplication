package io.fourfinanceit.repository;

import io.fourfinanceit.domain.Client;
import io.fourfinanceit.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByClientId(Long clientId);
}
