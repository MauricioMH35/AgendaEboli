package br.com.eboli.services;

import br.com.eboli.models.requests.AddressRequest;

import java.util.List;

public interface AddressService {

    AddressRequest save(AddressRequest request);

    AddressRequest findById(Integer id);

    List<AddressRequest> findAll();

    List<AddressRequest> findByZipCode(String zipcode);

    AddressRequest updateById(Integer id, AddressRequest request);

    void deleteById(Integer id);

}
