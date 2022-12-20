package br.com.eboli.services;

import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {

    CustomerResponse save(CustomerRequest request);
    Iterable<CustomerResponse> findAll();
    CustomerResponse findById(Long id);
    CustomerResponse findByCnpj(String cnpj);
    Iterable<CustomerResponse> findByNameContains(String name);
    Iterable<CustomerResponse> findByFoundation(String foundation);

}
