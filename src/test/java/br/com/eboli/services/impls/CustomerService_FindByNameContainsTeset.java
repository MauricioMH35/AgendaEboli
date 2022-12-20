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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerService_FindByNameContainsTeset {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private Customer model;
    private Iterable<CustomerResponse> responses;

    @BeforeEach
    void setUp() {
        model = Customer.builder()
                .id(1l)
                .fullname("Bruna e Calebe Alimentos Ltda")
                .cnpj("31449057000104")
                .foundation(LocalDate.of(1990, 1, 8))
                .registered(LocalDateTime.of(2017, 6, 23, 15, 32, 13))
                .build();

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
    @DisplayName("When Find By Name Contains And Returns Success")
    void whenFindByNameContainsAndReturnsSuccess() {
        when(repository.findByFullnameContains("Calebe")).thenReturn(List.of(model));

        Iterable<CustomerResponse> actual = underTest.findByNameContains("Calebe");
        verify(repository, times(1)).findByFullnameContains("Calebe");

        assertEquals(responses, actual);
    }

    @Test
    @DisplayName("When Find By Null Name Contains And Returns Illegal Argument Exception")
    void whenFindByNullNameContainsAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByNameContains(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O nome informado não é valido para realizar a operação de busca.");
    }

    @Test
    @DisplayName("When Find By Empty Name Contains And Returns Illegal Argument Exception")
    void whenFindByNameContainsAndReturnsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.findByNameContains(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O nome informado não é valido para realizar a operação de busca.");
    }

    @Test
    @DisplayName("When Find By Name Contains And Returns Not Found Exception")
    void whenFindByNameContainsAndReturnsNotFoundException() {
        assertThatThrownBy(() -> underTest.findByNameContains("Fulano"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes que contenham o nome indicado.");
    }

}
