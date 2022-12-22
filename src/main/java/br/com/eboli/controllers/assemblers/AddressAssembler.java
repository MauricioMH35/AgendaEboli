package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.AddressController;
import br.com.eboli.controllers.CustomerController;
import br.com.eboli.models.requests.AddressRequest;
import br.com.eboli.models.responses.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AddressAssembler {

    public static AddressResponse toModel(AddressRequest request) {
        AddressResponse response = AddressResponse.parse(request);

        response.add(linkTo(methodOn(AddressController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(AddressController.class)
                .deleteById(response.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("neighborhood", response.getNeighborhood()))
        ).withRel("by-neighborhood"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("city", response.getCity()))
        ).withRel("by-city"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("state", response.getNeighborhood()))
        ).withRel("by-state"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("zipcode", response.getNeighborhood()))
        ).withRel("by-zipcode"));
        response.add(linkTo(methodOn(AddressController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static AddressResponse toModel(AddressResponse response) {
        response.add(linkTo(methodOn(AddressController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(AddressController.class)
                .deleteById(response.getId())
        ).withRel("delete-by-id"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findById(response.getCustomerId())
        ).withRel("customer"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("neighborhood", response.getNeighborhood()))
        ).withRel("by-neighborhood"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("city", response.getCity()))
        ).withRel("by-city"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("state", response.getNeighborhood()))
        ).withRel("by-state"));
        response.add(linkTo(methodOn(AddressController.class)
                .find(Map.of("zipcode", response.getNeighborhood()))
        ).withRel("by-zipcode"));
        response.add(linkTo(methodOn(AddressController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static CollectionModel<AddressResponse> toCollectionModel(Iterable<AddressResponse> responses) {
        for (AddressResponse response : responses) {
            response.add(linkTo(methodOn(AddressController.class)
                    .findById(response.getId())
            ).withSelfRel());
            response.add(linkTo(methodOn(AddressController.class)
                    .deleteById(response.getId())
            ).withRel("delete-by-id"));
            response.add(linkTo(methodOn(AddressController.class)
                    .find(Map.of("neighborhood", response.getNeighborhood()))
            ).withRel("by-neighborhood"));
            response.add(linkTo(methodOn(AddressController.class)
                    .find(Map.of("city", response.getCity()))
            ).withRel("by-city"));
            response.add(linkTo(methodOn(AddressController.class)
                    .find(Map.of("state", response.getNeighborhood()))
            ).withRel("by-state"));
            response.add(linkTo(methodOn(AddressController.class)
                    .find(Map.of("zipcode", response.getNeighborhood()))
            ).withRel("by-zipcode"));
        }
        CollectionModel collection = CollectionModel.of(responses);
        collection.add(linkTo(methodOn(AddressController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return collection;
    }

}
