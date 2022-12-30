package br.com.eboli.services;

import br.com.eboli.models.requests.CustomerRequest;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    CustomerRequest save(CustomerRequest request);

    CustomerRequest findById(Integer id);

    CustomerRequest findByCnpj(String cnpj);

    List<CustomerRequest> findAll();

    List<CustomerRequest> find(Map<String, String> params);

    CustomerRequest updateById(Integer id, CustomerRequest request);

    void deleteById(Integer id);

}
