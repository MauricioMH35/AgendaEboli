package br.com.eboli.models.requests;

import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
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

}
