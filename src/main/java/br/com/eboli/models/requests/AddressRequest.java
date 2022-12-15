package br.com.eboli.models.requests;

import br.com.eboli.models.Address;
import br.com.eboli.models.Customer;
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

    public Address parse() {
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

}
