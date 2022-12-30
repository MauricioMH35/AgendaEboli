package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.ContactController;
import br.com.eboli.models.requests.ContactRequest;
import br.com.eboli.models.responses.ContactResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ContactAssembler {

    public static ContactResponse toModel(ContactRequest request) {
        ContactResponse response = request.parseToResponse();

        response.add(linkTo(methodOn(ContactController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(ContactController.class)
                .findByContact(response.getContact())
        ).withRel("find-by-contact"));
        response.add(linkTo(methodOn(ContactController.class)
                .findByType(response.getType().name())
        ).withRel("find-by-type"));
        response.add(linkTo(methodOn(ContactController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static CollectionModel<ContactResponse> toCollection(List<ContactRequest> requests) {
        Iterable<ContactResponse> responses = requests.stream()
                .map(c -> {
                    ContactResponse response = c.parseToResponse();

                    response.add(linkTo(methodOn(ContactController.class)
                            .findById(response.getId())
                    ).withSelfRel());
                    response.add(linkTo(methodOn(ContactController.class)
                            .findByContact(response.getContact())
                    ).withRel("find-by-contact"));
                    response.add(linkTo(methodOn(ContactController.class)
                            .findByType(response.getType().name())
                    ).withRel("find-by-type"));

                    return response;
                })
                .collect(Collectors.toList());

        return CollectionModel.of(responses);
    }

}
