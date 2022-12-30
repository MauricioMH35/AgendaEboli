package br.com.eboli.controllers;

import br.com.eboli.models.requests.ContactRequest;
import br.com.eboli.models.responses.ContactResponse;
import br.com.eboli.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static br.com.eboli.controllers.assemblers.ContactAssembler.toCollection;
import static br.com.eboli.controllers.assemblers.ContactAssembler.toModel;

@Scope("singleton")
@RestController
@RequestMapping("/v1/api/contacts")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = {"*"}, maxAge = 3600)
public class ContactController {

    private Logger log = LoggerFactory.getLogger(ContactController.class);

    @Qualifier("contactServiceImpl") private final ContactService service;

    @PostMapping
    public ResponseEntity<ContactResponse> save(@RequestBody ContactRequest request) {
        return ResponseEntity.ok(
                toModel(
                        service.save(request)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                toModel(
                        service.findById(id)
                )
        );
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ContactResponse>> findAll() {
        return ResponseEntity.ok(
                toCollection(
                        service.findAll()
                )
        );
    }

    @GetMapping("/contact/{contact}")
    public ResponseEntity<ContactResponse> findByContact(@PathVariable String contact) {
        return ResponseEntity.ok(
                toModel(
                        service.findByContact(contact)
                )
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<CollectionModel<ContactResponse>> findByType(@PathVariable String type) {
        return ResponseEntity.ok(
                toCollection(
                        service.findByType(type)
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CollectionModel<ContactResponse>> findByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(
                toCollection(
                        service.findByCustomerId(customerId)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateById(@PathVariable Integer id,
                                                      @RequestBody ContactRequest request) {
        return ResponseEntity.ok(
                toModel(
                        service.updateById(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ContactResponse> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
