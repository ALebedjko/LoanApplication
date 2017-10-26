package io.fourfinanceit.repository;

import io.fourfinanceit.domain.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    @Query("SELECT COUNT (r.remoteAddress) FROM LoanRequest r WHERE CAST(r.created as date) = CAST(:requestDate as date) AND r.remoteAddress = :ip")
    Long q(@Param("ip") String ip, @Param("requestDate") LocalDate date);

}
