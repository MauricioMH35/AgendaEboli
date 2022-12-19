package br.com.eboli.controllers;

import br.com.eboli.controllers.assemblers.CustomerAssembler;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import br.com.eboli.repositories.CustomerRepository;
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

import static br.com.eboli.utils.StringFormatter.replaceUnderscoreBySpace;

@RestController
@RequestMapping("/v1/api/customers")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerController {

    private final CustomerRepository repository;
    private final CustomerAssembler hateoas;

    @PostMapping
    public ResponseEntity<CustomerResponse> save(@RequestBody CustomerRequest request) {
        if (request.equals(new CustomerRequest()) || request.equals(null)) {
            throw new IllegalArgumentException(
                    "As informações do cliente devem ser informadas para proceguir com o cadastro.");
        }
        if (repository.findByCnpj(request.getCnpj()).isPresent()) {
            throw new IllegalArgumentException(
                    "Não é possível cadastrar o mesmo cliente.");
        }

        Long id = repository.save(CustomerRequest.parseToModel(request)).getId();
        request.setId(id);
        return ResponseEntity.ok(hateoas.toModel(request));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<CustomerResponse>> findAll() {
        Iterable<CustomerResponse> responses = repository.findAll().stream()
                .map(c -> CustomerResponse.parse(c))
                .collect(Collectors.toList());
        if (!responses.iterator().hasNext()) {
            throw new NotFoundException("Não foi possivel encontrar clientes cadastrados.");
        }

        return ResponseEntity.ok(hateoas.toCollectionModel(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificador deve ser informado para realizar a operação.");
        }

        CustomerResponse response = CustomerResponse.parse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o cliente indicado pelo identificador."))
        );
        return ResponseEntity.ok(hateoas.toModel(response));
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CustomerResponse> findByCnpj(@PathVariable String cnpj) {
        if (cnpj == null) {
            throw new IllegalArgumentException(
                    "O CNPJ informado não é válido.");
        }

        CustomerResponse response = CustomerResponse.parse(repository.findByCnpj(cnpj)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o cliente indicado pelo CNPJ.")));

        return ResponseEntity.ok(hateoas.toModel(response));
    }

    @GetMapping("/find")
    public ResponseEntity<CollectionModel<CustomerResponse>> find(@RequestParam Map<String, String> params) {
        if (params.equals(null)) {
            throw new IllegalArgumentException(
                    "Deve-se informar os dados que serão usados para encontrar o cliente.");
        }

        CollectionModel<CustomerResponse> responses = null;
        if (params.containsKey("name")) {
            responses = findByNameContains(params.get("name"));

        } else if (params.containsKey("foundation")) {
            responses = findByFoundation(params.get("foundation"));

        } else if (params.containsKey("foundation-start") && params.containsKey("foundation-end")) {
            responses = findByFoundationBetween(
                    params.get("foundation-start"),
                    params.get("foundation-end"));

        } else if (params.containsKey("registered")) {
            responses = findByRegistered(params.get("registered"));

        } else if (params.containsKey("registered-start") && params.containsKey("registered-end")) {
            responses = findByRegisteredBetween(
                    params.get("registered-start"),
                    params.get("registered-end"));

        } else {
            throw new IllegalArgumentException(
                    "O parâmetro informado não é valido.");
        }

        return ResponseEntity.ok(hateoas.toCollectionModel(responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateById(@PathVariable Long id,
                                                       @RequestBody CustomerRequest request) {
        if (id.equals(null) || request.equals(new CustomerRequest())) {
            throw new IllegalArgumentException(
                    "As informações passdas são invalidas.");
        }

        Customer updated = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o cliente com identificado."))
                .updateCustomer(request);
        repository.save(updated);

        return ResponseEntity.ok(hateoas.toModel(CustomerResponse.parse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificado informado não é valido.");
        }

        Customer found = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o cliente com o identificador informado."));
        repository.delete(found);
        return ResponseEntity.noContent().build();
    }

    private CollectionModel<CustomerResponse> findByNameContains(String name) {
        if (name != null) {
            String changeName = replaceUnderscoreBySpace(name);
            Iterable<CustomerResponse> responses = repository
                    .findByFullnameContains(changeName).stream()
                        .map(c -> CustomerResponse.parse(c))
                        .collect(Collectors.toList());

            if (!responses.iterator().hasNext()) {
                throw new NotFoundException(
                        "Não foi possivel encontrar clientes que contenham o nome indicado.");
            }

            return CollectionModel.of(responses);

        } else {
            throw new IllegalArgumentException(
                    "O nome informado não é valido para realizar a operação de busca.");
        }
    }

    private CollectionModel<CustomerResponse> findByFoundation(String foundation) {
        if (foundation != "" && checkDatePattern(foundation)) {
            Iterable<CustomerResponse> responses = repository.findByFoundation(DateFormatter.parseDate(foundation)).stream()
                    .map(c -> CustomerResponse.parse(c))
                    .collect(Collectors.toList());

            if (!responses.iterator().hasNext()) {
                throw new NotFoundException(
                        "Não foi possivel encontrar clientes com data de fundação indicada.");
            }

            return CollectionModel.of(responses);

        } else {
            throw new IllegalArgumentException(
                    "A data de fundação do cliente não é válida.");
        }
    }

    private CollectionModel<CustomerResponse> findByFoundationBetween(
            String startTarget, String endTarget) {
        boolean checkIsValid =
                startTarget != null &&
                endTarget != null &&
                checkDatePattern(startTarget) &&
                checkDatePattern(endTarget);

        if (checkIsValid) {
            Iterable<CustomerResponse> responses = null;
            responses = repository.findByFoundationBetween(
                    DateFormatter.parseDate(startTarget),
                    DateFormatter.parseDate(endTarget)).stream()
                    .map(c -> CustomerResponse.parse(c))
                    .collect(Collectors.toList());

            if (!responses.iterator().hasNext()) {
                throw new NotFoundException(
                        "Não foi possivel encontrar clientes entre as datas de fundação.");
            }

            return CollectionModel.of(responses);

        } else {
            throw new IllegalArgumentException(
                    "As datas de fundação não são validas.");
        }
    }

    private CollectionModel<CustomerResponse> findByRegistered(String target) {
        if (target != null && checkDateTimePattern(target)) {
            LocalDateTime registerd = DateFormatter.parseDateTime(replaceUnderscoreBySpace(target));
            Iterable<CustomerResponse> responses = repository.findByRegistered(registerd).stream()
                    .map(c -> CustomerResponse.parse(c))
                    .collect(Collectors.toList());

            if (!responses.iterator().hasNext()) {
                throw new NotFoundException(
                        "Não é possivel encontrar clientes registrados na data indicada.");
            }

            return CollectionModel.of(responses);

        } else {
            throw new IllegalArgumentException(
                    "A data de registro informada não é valida");
        }
    }

    private CollectionModel<CustomerResponse> findByRegisteredBetween(String startTarget, String endTarget) {
        boolean checkIsValid =
                startTarget != null &&
                endTarget != null &&
                checkDateTimePattern(startTarget) &&
                checkDateTimePattern(endTarget);

        if (checkIsValid) {
            LocalDateTime registeredStart = DateFormatter.parseDateTime(replaceUnderscoreBySpace(startTarget));
            LocalDateTime registeredEnd = DateFormatter.parseDateTime(replaceUnderscoreBySpace(endTarget));

            Iterable<CustomerResponse> responses = repository.findByRegisteredBetween(
                    registeredStart, registeredEnd).stream()
                    .map(c -> CustomerResponse.parse(c))
                    .collect(Collectors.toList());

            if (!responses.iterator().hasNext()) {
                throw new NotFoundException(
                        "Não foi encontrado clientes registrado entre as datas indicadas.");
            }

            return CollectionModel.of(responses);

        } else {
            throw new IllegalArgumentException(
                    "As datas de registro não são validas.");

        }
    }

    private static Boolean checkWhiteSpace(String target) {
        final String regex = "(\\s+)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    private Boolean checkDatePattern(String target) {
        final String regex = "(\\d{4}.\\d{2}.\\d{2})";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public Boolean checkDateTimePattern(String target) {
        final String regex = "(\\d{4}.\\d{2}.\\d{2})_(\\d{2}-\\d{2}-\\d{2})";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    private static String changeUnderlinesBySpace(String target) {
        final String regex = "([a-zA-Z]+|[0-9]+)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(target);
        String response = "";

        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                response += matcher.group(i) + ' ';
            }
        }

        return response;
    }

    private LocalDateTime changeDateTimePattern(String target) {
        final String regex = "(\\d{4}.\\d{2}.\\d{2})_(\\d{2}-\\d{2}-\\d{2})";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(target);

        String dateTimeString = "";
        while (matcher.find()) {
            dateTimeString = matcher.group(1) + ' ' + matcher.group(2);
        }

        return DateFormatter.parseDateTime(dateTimeString);
    }

}
