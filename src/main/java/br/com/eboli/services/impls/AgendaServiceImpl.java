package br.com.eboli.services.impls;

import br.com.eboli.exceptions.BadRequestException;
import br.com.eboli.exceptions.InternalServereErrorException;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Agenda;
import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.repositories.AgendaRepository;
import br.com.eboli.services.AgendaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.eboli.models.requests.AgendaRequest.parseToRequest;
import static br.com.eboli.utils.DateUtil.isDateTime;
import static br.com.eboli.utils.DateUtil.parseDateTime;
import static br.com.eboli.utils.StringUtil.isBooleanValid;
import static br.com.eboli.utils.StringUtil.replaceToSpace;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgendaServiceImpl implements AgendaService {

    private final Logger log = LoggerFactory.getLogger(AgendaServiceImpl.class);

    @Qualifier("agendaRepository")
    private final AgendaRepository repository;

    private final Clock clock;

    @Override
    public AgendaRequest save(AgendaRequest request) {
        if (request.fieldsAreBlank() || request.getCustomerId() <= 0) {
            log.warn("As informações do agendamento do evento não são válidos.");
            throw new IllegalArgumentException("As informações do agendamento do evento não são válidos.");
        }

        LocalDateTime markedTo = null;
        LocalDateTime dateTimeNow = LocalDateTime.now(clock);
        try {
            markedTo = parseDateTime(request.getMarkedTo());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar validar a data do cadastro do evento na agenda: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());
            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar validar a data do " +
                    "cadastro do evento na agenda.");
        }

        boolean isAfaterDateTimeNow = dateTimeNow.isAfter(markedTo);
        if (isAfaterDateTimeNow) {
            log.warn("A data marcada [" + markedTo + "] para o evento não pode ser anterior a data atual [" +
                    dateTimeNow + "]");

            throw new IllegalArgumentException("A data marcada [" + markedTo + "] para o evento não pode ser " +
                    "anterior a data atual [" + dateTimeNow + "]");
        }

        request.setConcluded(false);

        try {
            Integer id = repository.save(request.parse()).getId();
            request.setId(id);
            log.info("Um evento foi agendado com id [" + id + "] para a data [" + markedTo + "]");

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar cadastrar o evento na agenda: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());
            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar cadastrar o evento " +
                    "na agenda.");

        }

        return request;
    }

    @Override
    public AgendaRequest findById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O id [" + id + "] não é válido.");
            throw new IllegalArgumentException("O id [" + id + "] não é válido.");
        }

        AgendaRequest request = parseToRequest(basicFindById(id));
        log.info("Uma consulta foi realizada para encontrar o evento agendado com o Id [" + id + "].");

        return request;
    }

    @Override
    public List<AgendaRequest> findAll() {
        List<AgendaRequest> requests = new ArrayList<>();
        try {
            List<Agenda> agenda = repository.findAll();
            requests = agenda.stream()
                    .map(a -> parseToRequest(a))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar os eventos agendados: " +
                    "\tMessage: " + e.getMessage() + "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar os " +
                    "eventos agendados.");
        }

        if (requests.isEmpty()) {
            log.warn("Não foram encontrados evendos agendados.");
            throw new NotFoundException("Não foram encontrados evendos agendados.");
        }

        log.info("Uma consulta para listar os eventos agendados foi realizada.");
        return requests;
    }

    @Override
    public List<AgendaRequest> findByTitleContains(String title) {
        if (title == null || title == "" || title.contains(" ")) {
            log.warn("O titulo [" + title + "] não é válido.");
            throw new IllegalArgumentException("O titulo [" + title + "] não é válido.");
        }

        List<AgendaRequest> requests = new ArrayList<>();
        try {
            requests = repository.findByTitleContains(replaceToSpace(title))
                    .stream()
                    .map(a -> parseToRequest(a))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar o evento agendado com que possa conter " +
                    "titulo [" + title + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar o evento " +
                    "agendado com que possa conter titulo [" + title + "].");
        }

        if (requests.isEmpty()) {
            log.warn("Não foram encontrados eventos agendados que contenham o titulo [" + title + "] informado.");
            throw new NotFoundException("Não foram encontrados eventos agendados que contenham o titulo [" + title +
                    "] informada.");
        }

        return requests;
    }

    @Override
    public List<AgendaRequest> findByMarkedTo(String markedTo) {
        if (markedTo == null || markedTo == "" || !isDateTime(markedTo)) {
            log.warn("A data [" + markedTo + "] marcada para encontrar eventos agendados não é válida.");
            throw new IllegalArgumentException("A data [" + markedTo + "] marcada para encontrar eventos agendados " +
                    "não é válida.");
        }

        List<AgendaRequest> requests = new ArrayList<>();
        try {
            requests = repository.findByMarkedTo(parseDateTime(markedTo))
                    .stream()
                    .map(a -> parseToRequest(a))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar o evento agendado para [" + markedTo + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar o evento " +
                    "agendado para [" + markedTo + "].");
        }

        if (requests.isEmpty()) {
            log.warn("Não foram encontrados eventos agendados para a data [" + markedTo + "] informada.");
            throw new NotFoundException("Não foram encontrados eventos agendados para a data [" + markedTo +
                    "] informada.");
        }
        return requests;
    }

    @Override
    public List<AgendaRequest> findByMarkedToBetween(String markedStart, String markedEnd) {
        boolean markedStartStrNotValid =
                markedStart == null ||
                        markedStart == "" ||
                        !isDateTime(markedStart);

        boolean markedEndStrNotValid =
                markedEnd == null ||
                        markedEnd == "" ||
                        !isDateTime(markedEnd);

        if (markedStartStrNotValid && markedEndStrNotValid) {
            log.warn("As datas [" + markedStart + "] e [" + markedEnd + "] informadas para econtrar os eventos " +
                    "agendados não são válidas.");

            throw new IllegalArgumentException("As datas [" + markedStart + "] e [" + markedEnd +
                    "] informadas para econtrar os eventos agendados não são válidas.");
        }

        List<AgendaRequest> requests = new ArrayList<>();

        try {
            LocalDateTime datetimeStart = parseDateTime(markedStart);
            LocalDateTime datetimeEnd = parseDateTime(markedEnd);
            requests = repository.findByMarkedToBetween(datetimeStart, datetimeEnd)
                    .stream()
                    .map(a -> parseToRequest(a))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar eventos agendados para [" + markedStart
                    + "] e [" + markedEnd + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar eventos " +
                    "agendados para [" + markedStart + "] e [" + markedEnd + "]: ");
        }

        if (requests.isEmpty()) {
            log.warn("Não foram encontrados eventos agendados entre as datas [" + markedStart + "] e [" +
                    markedEnd + "].");

            throw new NotFoundException("Não foram encontrados eventos agendados entre as datas [" + markedStart +
                    "] e [" + markedEnd + "].");
        }

        return requests;
    }

    @Override
    public List<AgendaRequest> findByConcluded(String concluded) {
        boolean concludedNotValid =
                concluded == null ||
                        concluded == "" &&
                                (
                                        !concluded.equalsIgnoreCase("true") ||
                                                !concluded.equalsIgnoreCase("false")
                                );

        if (concludedNotValid) {
            log.warn("Não é válido a informação para realizar a operação.");
            throw new IllegalArgumentException("Não é válido a informação para realizar a operação.");
        }

        Boolean concludedBool = Boolean.parseBoolean(concluded);
        String markedConcludedStr = concludedBool ? "concluído" : "não concluído";
        List<AgendaRequest> requests = new ArrayList<>();

        try {
            requests = repository.findByConcluded(concludedBool)
                    .stream()
                    .map(a -> parseToRequest(a))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar buscar eventos agendados " + markedConcludedStr +
                    '.' +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar buscar " +
                    "eventos agendados " + markedConcludedStr + '.');
        }

        if (requests.isEmpty()) {
            log.warn("Não foram encontrados eventos agendados e " + markedConcludedStr + '.');
            throw new NotFoundException("Não foram encontrados eventos agendados e " + markedConcludedStr + '.');
        }

        return requests;
    }

    @Transactional
    @Override
    public AgendaRequest markedConcluded(Integer id) {
        if (id == null || id <= 0) {
            log.warn("O id [" + id + "] não é válido.");
            throw new IllegalArgumentException("O id [" + id + "] não é válido.");
        }

        Agenda agenda = basicFindById(id);

        LocalDateTime dateTimeNow = LocalDateTime.now(clock);
        boolean isValidMarkedConcluded = agenda.getMarkedTo().isBefore(dateTimeNow) ||
                agenda.getMarkedTo().isEqual(dateTimeNow);

        if (!isValidMarkedConcluded) {
            log.warn("Não é possivel marcar como concluido um evento que não ocorreu.");
            throw new BadRequestException("Não é possivel marcar como concluido um evento que não ocorreu.");
        }

        AgendaRequest request = parseToRequest(agenda);

        try {
            repository.markConcluded(id);
            request.setConcluded(true);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar marcar como evento concluído: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar marcar como evento " +
                    "concluído.");
        }

        return request;
    }

    @Override
    public AgendaRequest updateById(Integer id, AgendaRequest request) {
        if (id == null || id <= 0) {
            log.warn("O id [" + id + "] informado não é válido.");
            throw new IllegalArgumentException("O id [" + id + "] informado não é válido.");
        }

        Agenda found = basicFindById(id);

        try {
            request.setId(id);
            Agenda updated = found.update(request.parse());
            repository.save(updated);
            request = parseToRequest(updated);

        } catch (Exception e) {
            log.error("Houve um erro interno ao servidor ao tentar atualizar as informações do evento agendado " +
                    "com id [" + id + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());

            throw new InternalServereErrorException("Houve um erro interno ao servidor ao tentar atualizar " +
                    "as informações do evento agendado com id [" + id + "]: ");
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

        } catch (Exception e) {
            log.error("Houve um erro ao tentar remover o evento agendado com id [" + id + "]: " +
                    "\tMessage: " + e.getMessage() +
                    "\tCause: " + e.getCause());
            throw new InternalServereErrorException("Houve um erro ao tentar remover o evento agendado com id [" + id
                    + "].");
        }
        log.info("Foi removido da base de dados o evento agendado com id [" + id + "].");
    }

    private Agenda basicFindById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possivel encontrar o evento agendado com o id [" +
                        id + "] informado."));
    }

}
