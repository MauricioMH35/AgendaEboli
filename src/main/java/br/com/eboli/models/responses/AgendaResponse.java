package br.com.eboli.models.responses;

import br.com.eboli.models.Agenda;
import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.models.utils.CustomerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

import static br.com.eboli.utils.DateFormatter.parseDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AgendaResponse extends RepresentationModel<AgendaResponse> {

    private Long id;
    private String title;
    private String description;
    private String markedTo;
    private Boolean concluded;
    private Set<Long> customerIds;

    public Agenda parse() {
        return Agenda.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .markedTo(parseDateTime(this.markedTo))
                .concluded(this.concluded)
                .customers(CustomerUtil.proccessToIds(customerIds))
                .build();
    }

    public AgendaRequest parseToRequest() {
        return AgendaRequest.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .markedTo(this.markedTo)
                .concluded(this.concluded)
                .customerIds(this.customerIds)
                .build();
    }

    public static Agenda parseToModel(AgendaResponse response) {
        return Agenda.builder()
                .id(response.id)
                .title(response.title)
                .description(response.description)
                .markedTo(parseDateTime(response.markedTo))
                .concluded(response.concluded)
                .customers(CustomerUtil.proccessToIds(response.customerIds))
                .build();
    }

    public static AgendaRequest parseToResponse(AgendaResponse response) {
        return AgendaRequest.builder()
                .id(response.id)
                .title(response.title)
                .description(response.description)
                .markedTo(response.markedTo)
                .concluded(response.concluded)
                .customerIds(response.customerIds)
                .build();
    }

    public static AgendaResponse parse(Agenda model) {
        return AgendaResponse.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .markedTo(parseDateTime(model.getMarkedTo()))
                .concluded(model.getConcluded())
                .customerIds(CustomerUtil.proccessToCustomerIds(model.getCustomers()))
                .build();
    }

    public static AgendaResponse parse(AgendaRequest request) {
        return AgendaResponse.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .markedTo(request.getMarkedTo())
                .concluded(request.getConcluded())
                .customerIds(request.getCustomerIds())
                .build();
    }

}
