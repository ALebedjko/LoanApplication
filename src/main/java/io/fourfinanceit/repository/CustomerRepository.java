package io.fourfinanceit.repository;

import io.fourfinanceit.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Client, Long> {
    Client findOneByPersonalId(String personalId);
}
