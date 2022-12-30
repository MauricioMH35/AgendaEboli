package br.com.eboli.models.requests;

import br.com.eboli.models.Address;
import br.com.eboli.models.Customer;
import br.com.eboli.models.responses.AddressResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class AddressRequest implements Serializable {

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

    public AddressResponse parseToResponse() {
        return AddressResponse.builder()
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

    public static AddressRequest parseToRequest(Address address) {
        return AddressRequest.builder()
                .id(address.getId())
                .publicPlace(address.getPublicPlace())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .customerId(address.getCustomer().getId())
                .build();
    }

    public Boolean fieldsAreBlank() {
        return getPublicPlace() == null ||
                getNumber() == null || getNumber() <= 0 ||
                getNeighborhood() == null ||
                getCity() == null ||
                getState() == null ||
                getZipCode() == null ||
                getCustomerId() == null;
    }


}
