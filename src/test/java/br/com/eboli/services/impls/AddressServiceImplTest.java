package br.com.eboli.services.impls;

import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Address;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.AddressRequest;
import br.com.eboli.repositories.AddressRepository;
import br.com.eboli.services.impls.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl underTest;

    @Mock
    private AddressRepository repository;

    private Address model;
    private Address returnModel;
    private Address updatingModel;

    private AddressRequest request;
    private AddressRequest returnRequest;

    @BeforeEach
    void setUp() {

        model = Address.builder()
                .publicPlace("Rua Joaquim Sérvolo")
                .number(621)
                .complement("AP23")
                .neighborhood("Jardim Petrópolis")
                .city("Piracicaba")
                .state("SP")
                .zipCode("13420667")
                .customer(Customer.builder().id(2).build())
                .build();

        returnModel = Address.builder()
                .id(2)
                .publicPlace("Rua Joaquim Sérvolo")
                .number(621)
                .complement("AP23")
                .neighborhood("Jardim Petrópolis")
                .city("Piracicaba")
                .state("SP")
                .zipCode("13420667")
                .customer(Customer.builder().id(2).build())
                .build();

        updatingModel = Address.builder()
                .id(2)
                .publicPlace("Rua Duque de Caxias")
                .number(444)
                .complement("A123")
                .neighborhood("Serra das Lagoas")
                .city("Campinas")
                .state("SP")
                .zipCode("11608545")
                .customer(Customer.builder().id(1).build())
                .build();

        request = AddressRequest.builder()
                .publicPlace("Rua Joaquim Sérvolo")
                .number(621)
                .complement("AP23")
                .neighborhood("Jardim Petrópolis")
                .city("Piracicaba")
                .state("SP")
                .zipCode("13420667")
                .customerId(2)
                .build();

        returnRequest = AddressRequest.builder()
                .id(2)
                .publicPlace("Rua Joaquim Sérvolo")
                .number(621)
                .complement("AP23")
                .neighborhood("Jardim Petrópolis")
                .city("Piracicaba")
                .state("SP")
                .zipCode("13420667")
                .customerId(2)
                .build();

    }

    @Test
    @DisplayName("When Save And Returns Successful")
    void save_Successful() {
        when(repository.save(model)).thenReturn(returnModel);

        AddressRequest actual = underTest.save(request);
        verify(repository, times(1)).save(model);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Save And Returns Illegal Argument Exception")
    void save_IllegalArgumentException() {
        AddressRequest requestBlank = AddressRequest.builder().build();
        assertThatThrownBy(() -> underTest.save(requestBlank))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As informações do endereço não são válidas.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Successful")
    void findById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        AddressRequest actual = underTest.findById(id);
        verify(repository, times(1)).findById(id);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Id Null")
    void findById_IllegalArgumentExceptionIdNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Id Less Or Equal Zero")
    void findById_IllegalArgumentExceptionIdLessOrEqualZero() {
        Integer id = 0;
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found Exception")
    void findById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o endereço com o identificador [" +
                        id + "]");
    }

    @Test
    @DisplayName("When Find All And Returns Successful")
    void findAll_Successful() {
        when(repository.findAll()).thenReturn(List.of(returnModel));

        List<AddressRequest> expected = List.of(returnRequest);
        List<AddressRequest> actual = underTest.findAll();
        verify(repository, times(1)).findAll();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find All And Returns Not Found Exception")
    void findAll_NotFoundException() {
        when(repository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findAll())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados endereços cadastrados.");
    }

    @Test
    @DisplayName("When Find By Zipcode And Returns Succesful")
    void findByZipCode_Succesful() {
        String zipcode = "13420667";
        when(repository.findByZipCode(zipcode)).thenReturn(List.of(returnModel));

        List<AddressRequest> expected = List.of(returnRequest);
        List<AddressRequest> actual = underTest.findByZipCode(zipcode);
        verify(repository, times(1)).findByZipCode(zipcode);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Zipcode And Returns Illegal Argument Exception Null")
    void findByZipCode_IllegalArgumentExceptionNull() {
        String zipcode = null;

        assertThatThrownBy(() -> underTest.findByZipCode(zipcode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O CEP [" + zipcode + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Zipcode And Returns Illegal Argument Exception Blank")
    void findByZipCode_IllegalArgumentExceptionBlank() {
        String zipcode = "";

        assertThatThrownBy(() -> underTest.findByZipCode(zipcode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O CEP [" + zipcode + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Zipcode And Returns Illegal Argument Exception Less Eight")
    void findByZipCode_IllegalArgumentExceptionLessEight() {
        String zipcode = "13420";

        assertThatThrownBy(() -> underTest.findByZipCode(zipcode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O CEP [" + zipcode + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Zipcode And Returns Not Found Exception")
    void findByZipCode_NotFoundExceptionLessEight() {
        String zipcode = "13420667";
        when(repository.findByZipCode(zipcode)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByZipCode(zipcode))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados endereços com CEP [" + zipcode + "] informado.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Successful")
    void updateById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(updatingModel));
        when(repository.save(returnModel)).thenReturn(returnModel);

        AddressRequest actual = underTest.updateById(id, request);
        verify(repository, times(1)).save(returnModel);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Update By Id And Returns Illegal Argument Exception Id Null")
    void updateById_IllegalArgumentExceptionIdNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Illegal Argument Exception Id Less Or Equal Zero")
    void updateById_IllegalArgumentExceptionIdLessOrEqualZero() {
        Integer id = -1;
        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found Exception")
    void updateById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o endereço com o identificador [" +
                        id + "]");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Successful")
    void deleteById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        underTest.deleteById(id);
        verify(repository, times(1)).deleteById(id);
        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Id Null")
    void deleteById_IllegalArgumentExceptionIdNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Id Less Or Equal Zero")
    void deleteById_IllegalArgumentExceptionIdLessOrEqualZero() {
        Integer id = -1;
        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Not Found Exception")
    void deleteById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o endereço com o identificador [" +
                        id + "]");
    }

}