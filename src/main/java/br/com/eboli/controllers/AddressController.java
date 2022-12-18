package br.com.eboli.controllers;

import br.com.eboli.controllers.assemblers.AddressAssembler;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.requests.AddressRequest;
import br.com.eboli.models.responses.AddressResponse;
import br.com.eboli.repositories.AddressRepository;
import br.com.eboli.utils.StringFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

import static br.com.eboli.utils.StringFormatter.replaceUnderscoreBySpace;

@RestController
@RequestMapping("/v1/api/addresses")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressController {

    private final AddressRepository repository;

    @PostMapping
    public ResponseEntity<AddressResponse> save(@RequestBody AddressRequest request) {
        if (request.equals(new AddressRequest())) {
            throw new IllegalArgumentException(
                    "As informações do endereço não são validas.");
        }

        Long id = repository.save(AddressRequest.parseToModel(request)).getId();
        request.setId(id);
        return ResponseEntity.ok(AddressResponse.parse(request));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<AddressResponse>> findAll() {
        Iterable<AddressResponse> responses = repository.findAll().stream()
                .map(a -> AddressResponse.parse(a))
                .collect(Collectors.toList());
        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foram encontrados endereços cadastrados.");
        }

        return ResponseEntity.ok(CollectionModel.of(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificador informado não é valido.");
        }

        AddressResponse response = AddressResponse.parse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não é possivel encontrar o endereço usando o identificador informado.")));
        return ResponseEntity.ok(AddressAssembler.toModel(response));
    }

    @GetMapping("/find")
    public ResponseEntity<CollectionModel<AddressResponse>> find(@RequestParam Map<String, String> params) {
        if (params.equals(null)) {
            throw new IllegalArgumentException(
                    "Os parâmetro informados não são validos.");
        }

        CollectionModel<AddressResponse> responses = null;
        if (params.containsKey("neighborhood")) {
            responses = findByNeighborhood(
                    replaceUnderscoreBySpace(params.get("neighborhood")));

        } else if (params.containsKey("city")) {
            responses = findByCity(
                    replaceUnderscoreBySpace(params.get("city")));

        } else if (params.containsKey("state")) {
            responses = findByState(
                    replaceUnderscoreBySpace(params.get("state")));

        } else if (params.containsKey("zipcode")) {
            responses = findByZipCode(
                    replaceUnderscoreBySpace(params.get("zipcode")));

        } else {
            throw new IllegalArgumentException(
                    "O parâmetro informado não é valido.");

        }

        return ResponseEntity.ok(AddressAssembler.toCollectionModel(responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateById(@PathVariable Long id,
                                                      @RequestBody AddressRequest request) {
        if (id.equals(null) && request.equals(new AddressRequest())) {
            throw new IllegalArgumentException(
                    "As informações não são validas para proceguir com o processo.");
        }

        AddressResponse response = AddressResponse.parse(
                repository.findById(id)
                        .orElseThrow(() -> new NotFoundException(
                                "Não foi possivel encontrar o endereço indicado pelo identificador."))
                        .updateAddress(request));

        return ResponseEntity.ok(AddressAssembler.toModel(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<AddressResponse> deleteById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificador informado não é valido.");
        }

        repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não é possivel encontrar o endereço indicado pelo identificador."));

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CollectionModel<AddressResponse> findByNeighborhood(String target) {
        if (target.equals(null)) {
            throw new IllegalArgumentException(
                    "O bairro informado não é valido.");
        }
        target = StringFormatter.replaceUnderscoreBySpace(target);

        Iterable<AddressResponse> responses = repository.findByNeighborhood(target).stream()
                .map(a -> AddressResponse.parse(a))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar endereços no bairro indicado.");
        }

        return CollectionModel.of(responses);
    }

    private CollectionModel<AddressResponse> findByCity(String target) {
        if (target.equals(null)) {
            throw new IllegalArgumentException(
                    "A cidade informada não é valida.");
        }

        Iterable<AddressResponse> responses = repository.findByCity(target).stream()
                .map(a -> AddressResponse.parse(a))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar endereços na cidade indicada.");
        }

        return CollectionModel.of(responses);
    }

    private CollectionModel<AddressResponse> findByState(String target) {
        if (target.equals(null)) {
            throw new IllegalArgumentException(
                    "O estado informado não é valido.");
        }
        target = StringFormatter.replaceUnderscoreBySpace(target);
        Iterable<AddressResponse> responses = repository.findByState(target).stream()
                .map(a -> AddressResponse.parse(a))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não é possivel encontrar endereços no estado indicado.");
        }

        return CollectionModel.of(responses);
    }

    private CollectionModel<AddressResponse> findByZipCode(String target) {
        if (target.equals(null)) {
            throw new IllegalArgumentException(
                    "O CEP informado não é valido.");
        }

        Iterable<AddressResponse> responses = repository.findByZipCode(target).stream()
                .map(a -> AddressResponse.parse(a))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não é possivel encontrar endereços com o CEP indicado");
        }

        return CollectionModel.of(responses);
    }

}
