package br.com.eboli.repositories;

import br.com.eboli.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByFullnameContains(String fullname);

    Optional<Customer> findByCnpj(String cnpj);

    List<Customer> findByFoundation(LocalDate foundation);

    List<Customer> findByFoundationBetween(LocalDate start, LocalDate end);

    List<Customer> findByRegistered(LocalDateTime registered);

    List<Customer> findByRegisteredBetween(LocalDateTime start, LocalDateTime end);

}
