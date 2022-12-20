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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerService_FindByCnpjTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private Customer model;
    private CustomerResponse response;

    @BeforeEach
    void serUp() {
        model = Customer.builder()
                .id(2l)
                .fullname("Kamilly e Marlene Casa Noturna ME")
                .cnpj("86678081000107")
                .foundation(LocalDate.of(1989, 12, 03))
                .registered(LocalDateTime.of(2021, 9, 9, 9, 45, 01))
                .build();

        response = CustomerResponse.builder()
                .id(2l)
                .fullname("Kamilly e Marlene Casa Noturna ME")
                .cnpj("86678081000107")
                .foundation("1989.12.03")
                .registered("2021.09.09 09-45-01")
                .build();
    }

    @Test
    @DisplayName("When Find Customer By Cnpj And Returns Success")
    void whenFindCustomerByCnpjAndReturnsSuccess() {
        when(repository.findByCnpj("86678081000107")).thenReturn(Optional.of(model));

        CustomerResponse actual = underTest.findByCnpj("86678081000107");
        verify(repository, times(1)).findByCnpj("86678081000107");

        assertEquals(response, actual);
    }

    @Test
    @DisplayName("When Find Customer By Null Cnpj And Returns Illegal Argument Exception")
    void whenFindByNullCnpjAndReturnsIlligalArgumentException() {
        assertThatThrownBy(() -> underTest.findByCnpj(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O CNPJ informado não é válido.");
    }

    @Test
    @DisplayName("When Find Customer By Empty String Cnpj And Returns Illegal Argument Exception")
    void whenFindCustomerByEmptyStringCnpjAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByCnpj(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O CNPJ informado não é válido.");
    }

    @Test
    @DisplayName("When Find Customer By Cnpj And Returns Not Found Exception")
    void whenFindCustomerByCnpjAndReturnsNotFoundException() {
         when(repository.findByCnpj(anyString())).thenReturn(Optional.empty());
         assertThatThrownBy(() -> underTest.findByCnpj("32824638000133"))
                 .isInstanceOf(NotFoundException.class)
                 .hasMessageContaining("Não foi possivel encontrar o cliente indicado pelo CNPJ.");
    }

}
