package br.com.eboli.models.responses;

import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContactResponse extends RepresentationModel<ContactResponse> {

    private Long id;
    private ContactType type;
    private String contact;
    private Customer customer;

}
