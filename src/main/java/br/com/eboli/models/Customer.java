package br.com.eboli.models;

import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.utils.DateFormatter;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
@DynamicUpdate
@Entity
@Table(name = "tbl_customers")
public class Customer implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(length = 64, nullable = false)
    private final String fullname;

    @Column(length = 14, unique = true, nullable = false)
    private final String cnpj;

    @Column
    private final LocalDate foundation;

    @Column
    private final LocalDateTime registered;

    @OneToOne(mappedBy = "customer")
    private Address address;

    private Customer() {
        this.id = null;
        this.fullname = null;
        this.cnpj = null;
        this.foundation = null;
        this.registered = null;
    }

    public Customer updateCustomer(CustomerRequest request) {
        LocalDate foundationRequest = DateFormatter.parseDate(request.getFoundation());
        LocalDateTime registeredRequest = DateFormatter.parseDateTime(request.getRegistered());

        String fullname = request.getFullname() != this.fullname ? request.getFullname() : this.fullname;
        String cnpj = request.getCnpj() != this.cnpj ? request.getCnpj() : this.cnpj;
        LocalDate foundation = foundationRequest != this.foundation ? foundationRequest : this.foundation;
        LocalDateTime registered = registeredRequest != this.registered ? registeredRequest : this.registered;

        return Customer.builder()
                .id(this.id)
                .fullname(fullname)
                .cnpj(cnpj)
                .foundation(foundation)
                .registered(registered)
                .build();
    }

}
