package br.com.eboli.models.requests;

import br.com.eboli.models.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static br.com.eboli.utils.DateFormatter.parseDate;
import static br.com.eboli.utils.DateFormatter.parseDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerRequest {

    private Long id;
    private String fullname;
    private String cnpj;
    private String foundation;
    private String registered;

    public Customer parse() {
        return Customer.builder()
                .id(this.id)
                .fullname(this.fullname)
                .cnpj(this.cnpj)
                .foundation(parseDate(this.foundation))
                .registered(parseDateTime(this.registered))
                .build();
    }

}
