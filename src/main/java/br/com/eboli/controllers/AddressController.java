package br.com.eboli.controllers;

import br.com.eboli.models.requests.AddressRequest;
import br.com.eboli.models.responses.AddressResponse;
import br.com.eboli.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static br.com.eboli.controllers.assemblers.AddressAssembler.toCollection;
import static br.com.eboli.controllers.assemblers.AddressAssembler.toModel;

@Scope("singleton")
@RestController
@RequestMapping("/v1/api/addresses")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = {"*" }, maxAge = 3600)
public class AddressController {

    private Logger log = LoggerFactory.getLogger(AddressController.class);

    @Qualifier("addressServiceImpl")
    private final AddressService service;

    @PostMapping
    public ResponseEntity<AddressResponse> save(@RequestBody AddressRequest request) {
        return ResponseEntity.ok(
                toModel(
                        service.save(request)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                toModel(
                        service.findById(id)
                )
        );
    }

    @GetMapping
    public ResponseEntity<CollectionModel<AddressResponse>> findAll() {
        return ResponseEntity.ok(
                toCollection(
                        service.findAll()
                )
        );
    }

    @GetMapping("/cep/{zipcode}")
    public ResponseEntity<CollectionModel<AddressResponse>> findByZipcode(@PathVariable String zipcode) {
        return ResponseEntity.ok(
                toCollection(
                        service.findByZipCode(zipcode)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateById(@PathVariable Integer id,
                                                      @RequestBody AddressRequest request) {
        return ResponseEntity.ok(
                toModel(
                        service.updateById(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
