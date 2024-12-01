package io.fourfinanceit.config;

import io.fourfinanceit.domain.Client;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.repository.ClientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInit {
    private final ClientRepository clientRepository;

    @Autowired
    public DataInit(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostConstruct
    public void initData() {
        // First client with loans
        Client firstClient = new Client("John", "Smith", "abc-xyz0");
        firstClient.addLoan(new Loan(firstClient, new BigDecimal("10.1"), new BigDecimal("15.5"), 40));
        clientRepository.save(firstClient);

        // Second client with loans
        Client secondClient = new Client("Petja", "Bobrov", "abc-xyz1");
        secondClient.addLoan(new Loan(secondClient, new BigDecimal("20.3"), new BigDecimal("20.5"), 10));
        secondClient.addLoan(new Loan(secondClient, new BigDecimal("100.5"), new BigDecimal("50"), 30));
        clientRepository.save(secondClient);

        // Third client without loans
        Client thirdClient = new Client("Sasha", "Petrov", "abc-xyz2");
        clientRepository.save(thirdClient);
    }
}