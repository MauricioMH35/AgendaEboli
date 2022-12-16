package br.com.eboli.models.requests;

import br.com.eboli.models.Address;
import br.com.eboli.models.Customer;
import br.com.eboli.models.responses.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressRequest {

    private Long id;
    private String publicPlace;
    private Integer number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private Long customerId;

    public Address parseToModel() {
        return Address.builder()
                .id(this.id)
                .publicPlace(this.publicPlace)
                .number(this.number)
                .complement(this.complement)
                .neighborhood(this.neighborhood)
                .city(this.city)
                .state(this.state)
                .zipCode(this.zipCode)
                .customer(Customer.builder()
                        .id(this.customerId)
                        .fullname(null)
                        .cnpj(null)
                        .foundation(null)
                        .registered(null)
                        .build())
                .build();
    }

    public AddressResponse parseToResponse() {
        return AddressResponse.builder()
                .id(this.id)
                .publicPlace(this.publicPlace)
                .number(this.number)
                .complement(this.complement)
                .neighborhood(this.neighborhood)
                .city(this.city)
                .state(this.state)
                .zipCode(this.zipCode)
                .customerId(this.customerId)
                .build();
    }

    public static Address parseToModel(AddressRequest request) {
        return Address.builder()
                .id(request.getId())
                .publicPlace(request.getPublicPlace())
                .number(request.getNumber())
                .complement(request.getComplement())
                .neighborhood(request.getNeighborhood())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .customer(Customer.builder()
                        .id(request.getCustomerId())
                        .fullname(null)
                        .cnpj(null)
                        .foundation(null)
                        .registered(null)
                        .build())
                .build();
    }

    public static AddressResponse parseToResponse(AddressRequest request) {
        return AddressResponse.builder()
                .id(request.getId())
                .publicPlace(request.getPublicPlace())
                .number(request.getNumber())
                .complement(request.getComplement())
                .neighborhood(request.getNeighborhood())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .customerId(request.getCustomerId())
                .build();
    }

    public static AddressRequest parse(Address model) {
        return AddressRequest.builder()
                .id(model.getId())
                .publicPlace(model.getPublicPlace())
                .number(model.getNumber())
                .complement(model.getComplement())
                .neighborhood(model.getNeighborhood())
                .city(model.getCity())
                .state(model.getState())
                .zipCode(model.getZipCode())
                .customerId(model.getCustomer().getId())
                .build();
    }

    public static AddressRequest parse(AddressResponse response) {
        return AddressRequest.builder()
                .id(response.getId())
                .publicPlace(response.getPublicPlace())
                .number(response.getNumber())
                .complement(response.getComplement())
                .neighborhood(response.getNeighborhood())
                .city(response.getCity())
                .state(response.getState())
                .zipCode(response.getZipCode())
                .customerId(response.getCustomerId())
                .build();
    }

}
