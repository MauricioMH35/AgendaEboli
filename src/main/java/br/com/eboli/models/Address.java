package br.com.eboli.models;

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

}
