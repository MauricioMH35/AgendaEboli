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
public class CustomerServiceImpl_FindByFoundationBetweenTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private List<Customer> models;
    private Iterable<CustomerResponse> responses;

    @BeforeEach
    void setUp() {
        models = List.of(Customer.builder()
                .id(1l)
                .fullname("Bruna e Calebe Alimentos Ltda")
                .cnpj("31449057000104")
                .foundation(LocalDate.of(1990, 1, 8))
                .registered(LocalDateTime.of(2017, 6, 23, 15, 32, 13))
                .build()
        );

        responses = List.of(CustomerResponse.builder()
                .id(1l)
                .fullname("Bruna e Calebe Alimentos Ltda")
                .cnpj("31449057000104")
                .foundation("1990.01.08")
                .registered("2017.06.23 15-32-13")
                .build()
        );
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Success")
    void whenFindByFoundationBetweenAndReturnsSuccess() {
        LocalDate start = LocalDate.of(1990, 01, 01);
        LocalDate end = LocalDate.of(2000, 01, 01);
        when(repository.findByFoundationBetween(start, end)).thenReturn(models);

        Iterable<CustomerResponse> actual = underTest.findByFoundationBetween("1990.01.01", "2000.01.01");
        verify(repository, times(1)).findByFoundationBetween(start, end);
        assertEquals(responses, actual);
    }

    @Test
    @DisplayName("When Find By Null Foundation Between And Returns Illegal Argument Exception")
    void whenFindByNullFoundationBetweenAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByFoundationBetween(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As datas de fundação não são validas.");
    }

    @Test
    @DisplayName("When Find By Not Valid Pattern Foundation Between And Returns Illegal Argument Exception")
    void whenFindByNotValidPatternFoundationBetweenAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByFoundationBetween("1994-01-01", "2023-01-01"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As datas de fundação não são validas.");
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Not Found Exception")
    void whenFindByFoundationBetweenAndReturnsNotFoundException() {
        LocalDate start = LocalDate.of(1800, 01, 01);
        LocalDate end = LocalDate.of(1900, 01, 01);
        when(repository.findByFoundationBetween(start, end)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByFoundationBetween("1800.01.01", "1900.01.01"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes entre as datas de fundação.");
    }

}
