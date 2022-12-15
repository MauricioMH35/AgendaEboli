package br.com.eboli.repositories;

import br.com.eboli.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByNeighborhood(String neighborhood);

    List<Address> findByCity(String city);

    List<Address> findByState(String state);

    List<Address> findByZipCode(String zipCode);

}
