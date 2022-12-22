package br.com.eboli.models.responses;

import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import static br.com.eboli.utils.DateFormatter.parseDate;
import static br.com.eboli.utils.DateFormatter.parseDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerResponse extends RepresentationModel<CustomerResponse> {

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

    public CustomerRequest parseToRequest() {
        return CustomerRequest.builder()
                .id(this.id)
                .fullname(this.fullname)
                .cnpj(this.cnpj)
                .foundation(this.foundation)
                .registered(this.registered)
                .build();
    }

    public static Customer parseToModel(CustomerResponse response) {
        return Customer.builder()
                .id(response.getId())
                .fullname(response.getFullname())
                .cnpj(response.getCnpj())
                .foundation(parseDate(response.getFoundation()))
                .registered(parseDateTime(response.getRegistered()))
                .build();
    }

    public static CustomerRequest parseToRequest(CustomerResponse response) {
        return CustomerRequest.builder()
                .id(response.getId())
                .fullname(response.getFullname())
                .cnpj(response.getCnpj())
                .foundation(response.getFoundation())
                .registered(response.getRegistered())
                .build();
    }

    public static CustomerResponse parse(Customer model) {
        return CustomerResponse.builder()
                .id(model.getId())
                .fullname(model.getFullname())
                .cnpj(model.getCnpj())
                .foundation(parseDate(model.getFoundation()))
                .registered(parseDateTime(model.getRegistered()))
                .build();
    }

    public static CustomerResponse parse(CustomerRequest request) {
        return CustomerResponse.builder()
                .id(request.getId())
                .fullname(request.getFullname())
                .cnpj(request.getCnpj())
                .foundation(request.getFoundation())
                .registered(request.getRegistered())
                .build();
    }

}
