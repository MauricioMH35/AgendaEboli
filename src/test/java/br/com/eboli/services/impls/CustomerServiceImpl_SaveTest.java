package br.com.eboli.services.impls;

import br.com.eboli.exceptions.ConflictException;
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
class CustomerServiceImpl_SaveTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private Customer model;
    private CustomerRequest request;
    private CustomerResponse response;

    @BeforeEach
    void setUp() {
        model = Customer.builder()
                .id(6l)
                .fullname("Elza e Agatha Telecomunicações ME")
                .cnpj("68861391000172")
                .foundation(LocalDate.of(1990, 05, 22))
                .registered(LocalDateTime.of(2022, 12, 14, 00, 47, 00))
                .build();

        request = CustomerRequest.builder()
                .id(6l)
                .fullname("Elza e Agatha Telecomunicações ME")
                .cnpj("68861391000172")
                .foundation("1990.05.22")
                .registered("2022.12.14 00-47-00")
                .build();

        response = CustomerResponse.builder()
                .id(6l)
                .fullname("Elza e Agatha Telecomunicações ME")
                .cnpj("68861391000172")
                .foundation("1990.05.22")
                .registered("2022.12.14 00-47-00")
                .build();
    }

    @Test
    @DisplayName("When Save Customer And Returns Success")
    void whenSaveCustomerReturnsSuccessful() {
        when(repository.findByCnpj("68861391000172")).thenReturn(Optional.empty());
        when(repository.save(model)).thenReturn(model);

        CustomerResponse actual = underTest.save(request);

        verify(repository, times(1)).findByCnpj("68861391000172");
        verify(repository, times(1)).save(model);
        assertEquals(response, actual);
    }

    @Test
    @DisplayName("When Save Null Customer And Returns Illegal Argument Exception")
    void whenSaveNullCustomerAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.save(new CustomerRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As informações do cliente devem ser informadas para proceguir com o cadastro.");
    }

    @Test
    @DisplayName("When Save Customer With Existing CNPJ And Returns Conflict Exception")
    void whenSaveCustomerWithExistingCnpjAndReturnsIllegalArgumentException() {
        when(repository.findByCnpj("68861391000172")).thenReturn(Optional.of(model));
        assertThatThrownBy(() -> underTest.save(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Não é possível cadastrar o mesmo cliente.");
    }

}