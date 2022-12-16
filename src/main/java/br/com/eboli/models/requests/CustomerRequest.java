package br.com.eboli.models.requests;

import br.com.eboli.models.Customer;
import br.com.eboli.models.responses.CustomerResponse;
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

    public CustomerResponse parseToResponse() {
        return CustomerResponse.builder()
                .id(this.id)
                .fullname(this.fullname)
                .cnpj(this.cnpj)
                .foundation(this.foundation)
                .registered(this.registered)
                .build();
    }

    public static Customer parseToModel(CustomerRequest request) {
        return Customer.builder()
                .id(request.getId())
                .fullname(request.getFullname())
                .cnpj(request.getCnpj())
                .foundation(parseDate(request.getFoundation()))
                .registered(parseDateTime(request.getRegistered()))
                .build();
    }

    public static CustomerResponse parseToResponse(CustomerRequest request) {
        return CustomerResponse.builder()
                .id(request.getId())
                .fullname(request.getFullname())
                .cnpj(request.getCnpj())
                .foundation(request.getFoundation())
                .registered(request.getRegistered())
                .build();
    }

    public static CustomerRequest parse(Customer model) {
        return CustomerRequest.builder()
                .id(model.getId())
                .fullname(model.getFullname())
                .cnpj(model.getCnpj())
                .foundation(parseDate(model.getFoundation()))
                .registered(parseDateTime(model.getRegistered()))
                .build();
    }

    public static CustomerRequest parse(CustomerResponse response) {
        return CustomerRequest.builder()
                .id(response.getId())
                .fullname(response.getFullname())
                .cnpj(response.getCnpj())
                .foundation(response.getFoundation())
                .registered(response.getRegistered())
                .build();
    }

}
