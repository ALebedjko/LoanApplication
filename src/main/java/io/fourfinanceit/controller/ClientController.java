package io.fourfinanceit.controller;

import io.fourfinanceit.domain.Client;
import io.fourfinanceit.repository.ClientRepository;
import io.fourfinanceit.response.CustomerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/customer")

public class ClientController {
    private final
    ClientRepository clientRepository;

    @Autowired
    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @RequestMapping("/list")
    public List<CustomerResponse> list() {
        List<Client> clientList = clientRepository.findAll();
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
