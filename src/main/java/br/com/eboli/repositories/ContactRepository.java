package br.com.eboli.repositories;

import br.com.eboli.models.Contact;
import br.com.eboli.models.enums.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    Optional<Contact> findByContact(String contact);

    List<Contact> findByType(ContactType type);

    @Modifying
    @Query("SELECT c FROM Contact c WHERE c.customer.id=?1")
    List<Contact> findByCustomerId(Integer customerId);

}
