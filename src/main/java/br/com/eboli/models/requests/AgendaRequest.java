package br.com.eboli.models.requests;

import br.com.eboli.models.Agenda;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public Agenda parse() {
        return Agenda.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .markedTo(parseDateTime(this.markedTo))
                .concluded(this.concluded)
                .build();
    }

}
