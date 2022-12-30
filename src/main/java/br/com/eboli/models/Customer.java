package br.com.eboli.models;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder

@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "tbl_customers")
public class Customer implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Integer id;

    @Column(unique = true, length = 128, nullable = false)
    private final String fullname;

    @Column(unique = true, length = 14, nullable = false)
    private final String cnpj;

    @Column(nullable = false)
    private final LocalDate foundation;

    @Column
    private final LocalDateTime registered;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Contact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<Agenda> agenda = new HashSet<>();

    public Customer() {
        this.id = null;
        this.fullname = null;
        this.cnpj = null;
        this.foundation = null;
        this.registered = null;
    }

    public Customer update(Customer updated) {
        String fullname = getFullname().compareTo(updated.getFullname()) == 0 ?
                getFullname() : updated.getFullname();

        String cnpj = getCnpj().compareTo(updated.getCnpj()) == 0 ?
                getCnpj() : updated.getCnpj();

        LocalDate foundation = getFoundation().compareTo(updated.getFoundation()) == 0 ?
                getFoundation() : updated.getFoundation();

        return Customer.builder()
                .id(this.id)
                .fullname(fullname)
                .cnpj(cnpj)
                .foundation(foundation)
                .registered(this.registered)
                .build();
    }

}
