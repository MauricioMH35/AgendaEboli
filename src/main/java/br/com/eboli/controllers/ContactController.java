package br.com.eboli.controllers;

import br.com.eboli.controllers.assemblers.ContactAssembler;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.requests.ContactRequest;
import br.com.eboli.models.responses.ContactResponse;
import br.com.eboli.repositories.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/contacts")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContactController {

    private final ContactRepository repository;

    @PostMapping
    public ResponseEntity<ContactResponse> save(@RequestBody ContactRequest request) {
        if (request.equals(new ContactRequest())) {
            throw new IllegalArgumentException(
                    "As informaçãoes do contato não são validas.");
        }
        if (repository.findByContact(request.getContact()).isPresent()) {
            throw new IllegalArgumentException(
                    "Contata já cadastrado.");
        }

        Long id = repository.save(ContactRequest.parseToModel(request)).getId();
        request.setId(id);
        return ResponseEntity.ok(ContactResponse.parse(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> findById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificador deve ser informado para proceguir com o processo.");
        }

        ContactResponse response = ContactResponse.parse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o contato com o identificador informado.")));
        return ResponseEntity.ok(ContactAssembler.toModel(response));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<CollectionModel<ContactResponse>> findByContactType(@PathVariable String type) {
        ContactType checkedType = checkIsContactType(type);
        if (checkedType == null) {
            throw new IllegalArgumentException(
                    "O tipo de contato informado não é valido.");
        }

        Iterable<ContactResponse> responses = repository.findByType(checkedType).stream()
                .map(c -> ContactResponse.parse(c))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possible to econtrar contacts com type.");
        }
        return ResponseEntity.ok(ContactAssembler.toCollectionModel(responses));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CollectionModel<ContactResponse>> findByCustomerId(@PathVariable Long customerId) {
        if (customerId.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificador do cliente não valido para realizar a busca de seus contatos.");
        }

        Iterable<ContactResponse> responses = repository.findByCustomerId(customerId).stream()
                .map(c -> ContactResponse.parse(c))
                .collect(Collectors.toList());
        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar contatos do cliente indicado.");
        }

        return ResponseEntity.ok(ContactAssembler.toCollectionModel(responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateById(@PathVariable Long id,
                                                      @RequestBody ContactRequest request) {
        if (id.equals(null) || request.equals(new ContactRequest())) {
            throw new IllegalArgumentException(
                    "As informações informadas não são validas para realizar a operação.");
        }

        Contact updated = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o contato indicado pelo identificador."))
                .updateContact(request);
        repository.save(updated);

        return ResponseEntity.ok(ContactAssembler.toModel(ContactResponse.parse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ContactResponse> deleteById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificador não é valido.");
        }

        Contact found = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel localizar o contato indicado por seu identificador."));
        repository.delete(found);
        return ResponseEntity.noContent().build();
    }

    private ContactType checkIsContactType(String target) {
        if (target.equals("") || target.equals(null)) {
            return null;
        }

        for (ContactType type : ContactType.values()) {
            boolean checkEqualsLowerCase = type.toString().equalsIgnoreCase(target);
            if (checkEqualsLowerCase) {
                return type;
            }
        }
        return null;
    }

}
