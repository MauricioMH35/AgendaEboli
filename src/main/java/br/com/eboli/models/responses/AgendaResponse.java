package br.com.eboli.models.responses;

import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.AgendaRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

import static br.com.eboli.utils.DateUtil.parseDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class AgendaResponse extends RepresentationModel<AgendaResponse> implements Serializable {

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

    public AgendaRequest parseToRequest() {
        return AgendaRequest.builder()
                .id(id)
                .title(title)
                .description(description)
                .markedTo(markedTo)
                .concluded(concluded)
                .customerId(customerId)
                .build();
    }

}
