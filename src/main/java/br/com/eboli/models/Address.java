package br.com.eboli.models;

import br.com.eboli.models.requests.AddressRequest;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "tbl_addresses")
public class Address implements Serializable {

    public final static long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(nullable = false, length = 128)
    private final String publicPlace;

    @Column(nullable = false, length = 6)
    private final Integer number;

    @Column(length = 32)
    private final String complement;

    @Column(nullable = false, length = 128)
    private final String neighborhood;

    @Column(nullable = false, length = 32)
    private final String city;

    @Column(nullable = false, length = 2)
    private final String state;

    @Column(nullable = false, length = 14)
    private final String zipCode;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Address() {
        this.id = null;
        this.publicPlace = null;
        this.number = null;
        this.complement = null;
        this.neighborhood = null;
        this.city = null;
        this.state = null;
        this.zipCode = null;
    }

    public Address updateAddress(AddressRequest request) {
        Long id = request.getId();
        String publicPlace = request.getPublicPlace() != this.publicPlace ? request.getPublicPlace() : this.publicPlace;
        Integer number = request.getNumber() != this.number ? request.getNumber() : this.number;
        String complement = request.getComplement() != this.complement ? request.getComplement() : this.complement;
        String neighborhood = request.getNeighborhood() != this.neighborhood ? request.getNeighborhood() : this.neighborhood;
        String city = request.getCity() != this.city ? request.getCity() : this.city;
        String state = request.getState() != this.state ? request.getState() : this.state;
        String zipCode = request.getZipCode() != this.zipCode ? request.getZipCode() : this.zipCode;

        return Address.builder()
                .id(id)
                .publicPlace(publicPlace)
                .number(number)
                .complement(complement)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .customer(Customer.builder()
                        .id(request.getCustomerId())
                        .fullname(null)
                        .cnpj(null)
                        .foundation(null)
                        .registered(null)
                        .build())
                .build();
    }

}
