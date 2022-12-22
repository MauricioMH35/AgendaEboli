package br.com.eboli.services.impls;

import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.CustomerRequest;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerServiceImpl_UpdateByIdTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private Long id;
    private Customer model;
    private Customer found;
    private CustomerRequest request;
    private CustomerResponse response;

    @BeforeEach
    void setUp() {

        id = 5l;

        model = Customer.builder()
                .id(id)
                .fullname("Elza e Agatha Telecomunicações ME")
                .cnpj("68861391000172")
                .foundation(LocalDate.of(1990, 5, 22))
                .registered(LocalDateTime.of(2022, 12, 14, 00, 47, 00))
                .build();


        found = Customer.builder()
                .id(5l)
                .fullname("Giovanni e Leandro Adega Ltda")
                .cnpj("48847337000165")
                .foundation(LocalDate.of(1978, 11, 7))
                .registered(LocalDateTime.of(2022, 12, 3, 13, 32, 21))
                .build();

        request = CustomerRequest.builder()
                .fullname("Elza e Agatha Telecomunicações ME")
                .cnpj("68861391000172")
                .foundation("1990.05.22")
                .registered("2022.12.14 00-47-00")
                .build();

        response = CustomerResponse.builder()
                .id(5l)
                .fullname("Elza e Agatha Telecomunicações ME")
                .cnpj("68861391000172")
                .foundation("1990.05.22")
                .registered("2022.12.14 00-47-00")
                .build();

    }

    @Test
    @DisplayName("When Update By Id And Returns Success")
    void whenUpdateByIdAndReturnsSuccess() {
        when(repository.findById(id)).thenReturn(Optional.of(found));
        when(repository.save(model)).thenReturn(model);

        CustomerResponse actual = underTest.updateById(id, request);
        verify(repository, times(1)).save(model);
        assertEquals(response, actual);
    }

    @Test
    @DisplayName("When Update By Id And Returns Illegal Argument Exception")
    void whenUpdateByNullIdAndReturnsIllegalArgumentException() {
        CustomerRequest requestIllegal = new CustomerRequest();
        assertThatThrownBy(() -> underTest.updateById(null, requestIllegal))
            .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As informações passdas são invalidas.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found Exception")
    void whenUpdateByIdAndReturnsNotFoundException() {
        when(repository.findById(15l)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findById(15l))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o cliente indicado pelo identificador.");
    }

}
