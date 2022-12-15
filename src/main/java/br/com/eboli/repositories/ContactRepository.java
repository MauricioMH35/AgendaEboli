package br.com.eboli.repositories;

import br.com.eboli.models.Contact;
import br.com.eboli.models.enums.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByContact(String contact);

    List<Contact> findByType(ContactType type);

}
