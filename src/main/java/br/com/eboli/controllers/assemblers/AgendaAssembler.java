package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.AgendaController;
import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.models.responses.AgendaResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AgendaAssembler {

    public static AgendaResponse toModel(AgendaResponse response) {
        response.add(linkTo(methodOn(AgendaController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(AgendaController.class)
                .deleteById(response.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("title", response.getTitle()))
        ).withRel("find-by-title"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("marked", response.getMarkedTo()))
        ).withRel("find-by-marked"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of(
                        "marked-start", response.getMarkedTo(),
                        "marked-end", "2023-05-01"))
        ).withRel("find-by-marked"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("concluded", response.getConcluded().toString()))
        ).withRel("find-by-concluded"));
        response.add(linkTo(methodOn(AgendaController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static AgendaResponse toModel(AgendaRequest request) {
        AgendaResponse response = AgendaResponse.parse(request);

        response.add(linkTo(methodOn(AgendaController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(AgendaController.class)
                .deleteById(response.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("title", response.getTitle()))
        ).withRel("find-by-title"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("marked", response.getMarkedTo()))
        ).withRel("find-by-marked"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of(
                        "marked-start", response.getMarkedTo(),
                        "marked-end", "2023-05-01"))
        ).withRel("find-by-marked"));
        response.add(linkTo(methodOn(AgendaController.class)
                .find(Map.of("concluded", response.getConcluded().toString()))
        ).withRel("find-by-concluded"));
        response.add(linkTo(methodOn(AgendaController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static CollectionModel<AgendaResponse> toCollectionModel(Iterable<AgendaResponse> responses) {
        for (AgendaResponse response : responses) {
            response.add(linkTo(methodOn(AgendaController.class)
                    .findById(response.getId())
            ).withSelfRel());
            response.add(linkTo(methodOn(AgendaController.class)
                    .deleteById(response.getId())
            ).withRel("delete-by-id"));
            response.add(linkTo(methodOn(AgendaController.class)
                    .find(Map.of("title", response.getTitle()))
            ).withRel("find-by-title"));
            response.add(linkTo(methodOn(AgendaController.class)
                    .find(Map.of("marked", response.getMarkedTo()))
            ).withRel("find-by-marked"));
            response.add(linkTo(methodOn(AgendaController.class)
                    .find(Map.of(
                            "marked-start", response.getMarkedTo(),
                            "marked-end", "2023-05-01"))
            ).withRel("find-by-marked"));
            response.add(linkTo(methodOn(AgendaController.class)
                    .find(Map.of("concluded", response.getConcluded().toString()))
            ).withRel("find-by-concluded"));
        }
        CollectionModel collection = CollectionModel.of(responses);

        return collection;
    }

}
