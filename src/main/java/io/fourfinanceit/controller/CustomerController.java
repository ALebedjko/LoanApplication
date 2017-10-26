package io.fourfinanceit.controller;

import io.fourfinanceit.domain.Customer;
import io.fourfinanceit.repository.CustomerRepository;
import io.fourfinanceit.response.CustomerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/customer")

public class CustomerController {
    private final
    CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @RequestMapping("/list")
    public List<CustomerResponse> list() {
        List<Customer> customerList = customerRepository.findAll();
        List<CustomerResponse> responseList = new ArrayList<>();

        for (Customer customer : customerList) {
            CustomerResponse response = new CustomerResponse();
            response.setId(customer.getId());
            response.setName(customer.getName());
            response.setSurname(customer.getSurname());
            response.setPersonalId(customer.getPersonalId());
            response.setLoans(customer.getLoans());
            responseList.add(response);
        }
        return responseList;
    }

}
