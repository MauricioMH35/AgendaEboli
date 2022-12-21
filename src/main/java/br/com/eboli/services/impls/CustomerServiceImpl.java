package br.com.eboli.services.impls;

import br.com.eboli.exceptions.ConflictException;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import br.com.eboli.repositories.CustomerRepository;
import br.com.eboli.services.CustomerService;
import br.com.eboli.utils.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.eboli.utils.DateFormatter.*;
import static br.com.eboli.utils.StringFormatter.replaceUnderscoreBySpace;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    @Override
    public CustomerResponse save(CustomerRequest request) {
        if (request.equals(new CustomerRequest()) || request.equals(null)) {
            throw new IllegalArgumentException(
                    "As informações do cliente devem ser informadas para proceguir com o cadastro.");
        }

        boolean cnpjExists = repository.findByCnpj(request.getCnpj()).isPresent();
        if (cnpjExists) {
            throw new ConflictException(
                    "Não é possível cadastrar o mesmo cliente.");
        }

        Long id = repository.save(
                CustomerRequest.parseToModel(request)
        ).getId();
        request.setId(id);
        return CustomerResponse.parse(request);
    }

    @Override
    public Iterable<CustomerResponse> findAll() {
        Iterable<CustomerResponse> responses = repository.findAll().stream()
                .map(c -> CustomerResponse.parse(c))
                .collect(Collectors.toList());
        if (!responses.iterator().hasNext()) {
            throw new NotFoundException("Não foi possivel encontrar clientes cadastrados.");
        }

        return responses;
    }

    @Override
    public CustomerResponse findById(Long id) {
        if (id == null || id == 0l) {
            throw new IllegalArgumentException(
                    "O identificador deve ser informado para realizar a operação.");
        }

        CustomerResponse response = CustomerResponse.parse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o cliente indicado pelo identificador."))
        );

        return response;
    }

    @Override
    public CustomerResponse findByCnpj(String cnpj) {
        if (cnpj == null || cnpj == "") {
            throw new IllegalArgumentException(
                    "O CNPJ informado não é válido.");
        }

        CustomerResponse response = CustomerResponse.parse(repository.findByCnpj(cnpj)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o cliente indicado pelo CNPJ.")));

        return response;
    }

    @Override
    public Iterable<CustomerResponse> findByNameContains(String name) {
        if (name == null || name == "") {
            throw new IllegalArgumentException(
                    "O nome informado não é valido para realizar a operação de busca.");
        }

        String changeName = replaceUnderscoreBySpace(name);
        Iterable<CustomerResponse> responses = repository
                .findByFullnameContains(changeName).stream()
                .map(c -> CustomerResponse.parse(c))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar clientes que contenham o nome indicado.");
        }

        return responses;
    }

    @Override
    public Iterable<CustomerResponse> findByFoundation(String foundation) {
        if (foundation == "" || !checkDatePattern(foundation)) {
            throw new IllegalArgumentException(
                    "A data de fundação do cliente não é válida.");
        }

        Iterable<CustomerResponse> responses = repository.findByFoundation(
                    DateFormatter.parseDate(foundation)
                ).stream()
                .map(c -> CustomerResponse.parse(c))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar clientes com data de fundação indicada.");
        }

        return responses;
    }

    @Override
    public Iterable<CustomerResponse> findByFoundationBetween(String startTarget, String endTarget) {
        boolean checkIsValid = startTarget != null &&
                        endTarget != null &&
                        checkDatePattern(startTarget) &&
                        checkDatePattern(endTarget);

        if (!checkIsValid) {
            throw new IllegalArgumentException(
                    "As datas de fundação não são validas.");
        }

        Iterable<CustomerResponse> responses;
        responses = repository.findByFoundationBetween(
                        DateFormatter.parseDate(startTarget),
                        DateFormatter.parseDate(endTarget)
                ).stream()
                .map(c -> CustomerResponse.parse(c))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar clientes entre as datas de fundação.");
        }

        return responses;
    }

    @Override
    public Iterable<CustomerResponse> findByRegistered(String target) {
        boolean checkIsValid = target != null && checkDateTimePattern(target);

        if (!checkIsValid) {
            throw new IllegalArgumentException(
                    "A data de registro informada não é valida");
        }

        LocalDateTime registerd = DateFormatter.parseDateTime(replaceUnderscoreBySpace(target));
        Iterable<CustomerResponse> responses = repository.findByRegistered(registerd)
                .stream()
                .map(c -> CustomerResponse.parse(c))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não é possivel encontrar clientes registrados na data indicada.");
        }

        return responses;
    }

}
