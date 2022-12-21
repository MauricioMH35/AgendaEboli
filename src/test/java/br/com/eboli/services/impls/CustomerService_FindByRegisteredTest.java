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
public class CustomerService_FindByRegisteredTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private List<Customer> models;
    private Iterable<CustomerResponse> responses;


    @BeforeEach
    void setUp() {

        models = List.of(
                Customer.builder()
                        .id(2l)
                        .fullname("Kamilly e Marlene Casa Noturna ME")
                        .cnpj("86678081000107")
                        .foundation(LocalDate.of(1989, 12, 03))
                        .registered(LocalDateTime.of(2021, 9, 9, 9, 45, 01))
                        .build()
        );

        responses = List.of(
                CustomerResponse.builder()
                        .id(2l)
                        .fullname("Kamilly e Marlene Casa Noturna ME")
                        .cnpj("86678081000107")
                        .foundation("1989.12.03")
                        .registered("2021.09.09 09-45-01")
                        .build()
        );

    }

    @Test
    @DisplayName("When Find By Registered And Returns Success")
    void whenFindByRegisteredAndReturnsSuccess() {
        LocalDateTime registered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        when(repository.findByRegistered(registered)).thenReturn(models);

        Iterable<CustomerResponse> actual = underTest.findByRegistered("2021.09.09_09-45-01");
        verify(repository, times(1)).findByRegistered(registered);
        assertEquals(responses, actual);
    }

    @Test
    @DisplayName("When Find By Null Registered And Returns Illegal Argument Exception")
    void whenFindByNullRegisteredAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByRegistered(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data de registro informada não é valida");
    }

    @Test
    @DisplayName("When Find By Not Valid Pattern Registered And Returns Illegal Argument Exception")
    void whenFindByNotValidPatternRegisteredAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByRegistered("1990-01-01_15:30:45"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data de registro informada não é valida");
    }

    @Test
    @DisplayName("When Find By Registered And Returns Not Found Exception")
    void whenFindByRegisteredAndReturnsNotFoundException() {
        LocalDateTime registered = LocalDateTime.of(1800, 01, 01, 15, 30, 45);
        when(repository.findByRegistered(registered)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByRegistered("1800.01.01_15-30-45"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não é possivel encontrar clientes registrados na data indicada.");
    }

}
