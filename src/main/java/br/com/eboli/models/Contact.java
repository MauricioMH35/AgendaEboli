package com.eboli.models;

import com.eboli.models.enums.ContactType;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "tbl_contacts")
public class Contact extends RepresentationModel<Contact> implements Serializable {

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

}
