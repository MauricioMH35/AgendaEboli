package br.com.eboli.controllers;

import br.com.eboli.controllers.assemblers.AgendaAssembler;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Agenda;
import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.models.responses.AgendaResponse;
import br.com.eboli.repositories.AgendaRepository;
import br.com.eboli.utils.DateFormatter;
import br.com.eboli.utils.StringFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/agenda")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgendaController {

    private final AgendaRepository repository;

    @PostMapping
    public ResponseEntity<AgendaResponse> save(@RequestBody AgendaRequest request) {
        if (request.equals(new AgendaRequest())) {
            throw new IllegalArgumentException(
                    "As informações não são validas para prosseguir com a operação.");
        }

        boolean enabledMeeting = repository.findByMarkedTo(
                DateFormatter.parseDateTime(request.getMarkedTo())).stream()
                .filter(a -> !a.getConcluded())
                .collect(Collectors.toList())
                .isEmpty();

        if (!enabledMeeting) {
            throw new IllegalArgumentException(
                    "Não é possivel marcar uma reunião, pois a data informada ocorrera outro evento.");
        }

        Long id = repository.save(AgendaRequest.parseToModel(request)).getId();
        request.setId(id);
        return ResponseEntity.ok(AgendaAssembler.toModel(AgendaResponse.parse(request)));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<AgendaResponse>> findAll() {
        List<AgendaResponse> responses = repository.findAll().stream()
                .map(a -> AgendaResponse.parse(a))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar reuniões marcadas.");
        }

        return ResponseEntity.ok(AgendaAssembler.toCollectionModel(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponse> findById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException(
                    "O identificar informado não é valido.");
        }

        AgendaResponse response = AgendaResponse.parse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o evendo com o identificador informado.")));
        return ResponseEntity.ok(AgendaAssembler.toModel(response));
    }

    @GetMapping("/find")
    public ResponseEntity<CollectionModel<AgendaResponse>> find(@RequestParam Map<String, String> params) {
        if (params.equals(null)) {
            throw new IllegalArgumentException(
                    "A informação não é valida para realizar uma busca por eventos.");
        }

        Iterable<AgendaResponse> responses = null;
        if (params.containsKey("title")) {
            responses = findByTitleContains(params.get("title"));

        } else if (params.containsKey("marked")) {
            responses = findByMarkedTo(params.get("marked"));

        } else if (params.containsKey("marked-start") && params.containsKey("marked-end")) {
            responses = findByMarkedToBetween(
                    params.get("marked-start"),
                    params.get("marked-end"));

        } else if (params.containsKey("concluded")) {
            responses = findByConcluded(params.get("concluded"));

        } else {
            throw new IllegalArgumentException(
                    "O parâmetro informado não é valido.");
        }

        return ResponseEntity.ok(AgendaAssembler.toCollectionModel(responses));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendaResponse> markConcluded(@PathVariable Long id) {
        AgendaResponse response = AgendaResponse.parse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o evento para marcar como conluído.")));
        repository.markConcluded(id);
        response.setConcluded(true);

        return ResponseEntity.ok(AgendaAssembler.toModel(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponse> updateById(@PathVariable Long id,
                                                     @RequestBody AgendaRequest request) {
        if (id.equals(null) || request.equals(new AgendaRequest())) {
            throw new IllegalArgumentException(
                    "As informações não são validas para prosseguir com o processo de atualização.");
        }

        Agenda updated = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o evento informado pelo identificador."))
                .updateAgenda(request);
        repository.save(updated);

        return ResponseEntity.ok(AgendaAssembler.toModel(AgendaResponse.parse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        if (id.equals(null)) {
            throw new IllegalArgumentException("O identificador não valido para prosseguir com a operação.");
        }

        Agenda agenda = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi possivel encontrar o evento agendado."));
        repository.delete(agenda);
        return ResponseEntity.noContent().build();
    }

    private CollectionModel<AgendaResponse> findByTitleContains(String title) {
        if (title.equals(null) || title == "") {
            throw new IllegalArgumentException("O titulo informado não é valido.");
        }

        String titleWithoutUnderscore = StringFormatter.replaceUnderscoreBySpace(title);
        Iterable<AgendaResponse> responses = repository.findByTitleContains(titleWithoutUnderscore).stream()
                .map(a -> AgendaResponse.parse(a))
                .collect(Collectors.toList());
        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar eventos agendados que contenham o titulo indicado.");
        }

        return CollectionModel.of(responses);
    }

    private CollectionModel<AgendaResponse> findByMarkedTo(String markedTo) {
        if (markedTo.equals(null) || markedTo == "") {
            throw new IllegalArgumentException(
                    "A data marcada para o evento não é valida.");
        }

        LocalDateTime markedToDateTime = DateFormatter.parseDateTime(
                StringFormatter.replaceUnderscoreBySpace(markedTo));
        Iterable<AgendaResponse> responses = repository.findByMarkedTo(markedToDateTime)
                .stream()
                .map(a -> AgendaResponse.parse(a))
                .collect(Collectors.toList());
        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar eventos agendados para a data informada");
        }

        return CollectionModel.of(responses);
    }

    private CollectionModel<AgendaResponse> findByMarkedToBetween(
            String markedStart, String markedEnd) {
        boolean checkValid = markedStart.equals(null) || markedStart == "" || markedEnd.equals(null) || markedEnd == "";
        if (checkValid) {
            throw new IllegalArgumentException(
                    "As datas informadas não são validas.");
        }

        LocalDateTime startDateTime = DateFormatter.parseDateTime(
                StringFormatter.replaceUnderscoreBySpace(markedStart));
        LocalDateTime endDateTime = DateFormatter.parseDateTime(
                StringFormatter.replaceUnderscoreBySpace(markedEnd));
        Iterable<AgendaResponse> responses = repository.findByMarkedToBetween(startDateTime, endDateTime).stream()
                .map(a -> AgendaResponse.parse(a))
                .collect(Collectors.toList());

        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar evendos agendados entre as datas indicadas.");
        }

        return CollectionModel.of(responses);
    }

    private CollectionModel<AgendaResponse> findByConcluded(String concluded) {
        if (concluded.equals(null) || concluded == "") {
            throw new IllegalArgumentException(
                    "A infomação não é valida para determinar se o evendo foi concluido ou não.");
        }

        boolean concludedBool = Boolean.parseBoolean(concluded);

        Iterable<AgendaResponse> responses = repository.findByConcluded(concludedBool).stream()
                .map(a -> AgendaResponse.parse(a))
                .collect(Collectors.toList());
        if (!responses.iterator().hasNext()) {
            throw new NotFoundException(
                    "Não foi possivel encontrar eventos checados como a informação passada.");
        }

        return CollectionModel.of(responses);
    }
}
