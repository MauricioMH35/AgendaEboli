package br.com.eboli.models;

import br.com.eboli.models.enums.ContactType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder

@DynamicUpdate
@Entity
@Table(name = "tbl_contacts")
public class Contact implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Integer id;

    @Column(nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private ContactType type;

    @Column(unique = true, nullable = false)
    private final String contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    public Contact() {
        this.id = null;
        this.type = null;
        this.contact = null;
    }

    public Contact update(Contact updated) {
        ContactType type =
                getType().compareTo(updated.getType()) == 0 ?
                        getType() : updated.getType();

        String contact =
                getContact().compareTo(updated.getContact()) == 0 ?
                        getContact() : updated.getContact();

        Customer customer =
                getCustomer().equals(updated.getCustomer()) ?
                        getCustomer() : updated.getCustomer();

        return Contact.builder()
                .id(getId())
                .type(type)
                .contact(contact)
                .customer(customer)
                .build();
    }

}
