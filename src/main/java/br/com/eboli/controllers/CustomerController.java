package br.com.eboli.controllers;

import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import br.com.eboli.repositories.CustomerRepository;
import br.com.eboli.services.CustomerService;
import br.com.eboli.utils.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.eboli.controllers.assemblers.CustomerAssembler.toCollectionModel;
import static br.com.eboli.controllers.assemblers.CustomerAssembler.toModel;
import static br.com.eboli.utils.StringFormatter.replaceUnderscoreBySpace;

@RestController
@RequestMapping("/v1/api/customers")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerController {

    private final CustomerRepository repository;
    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CustomerResponse> save(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(
                toModel(service.save(request))
        );
    }

    @GetMapping
    public ResponseEntity<CollectionModel<CustomerResponse>> findAll() {
        Iterable<CustomerResponse> responses = service.findAll();
        return ResponseEntity.ok(
                toCollectionModel(responses)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                toModel(service.findById(id))
        );
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CustomerResponse> findByCnpj(@PathVariable String cnpj) {
        System.out.println("cnpj: " + cnpj);
        return ResponseEntity.ok(
                toModel(service.findByCnpj(cnpj))
        );
    }

    @GetMapping("/find")
    public ResponseEntity<CollectionModel<CustomerResponse>> find(@RequestParam Map<String, String> params) {
        if (params.equals(null)) {
            throw new IllegalArgumentException(
                    "Deve-se informar os dados que serão usados para encontrar o cliente.");
        }

        Iterable<CustomerResponse> responses;
        if (params.containsKey("name")) {
            String name = params.get("name");
            responses = service.findByNameContains(name);

        } else if (params.containsKey("foundation")) {
            String foundation = params.get("foundation");
            responses = service.findByFoundation(foundation);

        } else if (params.containsKey("foundation-start") && params.containsKey("foundation-end")) {
            String foundationStart = params.get("foundation-start");
            String foundationEnd = params.get("foundation-end");
            responses = service.findByFoundationBetween(foundationStart, foundationEnd);

        } else if (params.containsKey("registered")) {
            String registered = params.get("registered");
            responses = service.findByRegistered(registered);

        } else if (params.containsKey("registered-start") && params.containsKey("registered-end")) {
            String registeredStart = params.get("registered-start");
            String registeredEnd = params.get("registered-end");
            responses = service.findByRegisteredBetween(registeredStart, registeredEnd);

        } else {
            throw new IllegalArgumentException(
                    "O parâmetro informado não é valido.");
        }

        return ResponseEntity.ok(toCollectionModel(responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateById(@PathVariable Long id,
                                                       @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(
                toModel(service.updateById(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
