package br.com.eboli.controllers.assemblers;

import br.com.eboli.controllers.CustomerController;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.models.responses.CustomerResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.eboli.utils.StringUtil.replaceToUnderscore;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class CustomerAssembler {

    public static CustomerResponse toModel(CustomerRequest request) {
        CustomerResponse response = request.parseToResponse();

        response.add(linkTo(methodOn(CustomerController.class)
                .findById(response.getId())
        ).withSelfRel());
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("name", replaceToUnderscore(response.getFullname())))
        ).withRel("find-by-name"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findByCnpj(response.getCnpj())
        ).withRel("find-by-cnpj"));
        response.add(linkTo(methodOn(CustomerController.class)
                .find(Map.of("foundation", response.getFoundation()))
        ).withRel("find-by-foundation"));
        response.add((linkTo(methodOn(CustomerController.class)
                .find(Map.of("registered", response.getRegistered())))
        ).withRel("find-by-registered"));
        response.add(linkTo(methodOn(CustomerController.class)
                .findAll()
        ).withRel(IanaLinkRelations.COLLECTION));

        return response;
    }

    public static CollectionModel<CustomerResponse> toCollection(List<CustomerRequest> requests) {
        Iterable<CustomerResponse> responses = requests.stream()
                .map(c -> {
                    CustomerResponse response = c.parseToResponse();

                    response.add(linkTo(methodOn(CustomerController.class)
                            .findById(response.getId())
                    ).withSelfRel());
                    response.add(linkTo(methodOn(CustomerController.class)
                            .find(Map.of("name", replaceToUnderscore(response.getFullname())))
                    ).withRel("find-by-name"));
                    response.add(linkTo(methodOn(CustomerController.class)
                            .findByCnpj(response.getCnpj())
                    ).withRel("find-by-cnpj"));
                    response.add(linkTo(methodOn(CustomerController.class)
                            .find(Map.of("foundation", response.getFoundation()))
                    ).withRel("find-by-foundation"));
                    response.add((linkTo(methodOn(CustomerController.class)
                            .find(Map.of("registered", response.getRegistered())))
                    ).withRel("find-by-registered"));

                    return response;
                })
                .collect(Collectors.toList());

        return CollectionModel.of(responses);
    }

}
