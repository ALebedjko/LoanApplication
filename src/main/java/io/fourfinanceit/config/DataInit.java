package io.fourfinanceit.config;

import io.fourfinanceit.domain.Client;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInit {
    private final
    CustomerRepository customerRepository;

    @Autowired
    public DataInit(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostConstruct
    public void initData() {
        Client firstClient = new Client("John", "Smith", "abc-xyz0");
        firstClient.addLoan(new Loan(new BigDecimal(10.1), new BigDecimal(15.5), 40));
        customerRepository.save(firstClient);

        Client secondClient = new Client("Petja", "Bobrov", "abc-xyz1");
        secondClient.addLoan(new Loan(new BigDecimal(20.3), new BigDecimal(20.5), 10));
        secondClient.addLoan(new Loan(new BigDecimal(100.5), new BigDecimal(50), 30));
        customerRepository.save(secondClient);

        Client thirdClient = new Client("Sasha", "Petrov", "abc-xyz2");
        customerRepository.save(thirdClient);
    }
}
