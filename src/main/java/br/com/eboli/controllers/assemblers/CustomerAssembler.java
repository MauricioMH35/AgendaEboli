package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.CustomerController;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.stereotype.Component;

import java.util.Map;

import static br.com.eboli.utils.StringFormatter.replaceWhiteSpaceByUnderscore;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CustomerAssembler {

    public CustomerResponse toModel(CustomerRequest request) {
        CustomerResponse response = CustomerResponse.parse(request);

        response.add(linkTo(methodOn(CustomerController.class)
                .findById(request.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(CustomerController.class)
                .deleteById(request.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findByCnpj(request.getCnpj())
        ).withRel("by-cnpj"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("name", request.getFullname()))
        ).withRel("by-name"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("foundation", replaceWhiteSpaceByUnderscore(request.getFoundation())))
        ).withRel("by-foundation"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("registered", replaceWhiteSpaceByUnderscore(request.getFoundation())))
        ).withRel("by-registered"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of(
                        "foundation-start", "1960.01.01",
                        "foundation-end", "2023.01.01"))
        ).withRel("by-foundation-between"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of(
                        "registered-start", "1960.01.01_00-00-00",
                        "registered-end", "2023.01.01_00-00-00"))
        ).withRel("by-registered-between"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));
        return response;
    }

    public CustomerResponse toModel(CustomerResponse response) {
        response.add(linkTo(methodOn(CustomerController.class)
                .findById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(CustomerController.class)
                .deleteById(response.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findByCnpj(response.getCnpj())
        ).withRel("by-cnpj"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("name", response.getFullname()))
        ).withRel("by-name"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("foundation", replaceWhiteSpaceByUnderscore(response.getFoundation())))
        ).withRel("by-foundation"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("registered", replaceWhiteSpaceByUnderscore(response.getFoundation())))
        ).withRel("by-registered"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of(
                        "foundation-start", "1960.01.01",
                        "foundation-end", "2023.01.01"))
        ).withRel("by-foundation-between"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of(
                        "registered-start", "1960.01.01_00-00-00",
                        "registered-end", "2023.01.01_00-00-00"))
        ).withRel("by-registered-between"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public CollectionModel<CustomerResponse> toCollectionModel(Iterable<CustomerResponse> responses) {
        for (CustomerResponse response : responses) {
            response.add(linkTo(methodOn(CustomerController.class)
                    .findById(response.getId())).withSelfRel());
            response.add(linkTo(methodOn(CustomerController.class)
                    .deleteById(response.getId())
            ).withRel("delete-by-id"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .findByCnpj(response.getCnpj())
            ).withRel("by-cnpj"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .find(Map.of("name", response.getFullname()))
            ).withRel("by-name"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .find(Map.of("foundation", replaceWhiteSpaceByUnderscore(response.getFoundation())))
            ).withRel("by-foundation"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .find(Map.of("registered", replaceWhiteSpaceByUnderscore(response.getFoundation())))
            ).withRel("by-registered"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .find(Map.of(
                            "foundation-start", "1960.01.01",
                            "foundation-end", "2023.01.01"))
            ).withRel("by-foundation-between"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .find(Map.of(
                            "registered-start", "1960.01.01_00-00-00",
                            "registered-end", "2023.01.01_00-00-00"))
            ).withRel("by-registered-between"));
            response.add(linkTo(methodOn(CustomerController.class)
                    .findAll()
            ).withRel(IanaLinkRelations.COLLECTION));
        }

        return CollectionModel.of(responses);
    }

}
