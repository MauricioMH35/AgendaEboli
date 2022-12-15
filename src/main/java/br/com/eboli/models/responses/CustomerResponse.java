package br.com.eboli.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerResponse extends RepresentationModel<CustomerResponse> {

    private Long id;
    private String fullname;
    private String cnpj;
    private LocalDate foundation;
    private LocalDateTime registered;

}
