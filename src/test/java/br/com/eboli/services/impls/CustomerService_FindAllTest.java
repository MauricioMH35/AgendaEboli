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
public class CustomerService_FindAllTest {

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
                        .foundation(LocalDate.of(1990, 01, 8))
                        .registered(LocalDateTime.of(2017, 6, 23, 15, 32, 13))
                        .build(),
                Customer.builder()
                        .id(2l)
                        .fullname("Kamilly e Marlene Casa Noturna ME")
                        .cnpj("86678081000107")
                        .foundation(LocalDate.of(1989, 12, 03))
                        .registered(LocalDateTime.of(2021, 9, 9, 9, 45, 01))
                        .build(),
                Customer.builder()
                        .id(3l)
                        .fullname("Filipe e Alexandre Publicidade e Propaganda Ltda")
                        .cnpj("65203917000139")
                        .foundation(LocalDate.of(2001, 05, 21))
                        .registered(LocalDateTime.of(2021, 9, 18, 13, 1, 23))
                        .build(),
                Customer.builder()
                        .id(4l)
                        .fullname("Fabiana e Jennifer Eletrônica ME")
                        .cnpj("46989866000178")
                        .foundation(LocalDate.of(1960, 06, 12))
                        .registered(LocalDateTime.of(2022, 10, 8, 8, 12, 32))
                        .build(),
                Customer.builder()
                        .id(5l)
                        .fullname("Giovanni e Leandro Adega Ltda")
                        .cnpj("48847337000165")
                        .foundation(LocalDate.of(1978, 11, 07))
                        .registered(LocalDateTime.of(2022, 12, 3, 13, 32, 21))
                        .build()
        );

        responses = List.of(
                CustomerResponse.builder()
                        .id(1l)
                        .fullname("Bruna e Calebe Alimentos Ltda")
                        .cnpj("31449057000104")
                        .foundation("1990.01.08")
                        .registered("2017.06.23 15-32-13")
                        .build(),
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
                        .build(),
                CustomerResponse.builder()
                        .id(4l)
                        .fullname("Fabiana e Jennifer Eletrônica ME")
                        .cnpj("46989866000178")
                        .foundation("1960.06.12")
                        .registered("2022.10.08 08-12-32")
                        .build(),
                CustomerResponse.builder()
                        .id(5l)
                        .fullname("Giovanni e Leandro Adega Ltda")
                        .cnpj("48847337000165")
                        .foundation("1978.11.07")
                        .registered("2022.12.03 13-32-21")
                        .build()
        );
    }

    @Test
    @DisplayName("Whe Find All Customer And Returns Success")
    void whenFindAllCustomerAndReturnSuccess() {
        when(repository.findAll()).thenReturn(models);
        Iterable<CustomerResponse> actual = underTest.findAll();

        verify(repository, times(1)).findAll();
        assertEquals(responses, actual);
    }

    @Test
    @DisplayName("When Find All And Return Not Found Exception")
    void WhenFindAllAndReturnNotFoundException() {
        assertThatThrownBy(() -> underTest.findAll())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes cadastrados.");
    }

}
