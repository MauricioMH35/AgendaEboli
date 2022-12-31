package br.com.eboli.models.responses;

import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.requests.ContactRequest;
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
public class ContactResponse extends RepresentationModel<ContactResponse> implements Serializable {

    public static final long serialVersionUID = 1l;

    @JsonProperty private Integer id;
    @JsonProperty private ContactType type;
    @JsonProperty private String contact;
    @JsonProperty private Integer customerId;

    public Contact parse() {
        return Contact.builder()
                .id(id)
                .type(type)
                .contact(contact)
                .customer(Customer.builder().id(customerId).build())
                .build();
    }

    public ContactRequest parseToRequest() {
        return ContactRequest.builder()
                .id(id)
                .type(type)
                .contact(contact)
                .customerId(customerId)
                .build();
    }

}
