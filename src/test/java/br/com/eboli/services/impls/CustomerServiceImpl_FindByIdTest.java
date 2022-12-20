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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerServiceImpl_FindByIdTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private Customer model;
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

        response = CustomerResponse.builder()
                .id(6l)
                .fullname("Elza e Agatha Telecomunicações ME")
                .cnpj("68861391000172")
                .foundation("1990.05.22")
                .registered("2022.12.14 00-47-00")
                .build();
    }

    @Test
    @DisplayName("When Find Customer By Id And Retuns Success")
    void whenFindCustomerByIdAndReturnsSuccess() {
        when(repository.findById(6l)).thenReturn(Optional.of(model));

        CustomerResponse actual = underTest.findById(6l);
        verify(repository, times(1)).findById(6l);

        assertEquals(response, actual);
    }

    @Test
    @DisplayName("When Find Customer By Null Id And Returns Illegal Argument Exception")
    void whenFindCustomerByIdAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador deve ser informado para realizar a operação.");
    }

    @Test
    @DisplayName("When Find Customer By Zero Id And Returns Illegal Argument Exception")
    void whenFindCustomerByZeroIdAnsReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findById(0l))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador deve ser informado para realizar a operação.");
    }

    @Test
    @DisplayName("When Find Customer By Id And Returns Not Found Exception")
    void whenFindCustomerByIdAndReturnsNotFoundException() {
        when(repository.findById(10l)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findById(10l))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o cliente indicado pelo identificador.");
    }


}
