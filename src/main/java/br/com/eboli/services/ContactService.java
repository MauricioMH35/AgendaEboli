package br.com.eboli.services;

import br.com.eboli.models.requests.ContactRequest;

import java.util.List;

public interface ContactService {
    ContactRequest save(ContactRequest request);

    ContactRequest findById(Integer id);

    ContactRequest findByContact(String contact);

    List<ContactRequest> findAll();

    List<ContactRequest> findByType(String type);

    List<ContactRequest> findByCustomerId(Integer customerId);

    ContactRequest updateById(Integer id, ContactRequest request);

    void deleteById(Integer id);

}
