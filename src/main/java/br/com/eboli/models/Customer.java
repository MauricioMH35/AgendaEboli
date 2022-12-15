package com.eboli.models;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
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

    private Customer() {
        this.id = null;
        this.fullname = null;
        this.cnpj = null;
        this.foundation = null;
        this.registered = null;
    }

}
