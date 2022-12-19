package br.com.eboli.models;

import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.models.utils.CustomerUtil;
import br.com.eboli.utils.DateFormatter;
import br.com.eboli.utils.StringFormatter;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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
@DynamicUpdate
@Entity
@Table(name = "tbl_agenda")
public class Agenda implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(length = 128, nullable = false)
    private final String title;

    @Column
    private final String description;

    @Column(nullable = false)
    private final LocalDateTime markedTo;

    @Column
    private final Boolean concluded;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tbl_agenda_customers",
            joinColumns = @JoinColumn(name = "agenda_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id"))
    private Set<Customer> customers = new LinkedHashSet<>();

    private Agenda() {
        this.id = null;
        this.title = null;
        this.description = null;
        this.markedTo = null;
        this.concluded = null;
    }

    public Agenda updateAgenda(AgendaRequest request) {
        LocalDateTime markedToRequest = DateFormatter.parseDateTime(
                StringFormatter.replaceUnderscoreBySpace(request.getMarkedTo()));

        String title = request.getTitle().equals(this.title) ? this.title : request.getTitle();
        String description = request.getDescription().equals(this.description) ? this.description : request.getDescription();
        LocalDateTime markedTo = markedToRequest.equals(this.markedTo) ? this.markedTo : markedToRequest;
        Boolean concluded = request.getConcluded().equals(this.concluded) ? this.concluded : request.getConcluded();

        return Agenda.builder()
                .id(this.id)
                .title(title)
                .description(description)
                .markedTo(markedTo)
                .concluded(concluded)
                .build();
    }

}
