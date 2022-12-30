package br.com.eboli.services.impls;

import br.com.eboli.exceptions.ConflictException;
import br.com.eboli.exceptions.InternalServereErrorException;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.repositories.AgendaRepository;
import br.com.eboli.repositories.CustomerRepository;
import br.com.eboli.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.eboli.models.requests.CustomerRequest.parseToRequest;
import static br.com.eboli.utils.DateUtil.*;
import static br.com.eboli.utils.StringUtil.replaceToSpace;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceImpl implements CustomerService {

    private Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Qualifier("customerRepository")
    private final CustomerRepository repository;

    @Qualifier("agendaRepository")
    private final AgendaRepository agendaRepository;

    @Override
    public CustomerRequest save(CustomerRequest request) {
        if (request.fieldsAreBlank()) {
            log.warn("As informações do cliente não são válidas.");
            throw new IllegalArgumentException("As informações do cliente não são válidas.");
        }

        if (repository.findByCnpj(request.getCnpj()).isPresent()) {
            log.warn("O cliente com o CNPJ [" + request.getCnpj() + "] informado não pode ser cadastrado, " +
                    "ja possui um cadastro.");
            throw new ConflictException(
                    "O cliente com o CNPJ [" + request.getCnpj() + "] informado não pode ser cadastrado, " +
                            "ja possui um cadastro.");
        }

        try {
            if (request.getRegistered() == null || request.getRegistered() == "") {
                request.setRegistered(
                        parseDateTime(
                                LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
                        )
                );
            }

            Integer id = repository.save(request.parse()).getId();
            request.setId(id);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar cadastrar o cliente: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());
            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar cadastrar o cliente.");

        }

        return request;
    }

    @Override
    public CustomerRequest findById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O identificador informado [" + id + "] não é válido.");
            throw new IllegalArgumentException("O identificador informado [" + id + "] não é válido.");
        }

        CustomerRequest request = parseToRequest(basicFindById(id));
        return request;
    }

    @Override
    public CustomerRequest findByCnpj(String cnpj) {
        if (cnpj == null || cnpj == "") {
            log.warn("O CNPJ [" + cnpj + "] informado não é válido.");
            throw new IllegalArgumentException("O CNPJ [" + cnpj + "] informado não é válido.");
        }

        Customer found = basicFindByCnpj(cnpj);
        return parseToRequest(found);
    }

    @Override
    public List<CustomerRequest> findAll() {
        List<CustomerRequest> found = repository.findAll()
                .stream()
                .map(c -> parseToRequest(c))
                .collect(Collectors.toList());

        if (found.isEmpty()) {
            log.warn("Não foram encontrados clientes cadastrados.");
            throw new NotFoundException("Não foram encontrados clientes cadastrados.");
        }

        return found;
    }

    @Override
    public List<CustomerRequest> find(Map<String, String> params) {
        if (params.containsKey("name")) {
            return basicFindByNameContains(params.get("name"))
                    .stream()
                    .map(c -> parseToRequest(c))
                    .collect(Collectors.toList());

        } else if (params.containsKey("foundation")) {
            return basicFindByFoundation(params.get("foundation"))
                    .stream()
                    .map(c -> parseToRequest(c))
                    .collect(Collectors.toList());

        } else if (params.containsKey("foundation-start") && params.containsKey("foundation-end")) {
            return basicFindByFoundationBetween(params.get("foundation-start"), params.get("foundation-end"))
                    .stream()
                    .map(c -> parseToRequest(c))
                    .collect(Collectors.toList());

        } else if (params.containsKey("registered")) {
            return basicFindByRegistered(params.get("registered"))
                    .stream()
                    .map(c -> parseToRequest(c))
                    .collect(Collectors.toList());

        } else if (params.containsKey("registered-start") && params.containsKey("registered-end")) {
            return basicFindByRegisteredBetween(params.get("registered-start"), params.get("registered-end"))
                    .stream()
                    .map(c -> parseToRequest(c))
                    .collect(Collectors.toList());

        } else {
            log.error("A consulta não pode ser realizada, pois a informação não é válida para qualquer operação " +
                    "possivel.");

            throw new IllegalArgumentException("A consulta não pode ser realizada, pois a informação não é válida " +
                    "para qualquer operação possivel.");
        }
    }

    @Override
    public CustomerRequest updateById(Integer id, CustomerRequest request) {
        if (id == null || id <= 0) {
            log.warn("O identificador [" + id + "] informado não é válido.");
            throw new IllegalArgumentException("O identificador [" + id + "] informado não é válido.");
        }

        Customer found = basicFindById(id);

        try {
            request.setId(id);
            Customer updated = found.update(request.parse());
            repository.save(updated);
            request = parseToRequest(updated);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar atualizar o cliente com id [" + id + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar atualizar o cliente " +
                    "com id [" + id + "].");
        }

        return request;
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O identificador informado [" + id + "] não é válido.");
            throw new IllegalArgumentException("O identificador informado [" + id + "] não é válido.");
        }

        basicFindById(id);

        List<Agenda> agenda = agendaRepository.findByCustomerId(id);
        if (!agenda.isEmpty()) {
            log.warn("Existem eventos agendados com o cliente de id [" + id + "]. Para Prosseguir com a operação " +
                    "de remoção do clinte, os eventos marcados com o cliente deverão ser removidos.");

            throw new IllegalArgumentException("Existem eventos agendados com o cliente de id [" + id + "]. " +
                    "Para Prosseguir com a operação de remoção do clinte, os eventos marcados com o cliente " +
                    "deverão ser removidos.");
        }

        try {
            repository.deleteById(id);

        } catch (Throwable e) {
            log.error("Houve um erro ao tentar remover o cliente com id [" + id + "]." +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());
            throw new InternalServereErrorException("Houve um erro ao tentar remover o cliente com id [" + id + "].");
        }
        log.info("Foi removido da base de dados o cliente com id [" + id + "].");
    }

    private Customer basicFindById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possivel encontrar o cliente com o identificador [" +
                        id + "] informado."));
    }

    private Customer basicFindByCnpj(String cnpj) {
        return repository.findByCnpj(cnpj)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o cliente com o CNPJ [" + cnpj + "] informado."));
    }

    private List<Customer> basicFindByNameContains(String name) {
        if (name == "") {
            log.warn("O nome [" + name + "] informado não é válido.");
            throw new IllegalArgumentException("O nome [" + name + "] informado não é válido.");
        }

        String WithoutUnderscore = replaceToSpace(name);
        List<Customer> found = repository.findByFullnameContains(WithoutUnderscore);

        if (found.isEmpty()) {
            log.warn("Não foi possivel encontrar clientes que contenham o nome [" + name + "].");
            throw new NotFoundException("Não foi possivel encontrar clientes que contenham o nome [" + name + "].");
        }

        return found;
    }

    private List<Customer> basicFindByFoundation(String foundation) {
        if (foundation == "" || !isDate(foundation)) {
            log.warn("A data [" + foundation + "] de foundação não é válida.");
            throw new IllegalArgumentException("A data [" + foundation + "] de foundação não é válida.");
        }

        LocalDate foundationDate = parseDate(foundation);
        List<Customer> found = repository.findByFoundation(foundationDate);

        if (found.isEmpty()) {
            log.warn("Não foi possivel encontrar o clientes com a data [" + foundation + "] de fundação.");
            throw new NotFoundException("Não foi possivel encontrar o clientes com a data [" + foundation +
                    "] de fundação.");
        }

        return found;
    }

    private List<Customer> basicFindByFoundationBetween(String start, String end) {
        if (start == "" || !isDate(start)) {
            log.warn("A consulta entre datas de fundação não é válida para a data [" + start + "] incial.");
            throw new IllegalArgumentException("A consulta entre datas de fundação não é válida para a data [" +
                    start + "] incial.");
        }

        if (end == "" || !isDate(end)) {
            log.warn("A consulta entre datas de fundação não é válida para a data [" + end + "] final.");
            throw new IllegalArgumentException("A consulta entre datas de fundação não é válida para a data [" +
                    end + "] final.");
        }

        LocalDate startDate = parseDate(start);
        LocalDate endDate = parseDate(end);
        List<Customer> found = repository.findByFoundationBetween(startDate, endDate);

        if (found.isEmpty()) {
            log.warn("Não foi possivel encontrar clientes entre as datas [" + start + "] e [" + end + "] de fundação.");
            throw new NotFoundException("Não foi possivel encontrar clientes entre as datas [" + start + "] e [" +
                    end + "] de fundação.");
        }

        return found;
    }

    private List<Customer> basicFindByRegistered(String registered) {
        boolean registeredIsValid = registered != null || registered != "";
        if (!registeredIsValid || !isDateTime(registered)) {
            log.warn("A data [" + registered + "] de registro informada não é válida.");
            throw new IllegalArgumentException("A data [" + registered + "] de registro informada não é válida.");
        }

        LocalDateTime registeredDateTime = parseDateTime(registered);
        List<Customer> found = repository.findByRegistered(registeredDateTime);

        if (found.isEmpty()) {
            log.warn("Não foi possivel encontrar clientes registrados na data [" + registered + "].");
            throw new NotFoundException("Não foi possivel encontrar clientes registrados na data [" + registered + "].");
        }

        return found;
    }

    private List<Customer> basicFindByRegisteredBetween(String start, String end) {
        if (start == "" || !isDateTime(start)) {
            log.warn("A consulta entre datas de registro não é válida para a data [" + start + "] incial.");
            throw new IllegalArgumentException("A consulta entre datas de registro não é válida para a data [" + start +
                    "] incial.");
        }

        if (end == "" || !isDateTime(end)) {
            log.warn("A consulta entre datas de registro não é válida para a data [" + end + "] final.");
            throw new IllegalArgumentException("A consulta entre datas de registro não é válida para a data [" + end +
                    "] final.");
        }

        LocalDateTime startDateTime = parseDateTime(start);
        LocalDateTime endDateTime = parseDateTime(end);
        List<Customer> found = repository.findByRegisteredBetween(startDateTime, endDateTime);

        if (found.isEmpty()) {
            log.warn("Não foi possivel encontrar clientes registrados entre as datas [" + start + "] e [" + end + "]");
            throw new NotFoundException("Não foi possivel encontrar clientes registrados entre as datas [" + start +
                    "] e [" + end + "].");
        }
        return found;
    }

}
