package br.com.eboli.controllers;

import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.models.responses.AgendaResponse;
import br.com.eboli.services.AgendaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static br.com.eboli.controllers.assemblers.AgendaAssembler.toCollection;
import static br.com.eboli.controllers.assemblers.AgendaAssembler.toModel;

@Scope("singleton")
@RestController
@RequestMapping("/v1/api/agenda")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = {"*"}, maxAge = 3600)
public class AgendaController {

    private Logger log = LoggerFactory.getLogger(AgendaController.class);

    @Qualifier("agendaServiceImpl") private final AgendaService service;

    @PostMapping
    public ResponseEntity<AgendaResponse> save(@RequestBody AgendaRequest request) {
        return ResponseEntity.ok(
                toModel(
                        service.save(request)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                toModel(
                        service.findById(id)
                )
        );
    }

    @GetMapping
    public ResponseEntity<CollectionModel<AgendaResponse>> findAll() {
        return ResponseEntity.ok(
                toCollection(
                        service.findAll()
                )
        );
    }

    @GetMapping("/find")
    public ResponseEntity<CollectionModel<AgendaResponse>> find(
            @RequestParam Map<String, String> params) {
        if (params.containsKey("title")) {
            return ResponseEntity.ok(
                    toCollection(
                            service.findByTitleContains(params.get("title"))
                    )
            );

        } else if (params.containsKey("marked-to")) {
            return ResponseEntity.ok(
                    toCollection(
                            service.findByMarkedTo(params.get("marked-to"))
                    )
            );

        } else if (params.containsKey("marked-start") && params.containsKey("marked-end")) {
            return ResponseEntity.ok(
                    toCollection(
                        service.findByMarkedToBetween(
                                params.get("marked-start"),
                                params.get("marked-end")
                        )
                    )
            );

        } else if (params.containsKey("concluded")) {
            return ResponseEntity.ok(
                    toCollection(
                            service.findByConcluded(params.get("concluded"))
                    )
            );

        } else {
            throw new IllegalArgumentException(
                    "Não é possível realizar a operção de consulta com um parâmetro inválido.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendaResponse> markedConcluded(@PathVariable Integer id) {
        return ResponseEntity.ok(
                toModel(
                        service.markedConcluded(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponse> updateById(@PathVariable Integer id,
                                                     @RequestBody AgendaRequest request) {
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
