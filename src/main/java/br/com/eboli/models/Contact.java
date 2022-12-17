package br.com.eboli.models;

import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.requests.ContactRequest;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "tbl_contacts")
public class Contact implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private final ContactType type;

    @Column(nullable = false, unique = true)
    private final String contact;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Contact() {
        this.id = null;
        this.type = null;
        this.contact = null;
    }

    public Contact updateContact(ContactRequest request) {
        ContactType type = request.getType() != this.type ? request.getType() : this.type;
        String contact = request.getContact() != this.contact ? request.getContact() : this.contact;

        return Contact.builder()
                .id(this.id)
                .type(type)
                .contact(contact)
                .build();
    }

}
