package br.com.eboli.models.responses;

import br.com.eboli.models.Address;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.AddressRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class AddressResponse extends RepresentationModel<AddressResponse> implements Serializable {

    public static final long serialVersionUID = 1l;

    @JsonProperty private Integer id;
    @JsonProperty private String publicPlace;
    @JsonProperty private Integer number;
    @JsonProperty private String complement;
    @JsonProperty private String neighborhood;
    @JsonProperty private String city;
    @JsonProperty private String state;
    @JsonProperty private String zipCode;
    @JsonProperty private Integer customerId;

    public Address parse() {
        return Address.builder()
                .id(id)
                .publicPlace(publicPlace)
                .number(number)
                .complement(complement)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .customer(Customer.builder().id(customerId).build())
                .build();
    }

    public AddressRequest parseToRequest() {
        return AddressRequest.builder()
                .id(id)
                .publicPlace(publicPlace)
                .number(number)
                .complement(complement)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .customerId(customerId)
                .build();
    }

}
