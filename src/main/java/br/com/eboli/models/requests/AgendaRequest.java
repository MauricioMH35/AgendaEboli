package br.com.eboli.models.requests;

import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.models.responses.AgendaResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

import static br.com.eboli.utils.DateUtil.isDateTime;
import static br.com.eboli.utils.DateUtil.parseDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class AgendaRequest implements Serializable {

    public static final long serialVersionUID = 1l;

    @JsonProperty private Integer id;
    @JsonProperty private String title;
    @JsonProperty private String description;
    @JsonProperty private String markedTo;
    @JsonProperty private Boolean concluded;
    @JsonProperty private Integer customerId;

    public Agenda parse() {
        return Agenda.builder()
                .id(id)
                .title(title)
                .description(description)
                .markedTo(parseDateTime(markedTo))
                .concluded(concluded)
                .customer(Customer.builder().id(customerId).build())
                .build();
    }

    public AgendaResponse parseToResponse() {
        return AgendaResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .markedTo(markedTo)
                .concluded(concluded)
                .customerId(customerId)
                .build();
    }

    public static AgendaRequest parseToRequest(Agenda agenda) {
        return AgendaRequest.builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .description(agenda.getDescription())
                .markedTo(parseDateTime(agenda.getMarkedTo()))
                .concluded(agenda.getConcluded())
                .customerId(agenda.getCustomer().getId())
                .build();
    }

    public Boolean fieldsAreBlank() {
        return title == null ||
                markedTo == null ||
                !isDateTime(markedTo);
    }

}
