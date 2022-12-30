package br.com.eboli.services.impls;

import br.com.eboli.exceptions.ConflictException;
import br.com.eboli.exceptions.InternalServereErrorException;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Contact;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.requests.ContactRequest;
import br.com.eboli.repositories.ContactRepository;
import br.com.eboli.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.eboli.models.requests.ContactRequest.parseToRequest;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContactServiceImpl implements ContactService {

    private final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Qualifier("contactRepository")
    private final ContactRepository repository;

    @Override
    public ContactRequest save(ContactRequest request) {
        if (request.fieldsAreBlank()) {
            log.warn("As informações do contato não são válidas.");
            throw new IllegalArgumentException("As informações do contato não são válidas.");
        }

        if (repository.findByContact(request.getContact()).isPresent()) {
            log.warn("O contato [" + request.getContact() + "] já possui um cadastro.");
            throw new ConflictException("O contato [" + request.getContact() + "] já possui um cadastro.");
        }

        try {
            Integer id = repository.save(request.parse()).getId();
            request.setId(id);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar cadastrar o contato [" + request.getContact() +
                    "]: \tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("\"Houve um erro interno ao servidor ao tentar buscar o " +
                    "contato [" + request.getContact() + "].");
        }

        return request;
    }

    @Override
    public ContactRequest findById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O id [" + id + "] informado não é válido.");
            throw new IllegalArgumentException("O id [" + id + "] informado não é válido.");
        }

        ContactRequest request = parseToRequest(basicFindById(id));
        return request;
    }

    @Override
    public ContactRequest findByContact(String contact) {
        if (contact == null || contact == "" || contact.toCharArray().length < 6) {
            log.warn("O contato informado não é válido.");
            throw new IllegalArgumentException("O contato informado não é válido.");
        }

        ContactRequest request = new ContactRequest();

        Contact found = repository.findByContact(contact)
                .orElseThrow(() -> new NotFoundException("O contato informado não foi encontrado."));

        try {
            request = parseToRequest(found);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar o contato [" + contact + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar o " +
                    "contato [" + contact + "].");

        }

        return request;
    }

    @Override
    public List<ContactRequest> findAll() {
        List<ContactRequest> requests = repository.findAll()
                .stream()
                .map(c -> parseToRequest(c))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            log.warn("Não foi possivel encontrar contatos cadastrados.");
            throw new NotFoundException("Não foi possivel encontrar contatos cadastrados.");
        }

        return requests;
    }

    @Override
    public List<ContactRequest> findByType(String type) {
        if (type == null || type == "") {
            log.warn("O tipo [" + type + "] informado não é válido.");
            throw new IllegalArgumentException("O tipo [" + type + "] informado não é válido.");
        }

        boolean isValidType = false;
        ContactType typeEnum = ContactType.PHONE;
        for (ContactType t : ContactType.values()) {
            if (t.toString().equalsIgnoreCase(type)) {
                typeEnum = t;
                isValidType = true;
            }
        }

        if (!isValidType) {
            throw new IllegalArgumentException("O tipo ["+type+"] informado não é válido.");
        }

        List<ContactRequest> requests = new ArrayList<>();
        try {
            requests = repository.findByType(typeEnum)
                    .stream()
                    .map(c -> parseToRequest(c))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar o contato pelo tipo [" + type + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar o contato " +
                    "pelo tipo [" + type + "].");
        }

        if (requests.isEmpty()) {
            log.warn("Não foram encontrados contatos do tipo [" + type + "] informado.");
            throw new NotFoundException("Não foram encontrados contatos do tipo [" + type + "] informado.");
        }

        return requests;
    }

    @Override
    public List<ContactRequest> findByCustomerId(Integer customerId) {
        if (customerId == null || customerId <= 0) {
            log.warn("O id [" + customerId + "] do cliente não é válido.");
            throw new IllegalArgumentException("O id [" + customerId + "] do cliente não é válido.");
        }

        List<ContactRequest> requests = new ArrayList<>();
        try {
            requests = repository.findByCustomerId(customerId)
                    .stream()
                    .map(c -> parseToRequest(c))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar os contatos do cliente com id [" +
                    customerId + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar os contatos " +
                    "do cliente com id [" + customerId + "].");
        }

        if (requests.isEmpty()) {
            log.warn("Não foram encontrados contatos do cliente de id [" + customerId + "] informado.");
            throw new NotFoundException("Não foram encontrados contatos do cliente de id [" + customerId +
                    "] informado.");
        }

        return requests;
    }

    @Override
    public ContactRequest updateById(Integer id, ContactRequest request) {
        if (id == null || id <= 0) {
            log.warn("O id [" + id + "] informado não é válido.");
            throw new IllegalArgumentException("O id [" + id + "] informado não é válido.");
        }

        Contact found = basicFindById(id);

        try {
            Contact updated = found.update(request.parse());
            repository.save(updated);
            request = parseToRequest(updated);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar atualizar o contato com id [" + id + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar atualizar o contato " +
                    "com id [" + id + "].");
        }

        return request;
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O id [" + id + "] informado não é válido.");
            throw new IllegalArgumentException("O id [" + id + "] informado não é válido.");
        }

        basicFindById(id);

        try {
            repository.deleteById(id);

        } catch (Throwable e) {
            log.error("Houve um erro ao tentar remover o contato com id [" + id + "]." +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro ao tentar remover o contato com id [" + id + "].");
        }

        log.info("Foi removido da base de dados o contato com id [" + id + "].");
    }

    private Contact basicFindById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possivel encontrar o contato com id [" + id + "]."));
    }

}
