package br.com.eboli.models.requests;

import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.responses.ContactResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContactRequest {

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

    public ContactResponse parseToResponse() {
        return ContactResponse.builder()
                .id(this.id)
                .type(this.type)
                .contact(this.contact)
                .customerId(this.customerId)
                .build();
    }

    public static Contact parseToModel(ContactRequest request) {
        return Contact.builder()
                .id(request.id)
                .type(request.type)
                .contact(request.contact)
                .customer(Customer.builder()
                        .id(request.customerId)
                        .fullname(null)
                        .cnpj(null)
                        .foundation(null)
                        .registered(null)
                        .build())
                .build();
    }

    public static ContactResponse parseToResponse(ContactRequest request) {
        return ContactResponse.builder()
                .id(request.id)
                .type(request.type)
                .contact(request.contact)
                .customerId(request.customerId)
                .build();
    }

    public static ContactRequest parse(Contact model) {
        return ContactRequest.builder()
                .id(model.getId())
                .type(model.getType())
                .contact(model.getContact())
                .customerId(model.getCustomer().getId())
                .build();
    }

    public static ContactRequest parse(ContactResponse response) {
        return ContactRequest.builder()
                .id(response.getId())
                .type(response.getType())
                .contact(response.getContact())
                .customerId(response.getCustomerId())
                .build();
    }

}
