package br.com.eboli.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AgendaResponse extends RepresentationModel<AgendaResponse> {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime markedTo;
    private Boolean concluded;

}
