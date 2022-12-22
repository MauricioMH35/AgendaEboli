package br.com.eboli.models.responses;

import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.requests.ContactRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContactResponse extends RepresentationModel<ContactResponse> {

    private Long id;
    private ContactType type;
    private String contact;
    private Long customerId;

    public Contact parse() {
        return Contact.builder()
                .id(this.id)
                .type(this.type)
                .contact(this.contact)
                .customer(Customer.builder()
                        .id(this.customerId)
                        .fullname(null)
                        .cnpj(null)
                        .foundation(null)
                        .registered(null)
                        .build())
                .build();
    }

    public ContactRequest parseToRequest() {
        return ContactRequest.builder()
                .id(this.id)
                .type(this.type)
                .contact(this.contact)
                .customerId(this.customerId)
                .build();
    }

    public static Contact parseToModel(ContactResponse response) {
        return Contact.builder()
                .id(response.id)
                .type(response.type)
                .contact(response.contact)
                .customer(Customer.builder()
                        .id(response.customerId)
                        .fullname(null)
                        .cnpj(null)
                        .foundation(null)
                        .registered(null)
                        .build())
                .build();
    }

    public static ContactRequest parseToResponse(ContactResponse response) {
        return ContactRequest.builder()
                .id(response.id)
                .type(response.type)
                .contact(response.contact)
                .customerId(response.customerId)
                .build();
    }

    public static ContactResponse parse(Contact model) {
        return ContactResponse.builder()
                .id(model.getId())
                .type(model.getType())
                .contact(model.getContact())
                .customerId(model.getCustomer().getId())
                .build();
    }

    public static ContactResponse parse(ContactRequest request) {
        return ContactResponse.builder()
                .id(request.getId())
                .type(request.getType())
                .contact(request.getContact())
                .customerId(request.getCustomerId())
                .build();
    }

}
