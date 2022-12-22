package br.com.eboli.services.impls;

import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Customer;
import br.com.eboli.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerService_DeleteByIdTest {

    @InjectMocks private CustomerServiceImpl underTest;
    @Mock private CustomerRepository repository;

    private Customer model;

    @BeforeEach
    void setUp() {
        model = Customer.builder()
                .id(5l)
                .fullname("Giovanni e Leandro Adega Ltda")
                .cnpj("48847337000165")
                .foundation(LocalDate.of(1978, 11, 7))
                .registered(LocalDateTime.of(2022, 12, 3, 13, 32, 21))
                .build();
    }

    @Test
    @Transactional
    @DisplayName("When Delete By Id And Returns Success")
    void whenDeleteByIdAndReturnsSuccess() {
        when(repository.findById(5l)).thenReturn(Optional.of(model));
        underTest.deleteById(5l);
        verify(repository, times(1)).delete(model);
        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illlegal Argument Exception")
    void whenDeleteByIdAndReturnsIlllegalArgumentException() {
        assertThatThrownBy(() -> underTest.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificado informado não é valido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Not Found Exception")
    void whenDeleteByIdAndReturnsNotFoundException() {
        when(repository.findById(10l)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.deleteById(10l))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o cliente indicado pelo identificador.");
    }

}
