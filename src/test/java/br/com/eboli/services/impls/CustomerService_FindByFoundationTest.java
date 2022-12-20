package br.com.eboli.services.impls;

import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Customer;
import br.com.eboli.models.responses.CustomerResponse;
import br.com.eboli.repositories.CustomerRepository;
import br.com.eboli.utils.DateFormatter;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerService_FindByFoundationTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private List<Customer> models;
    private Iterable<CustomerResponse> responses;

    @BeforeEach
    void setUp() {
        models = List.of(
                Customer.builder()
                        .id(1l)
                        .fullname("Bruna e Calebe Alimentos Ltda")
                        .cnpj("31449057000104")
                        .foundation(LocalDate.of(1990, 1, 8))
                        .registered(LocalDateTime.of(2017, 6, 23, 15, 32, 13))
                        .build()
        );

        responses = List.of(
                CustomerResponse.builder()
                        .id(1l)
                        .fullname("Bruna e Calebe Alimentos Ltda")
                        .cnpj("31449057000104")
                        .foundation("1990.01.08")
                        .registered("2017.06.23 15-32-13")
                        .build()
        );
    }

    @Test
    @DisplayName("When Find By Foundation And Returns Success")
    void whenFindByFoundationAndReturnsSuccess() {
        LocalDate foundationTargetDate = LocalDate.of(1990, 1, 8);
        String foundantionTargetStr = "1990.01.08";

        when(repository.findByFoundation(foundationTargetDate))
                .thenReturn(models);

        Iterable<CustomerResponse> actual = underTest.findByFoundation(foundantionTargetStr);

        verify(repository, times(1)).findByFoundation(foundationTargetDate);
        assertEquals(responses, actual);
    }

    @Test
    @DisplayName("When Find By Empty Foundation And Returns Illegal Argument Exception")
    void whenFindByEmptyFoundationAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByFoundation(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data de fundação do cliente não é válida.");
    }

    @Test
    @DisplayName("When Find By Foundation Date Format Not Supported And Returns Illegal Argument Exception")
    void whenFindByFoundationDateFormatNotSupportedAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByFoundation("1800/01/01"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data de fundação do cliente não é válida.");
    }

    @Test
    @DisplayName("When Find By Foundation And Returns Not Found Exception")
    void whenFindByFoundationAndReturnsNotFoundException() {
        LocalDate foundateionDate = DateFormatter.parseDate("1800.01.01");
        when(repository.findByFoundation(foundateionDate)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByFoundation("1800.01.01"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes com data de fundação indicada.");
    }

}
