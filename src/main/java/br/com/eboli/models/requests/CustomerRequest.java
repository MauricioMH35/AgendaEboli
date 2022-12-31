package br.com.eboli.models.requests;

import br.com.eboli.models.Customer;
import br.com.eboli.models.responses.CustomerResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

import static br.com.eboli.utils.DateUtil.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class CustomerRequest implements Serializable {

    public static final long serialVersionUID = 1l;

    @JsonProperty private Integer id;
    @JsonProperty private String fullname;
    @JsonProperty private String cnpj;
    @JsonProperty private String foundation;
    @JsonProperty private String registered;

    public Customer parse() {
        return Customer.builder()
                .id(id)
                .fullname(fullname)
                .cnpj(cnpj)
                .foundation(parseDate(foundation))
                .registered(parseDateTime(registered))
                .build();
    }

    public CustomerResponse parseToResponse() {
        return CustomerResponse.builder()
                .id(id)
                .fullname(fullname)
                .cnpj(cnpj)
                .foundation(foundation)
                .registered(registered)
                .build();
    }

    public static CustomerRequest parseToRequest(Customer customer) {
        if (customer.getRegistered() != null) {
            return CustomerRequest.builder()
                    .id(customer.getId())
                    .fullname(customer.getFullname())
                    .cnpj(customer.getCnpj())
                    .foundation(parseDate(customer.getFoundation()))
                    .registered(parseDateTime(customer.getRegistered()))
                    .build();
        }

        return CustomerRequest.builder()
                .id(customer.getId())
                .fullname(customer.getFullname())
                .cnpj(customer.getCnpj())
                .foundation(parseDate(customer.getFoundation()))
                .build();
    }

    public Boolean fieldsAreBlank() {
        return this.fullname == null ||
                this.cnpj == null ||
                this.foundation == null ||
                this.foundation == "" ||
                !isDate(this.foundation);
    }

}
