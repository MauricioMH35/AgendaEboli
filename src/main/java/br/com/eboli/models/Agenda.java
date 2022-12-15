package br.com.eboli.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "tbl_agenda")
public class Agenda implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(length = 32, nullable = false)
    private final String title;

    @Column
    private final String description;

    @Column(nullable = false)
    private final LocalDateTime markedTo;

    @Column
    private final Boolean concluded;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_agenda_customers",
            joinColumns = @JoinColumn(name = "agenda_id"),
            inverseJoinColumns = @JoinColumn(name = "customers_id"))
    private Set<Customer> customers = new LinkedHashSet<>();

    private Agenda() {
        this.id = null;
        this.title = null;
        this.description = null;
        this.markedTo = null;
        this.concluded = null;
    }

}
