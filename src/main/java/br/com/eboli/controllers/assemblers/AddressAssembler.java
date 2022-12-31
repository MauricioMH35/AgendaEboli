package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.AddressController;
import br.com.eboli.models.requests.AddressRequest;
import br.com.eboli.models.responses.AddressResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AddressAssembler {

    public static AddressResponse toModel(AddressRequest request) {
        AddressResponse response = request.parseToResponse();

        response.add(linkTo(methodOn(AddressController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(AddressController.class)
                .findByZipcode(response.getZipCode())
        ).withRel("find-by-zipcode"));
        response.add(linkTo(methodOn(AddressController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static CollectionModel<AddressResponse> toCollection(List<AddressRequest> requests) {
        Iterable<AddressResponse> responses = requests.stream()
                .map(a -> {
                    AddressResponse response = a.parseToResponse();

                    response.add(linkTo(methodOn(AddressController.class)
                            .findById(response.getId())
                    ).withSelfRel());
                    response.add(linkTo(methodOn(AddressController.class)
                            .findByZipcode(response.getZipCode())
                    ).withRel("find-by-zipcode"));

                    return response;
                })
                .collect(Collectors.toList());

        return CollectionModel.of(responses);
    }

}
