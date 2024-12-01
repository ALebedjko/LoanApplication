package io.fourfinanceit.controller;

import io.fourfinanceit.domain.Client;
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
        List<Client> clientList = customerRepository.findAll();
        List<CustomerResponse> responseList = new ArrayList<>();

        for (Client client : clientList) {
            CustomerResponse response = new CustomerResponse();
            response.setId(client.getId());
            response.setName(client.getName());
            response.setSurname(client.getSurname());
            response.setPersonalId(client.getPersonalId());
            response.setLoans(client.getLoans());
            responseList.add(response);
        }
        return responseList;
    }

}
