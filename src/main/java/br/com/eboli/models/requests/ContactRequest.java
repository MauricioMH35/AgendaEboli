package br.com.eboli.models.requests;

import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.responses.ContactResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class ContactRequest implements Serializable {

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

    public ContactResponse parseToResponse() {
        return ContactResponse.builder()
                .id(id)
                .type(type)
                .contact(contact)
                .customerId(customerId)
                .build();
    }

    public static ContactRequest parseToRequest(Contact contact) {
        return ContactRequest.builder()
                .id(contact.getId())
                .type(contact.getType())
                .contact(contact.getContact())
                .customerId(contact.getCustomer().getId())
                .build();
    }

    public Boolean fieldsAreBlank() {
        return type == null ||
                contact == null ||
                contact == "" ||
                customerId == null ||
                customerId <= 0;
    }

}
