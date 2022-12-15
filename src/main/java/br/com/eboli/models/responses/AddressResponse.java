package br.com.eboli.models.responses;

import br.com.eboli.models.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressResponse  extends RepresentationModel<AddressResponse> {

    private Long id;
    private String publicPlace;
    private Integer number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private Customer customer;

}
