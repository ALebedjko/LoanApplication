package io.fourfinanceit.config;

import io.fourfinanceit.domain.Customer;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
        Customer firstCustomer = new Customer("John", "Smith", "abc-xyz0");
        firstCustomer.addLoan(new Loan(new BigDecimal(10.1), new BigDecimal(15.5), 40));
        customerRepository.save(firstCustomer);

        Customer secondCustomer = new Customer("Petja", "Bobrov", "abc-xyz1");
        secondCustomer.addLoan(new Loan(new BigDecimal(20.3), new BigDecimal(20.5), 10));
        secondCustomer.addLoan(new Loan(new BigDecimal(100.5), new BigDecimal(50), 30));
        customerRepository.save(secondCustomer);

        Customer thirdCustomer = new Customer("Sasha", "Petrov", "abc-xyz2");
        customerRepository.save(thirdCustomer);
    }
}
