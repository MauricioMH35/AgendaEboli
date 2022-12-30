package br.com.eboli.services.impls;

import br.com.eboli.exceptions.InternalServereErrorException;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Address;
import br.com.eboli.models.requests.AddressRequest;
import br.com.eboli.repositories.AddressRepository;
import br.com.eboli.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.eboli.models.requests.AddressRequest.parseToRequest;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressServiceImpl implements AddressService {

    private final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Qualifier("addressRepository")
    private final AddressRepository repository;

    @Override
    public AddressRequest save(AddressRequest request) {
        if (request.fieldsAreBlank()) {
            log.warn("As informações do endereço não são válidas.");
            throw new IllegalArgumentException("As informações do endereço não são válidas.");
        }

        try {
            Address address = request.parse();
            Integer id = repository.save(address).getId();
            request.setId(id);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar cadastrar o endereço: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());
            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar cadastrar o endereço");
        }

        return request;
    }

    @Override
    public AddressRequest findById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O identificador [" + id + "] não é válido.");
            throw new IllegalArgumentException("O identificador [" + id + "] não é válido.");
        }

        Address found = basicFindById(id);
        AddressRequest request = new AddressRequest();

        try {
            request = parseToRequest(found);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar o endereço com id [" + id + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());
            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar o endereço " +
                    "com id [" + id + "]");
        }

        return request;
    }

    @Override
    public List<AddressRequest> findAll() {
        List<AddressRequest> found = repository.findAll()
                .stream()
                .map(a -> parseToRequest(a))
                .collect(Collectors.toList());

        if (found.isEmpty()) {
            log.warn("Não foram encontrados endereços cadastrados.");
            throw new NotFoundException("Não foram encontrados endereços cadastrados.");
        }

        return found;
    }

    @Override
    public List<AddressRequest> findByZipCode(String zipcode) {
        if (zipcode == null || zipcode == "" || zipcode.toCharArray().length < 8) {
            log.warn("O CEP [" + zipcode + "] informado não é válido.");
            throw new IllegalArgumentException("O CEP [" + zipcode + "] informado não é válido.");
        }

        List<AddressRequest> request = new ArrayList<>();
        try {
            request = repository.findByZipCode(zipcode).stream()
                    .map(a -> parseToRequest(a))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar o endereço com o CEP [" + zipcode + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar o endereço com " +
                    "o CEP [" + zipcode + "].");
        }

        if (request.isEmpty()) {
            log.warn("Não foram encontrados endereços com CEP [" + zipcode + "] informado.");
            throw new NotFoundException("Não foram encontrados endereços com CEP [" + zipcode + "] informado.");
        }

        return request;
    }

    @Override
    public AddressRequest updateById(Integer id, AddressRequest request) {
        if (id == null || id <= 0) {
            log.warn("O identificador [" + id + "] informado não é válido.");
            throw new IllegalArgumentException("O identificador [" + id + "] informado não é válido.");
        }

        Address found = basicFindById(id);

        try {
            Address updated = found.update(request.parse());
            repository.save(updated);
            request = parseToRequest(updated);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar atualizar o endereço com o id [" + id + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar atualizar o " +
                    "endereço com o id [" + id + "].");

        }

        return request;
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O identificador [" + id + "] informado não é válido.");
            throw new IllegalArgumentException("O identificador [" + id + "] informado não é válido.");
        }

        basicFindById(id);

        try {
            repository.deleteById(id);

        } catch (Exception e) {

        }
        log.info("Foi removido da base de dados o endereço com id [" + id + "].");
    }

    private Address basicFindById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possivel encontrar o endereço com o identificador [" +
                        id+"]"));
    }

}
