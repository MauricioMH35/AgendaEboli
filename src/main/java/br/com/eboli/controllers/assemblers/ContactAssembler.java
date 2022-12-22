package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.ContactController;
import br.com.eboli.controllers.CustomerController;
import br.com.eboli.models.requests.ContactRequest;
import br.com.eboli.models.responses.ContactResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ContactAssembler {

    public static ContactResponse toModel(ContactResponse response) {
        response.add(linkTo(methodOn(CustomerController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(ContactController.class)
                .findByCustomerId(response.getCustomerId())
        ).withRel("by-customer-id"));
        response.add(linkTo(methodOn(CustomerController.class)
                .deleteById(response.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findById(response.getCustomerId()))
        .withRel("find-by-id"));
        response.add(linkTo(methodOn(ContactController.class)
                .findByContactType(response.getType().toString())
        ).withRel("find-by-type"));

        return response;
    }

    public static ContactResponse toModel(ContactRequest request) {
        ContactResponse response = ContactResponse.parse(request);

        response.add(linkTo(methodOn(CustomerController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(ContactController.class)
                .findByCustomerId(response.getCustomerId())
        ).withRel("by-customer-id"));
        response.add(linkTo(methodOn(CustomerController.class)
                .deleteById(response.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findById(response.getCustomerId()))
                .withRel("find-by-id"));
        response.add(linkTo(methodOn(ContactController.class)
                .findByContactType(response.getType().toString())
        ).withRel("find-by-type"));

        return response;
    }

    public static CollectionModel<ContactResponse> toCollectionModel(Iterable<ContactResponse> responses) {
        for (ContactResponse response : responses) {
            response.add(linkTo(methodOn(CustomerController.class)
                    .findById(response.getId())
            ).withSelfRel());
            response.add(linkTo(methodOn(ContactController.class)
                    .findByCustomerId(response.getCustomerId())
            ).withRel("by-customer-id"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .deleteById(response.getId())
            ).withRel("delete-by-id"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .findById(response.getCustomerId()))
                    .withRel("find-by-id"));
            response.add(linkTo(methodOn(ContactController.class)
                    .findByContactType(response.getType().toString())
            ).withRel("find-by-type"));
        }
        CollectionModel collection = CollectionModel.of(responses);

        return collection;
    }

}
