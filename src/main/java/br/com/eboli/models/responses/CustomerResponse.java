package br.com.eboli.models.responses;

import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.CustomerRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

import static br.com.eboli.utils.DateUtil.parseDate;
import static br.com.eboli.utils.DateUtil.parseDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class CustomerResponse extends RepresentationModel<CustomerResponse> implements Serializable {

    public static final long serialVersionUID = 1l;

    @JsonProperty private Integer id;
    @JsonProperty private String fullname;
    @JsonProperty private String cnpj;
    @JsonProperty private String foundation;
    @JsonProperty private String registered;

    public Customer parse() {
        if (registered != null || registered != "") {
            return Customer.builder()
                    .id(id)
                    .fullname(fullname)
                    .cnpj(cnpj)
                    .foundation(parseDate(foundation))
                    .registered(parseDateTime(registered))
                    .build();
        }

        return Customer.builder()
                .id(id)
                .fullname(fullname)
                .cnpj(cnpj)
                .foundation(parseDate(foundation))
                .build();
    }

    public CustomerRequest parseToRequest() {
        return CustomerRequest.builder()
                .id(id)
                .fullname(fullname)
                .cnpj(cnpj)
                .foundation(foundation)
                .registered(registered)
                .build();
    }

}
