package br.com.eboli.models;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder

@DynamicInsert
@Entity
@Table(name = "tbl_agenda")
public class Agenda implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Integer id;

    @Column(nullable = false, length = 128)
    private final String title;

    @Column
    private final String description;

    @Column(nullable = false)
    private final LocalDateTime markedTo;

    @Column(nullable = false)
    private final Boolean concluded;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    public Agenda() {
        this.id = null;
        this.title = null;
        this.description = null;
        this.markedTo = null;
        this.concluded = null;
    }

    public Agenda update(Agenda updated) {
        String title = getTitle().compareTo(updated.getTitle()) == 0 ?
                getTitle() : updated.getTitle();

        String description = getDescription().compareTo(updated.getDescription()) == 0 ?
                getDescription() : updated.getDescription();

        LocalDateTime markedTo = getMarkedTo().compareTo(updated.getMarkedTo()) == 0 ?
                getMarkedTo() : updated.getMarkedTo();

        Boolean concluded = getConcluded().compareTo(updated.getConcluded()) == 0 ?
                getConcluded() : updated.getConcluded();

        return Agenda.builder()
                .id(getId())
                .title(title)
                .description(description)
                .markedTo(markedTo)
                .concluded(concluded)
                .customer(getCustomer())
                .build();
    }

    @Override
    public String toString() {
        return "Agenda{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", markedTo=" + markedTo +
                ", concluded=" + concluded +
                ", customer{" +
                "id=" + customer.getId() +
                "fullname='" + customer.getFullname() + '\'' +
                "cnpj='" + customer.getCnpj() + '\'' +
                "foudation='" + customer.getFoundation() + '\'' +
                "registered='" + customer.getRegistered() + '\'' +
                '}' +
                '}';
    }
}
