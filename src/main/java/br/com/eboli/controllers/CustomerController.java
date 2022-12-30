package br.com.eboli.controllers;

import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import br.com.eboli.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static br.com.eboli.controllers.assemblers.CustomerAssembler.toCollection;
import static br.com.eboli.controllers.assemblers.CustomerAssembler.toModel;

@Scope("singleton")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/v1/api/customers")
@CrossOrigin(origins = {"*" }, maxAge = 3600)
public class CustomerController {

    @Qualifier("customerServiceImpl")
    private final CustomerService service;

    @GetMapping("/gettime")
    public ResponseEntity<LocalDateTime> get() {
        return ResponseEntity.ok(
                LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> save(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(
                toModel(service.save(request))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                toModel(
                        service.findById(id)
                )
        );
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CustomerResponse> findByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(
                toModel(
                        service.findByCnpj(cnpj)
                )
        );
    }

    @GetMapping
    public ResponseEntity<CollectionModel<CustomerResponse>> findAll() {
        return ResponseEntity.ok(
                toCollection(
                        service.findAll()
                )
        );
    }

    @GetMapping("/find")
    public ResponseEntity<CollectionModel<CustomerResponse>> find(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(
                toCollection(
                        service.find(params)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateById(@PathVariable Integer id,
                                                       @RequestBody CustomerRequest request) {
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
