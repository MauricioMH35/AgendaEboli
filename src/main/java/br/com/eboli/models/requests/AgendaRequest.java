package br.com.eboli.models.requests;

import br.com.eboli.models.Agenda;
import br.com.eboli.models.responses.AgendaResponse;
import br.com.eboli.models.utils.CustomerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static br.com.eboli.utils.DateFormatter.parseDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AgendaRequest {

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

    public AgendaResponse parseToResponse() {
        return AgendaResponse.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .markedTo(this.markedTo)
                .concluded(this.concluded)
                .customerIds(this.customerIds)
                .build();
    }

    public static Agenda parseToModel(AgendaRequest request) {
        return Agenda.builder()
                .id(request.id)
                .title(request.title)
                .description(request.description)
                .markedTo(parseDateTime(request.markedTo))
                .concluded(request.concluded)
                .customers(CustomerUtil.proccessToIds(request.customerIds))
                .build();
    }

    public static AgendaResponse parseToResponse(AgendaRequest request) {
        return AgendaResponse.builder()
                .id(request.id)
                .title(request.title)
                .description(request.description)
                .markedTo(request.markedTo)
                .concluded(request.concluded)
                .customerIds(request.customerIds)
                .build();
    }

    public static AgendaRequest parse(Agenda model) {
        return AgendaRequest.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .markedTo(parseDateTime(model.getMarkedTo()))
                .concluded(model.getConcluded())
                .customerIds(CustomerUtil.proccessToCustomerIds(model.getCustomers()))
                .build();
    }

    public static AgendaRequest parse(AgendaResponse response) {
        return AgendaRequest.builder()
                .id(response.getId())
                .title(response.getTitle())
                .description(response.getDescription())
                .markedTo(response.getMarkedTo())
                .concluded(response.getConcluded())
                .customerIds(response.getCustomerIds())
                .build();
    }

}
