package br.com.eboli.models;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder

@DynamicInsert
@Entity
@Table(name = "tbl_addresses")
public class Address implements Serializable {

    public static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Integer id;

    @Column(nullable = false, length = 128)
    private final String publicPlace;

    @Column(nullable = false, length = 6)
    private final Integer number;

    @Column(length = 12)
    private final String complement;

    @Column(nullable = false, length = 128)
    private final String neighborhood;

    @Column(nullable = false, length = 64)
    private final String city;

    @Column(nullable = false, length = 2)
    private final String state;

    @Column(nullable = false, length = 8)
    private final String zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer = new Customer();

    public Address() {
        this.id = null;
        this.publicPlace = null;
        this.number = null;
        this.complement = null;
        this.neighborhood = null;
        this.city = null;
        this.state = null;
        this.zipCode = null;
    }

    public Address update(Address updated) {
        String publicPlace = getPublicPlace().compareTo(updated.getPublicPlace()) == 0 ?
                getPublicPlace() : updated.getPublicPlace();

        Integer number = getNumber() == updated.getNumber() ?
                getNumber() : updated.getNumber();

        String complement = getComplement().compareTo(updated.getComplement()) == 0 ?
                getComplement() : updated.getComplement();

        String neighborhood = getNeighborhood().compareTo(updated.getNeighborhood()) == 0 ?
                getNeighborhood() : updated.getNeighborhood();

        String city = getCity().compareTo(updated.getCity()) == 0 ?
                getCity() : updated.getCity();

        String state = getState().compareTo(updated.getState()) == 0 ?
                getState() : updated.getState();

        String zipCode = getZipCode().compareTo(updated.getZipCode()) == 0 ?
                getZipCode() : updated.getZipCode();

        Customer customer = getCustomer().equals(updated.getCustomer()) ?
                getCustomer() : updated.getCustomer();

        return Address.builder()
                .id(getId())
                .publicPlace(publicPlace)
                .number(number)
                .complement(complement)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .customer(customer)
                .build();
    }

}
