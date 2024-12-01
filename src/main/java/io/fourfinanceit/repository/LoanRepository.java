package io.fourfinanceit.repository;

import io.fourfinanceit.domain.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByClientId(Long clientId);

    @Query("SELECT l FROM Loan l WHERE l.client.personalId = :personalId")
    List<Loan> findAllByClientPersonalId(String personalId);
}

