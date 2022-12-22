package br.com.eboli.services;

import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public interface CustomerService {

    CustomerResponse save(CustomerRequest request);
    Iterable<CustomerResponse> findAll();
    CustomerResponse findById(Long id);
    CustomerResponse findByCnpj(String cnpj);
    Iterable<CustomerResponse> findByNameContains(String name);
    Iterable<CustomerResponse> findByFoundation(String foundation);
    Iterable<CustomerResponse> findByFoundationBetween(String startTarget, String endTarget);
    Iterable<CustomerResponse> findByRegistered(String target);
    Iterable<CustomerResponse> findByRegisteredBetween(String startTarget, String endTarget);
    CustomerResponse updateById(Long id, CustomerRequest request);

}
