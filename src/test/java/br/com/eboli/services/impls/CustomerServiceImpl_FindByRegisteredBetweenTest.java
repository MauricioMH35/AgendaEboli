package br.com.eboli.services.impls;

import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Customer;
import br.com.eboli.models.responses.CustomerResponse;
import br.com.eboli.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerServiceImpl_FindByRegisteredBetweenTest {

    @InjectMocks
    private CustomerServiceImpl underTest;
    @Mock
    private CustomerRepository repository;

    private List<Customer> models;
    private Iterable<CustomerResponse> responses;

    @BeforeEach
    void setUp() {

        models = List.of(
                Customer.builder()
                        .id(2l)
                        .fullname("Kamilly e Marlene Casa Noturna ME")
                        .cnpj("86678081000107")
                        .foundation(LocalDate.of(1989, 12, 3))
                        .registered(LocalDateTime.of(2021, 9, 9, 9, 45, 01))
                        .build(),
                Customer.builder()
                        .id(3l)
                        .fullname("Filipe e Alexandre Publicidade e Propaganda Ltda")
                        .cnpj("65203917000139")
                        .foundation(LocalDate.of(2001, 5, 21))
                        .registered(LocalDateTime.of(2021, 9, 18, 13, 01, 23))
                        .build()
        );

        responses = List.of(
                CustomerResponse.builder()
                        .id(2l)
                        .fullname("Kamilly e Marlene Casa Noturna ME")
                        .cnpj("86678081000107")
                        .foundation("1989.12.03")
                        .registered("2021.09.09 09-45-01")
                        .build(),
                CustomerResponse.builder()
                        .id(3l)
                        .fullname("Filipe e Alexandre Publicidade e Propaganda Ltda")
                        .cnpj("65203917000139")
                        .foundation("2001.05.21")
                        .registered("2021.09.18 13-01-23")
                        .build()
        );

    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Success")
    void whenFindByRegisteredBetweenAndReturnsSuccess() {
        LocalDateTime start = LocalDateTime.of(2021, 1, 1, 15, 30, 00);
        LocalDateTime end = LocalDateTime.of(2022, 1, 1, 15, 30, 00);
        when(repository.findByRegisteredBetween(start, end)).thenReturn(models);

        Iterable<CustomerResponse> actual = underTest.findByRegisteredBetween("2021.01.01_15-30-00", "2022.01.01_15-30-00");
        verify(repository, times(1)).findByRegisteredBetween(start, end);
        assertEquals(responses, actual);
    }

    @Test
    @DisplayName("When Find By Null Registered Between And Returns Illegal Argument Exception")
    void whenFindByNullRegisteredBetweenAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByRegisteredBetween(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As datas de registro não são validas.");
    }

    @Test
    @DisplayName("When Find By Not Valid Pattern Registered Between And Returns Illegal Argument Exception")
    void whenFindByNotValidPatternRegisteredBetweenAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByRegisteredBetween("1960-01-01 15.30.00", "2000/01/01 15:30:00"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As datas de registro não são validas.");
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Not Found Exception")
    void whenFindByRegisteredBetweenAndReturnsNotFoundException() {
        LocalDateTime start = LocalDateTime.of(1960, 1, 1, 15, 30, 00);
        LocalDateTime end = LocalDateTime.of(2000, 1, 1, 15, 30, 00);
        when(repository.findByRegisteredBetween(start, end)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByRegisteredBetween("1960.01.01_15-30-00", "2000.01.01_15-30-00"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi encontrado clientes registrado entre as datas indicadas.");
    }

}
