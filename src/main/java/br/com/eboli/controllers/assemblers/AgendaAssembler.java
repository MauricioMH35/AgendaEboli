package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.AgendaController;
import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.models.responses.AgendaResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AgendaAssembler {

    public static AgendaResponse toModel(AgendaRequest request) {
        AgendaResponse response = request.parseToResponse();

        response.add(linkTo(methodOn(AgendaController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("title", response.getTitle()))
        ).withRel("find-by-title"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("marked-to", response.getMarkedTo()))
        ).withRel("find-by-marked-to"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("concluded", response.getConcluded().toString()))
        ).withRel("find-by-concluded"));
        response.add(linkTo(methodOn(AgendaController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static CollectionModel<AgendaResponse> toCollection(List<AgendaRequest> requests) {
        Iterable<AgendaResponse> responses = requests.stream()
                .map(a -> {
                    AgendaResponse response = a.parseToResponse();

                    response.add(linkTo(methodOn(AgendaController.class)
                            .findById(response.getId())
                    ).withSelfRel());
                    response.add(linkTo(methodOn(AgendaController.class)
                            .find(Map.of("title", response.getTitle()))
                    ).withRel("find-by-title"));
                    response.add(linkTo(methodOn(AgendaController.class)
                            .find(Map.of("marked-to", response.getMarkedTo()))
                    ).withRel("find-by-marked-to"));
                    response.add(linkTo(methodOn(AgendaController.class)
                            .find(Map.of("concluded", response.getConcluded().toString()))
                    ).withRel("find-by-concluded"));

                    return response;
                })
                .collect(Collectors.toList());

        return CollectionModel.of(responses);
    }

}
