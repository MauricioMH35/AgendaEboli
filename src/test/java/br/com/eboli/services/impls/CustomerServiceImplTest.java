package br.com.eboli.services.impls;

import br.com.eboli.exceptions.ConflictException;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.CustomerRequest;
import br.com.eboli.repositories.AgendaRepository;
import br.com.eboli.repositories.CustomerRepository;
import br.com.eboli.services.impls.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl underTest;

    @Mock
    private CustomerRepository repository;

    @Mock
    private AgendaRepository agendaRepository;

    private CustomerRequest request;
    private CustomerRequest returnRequest;

    private Customer model;
    private Customer returnModel;
    private Customer updatetingModel;

    private Agenda agendaReturnModel;

    @BeforeEach
    void setUp() {

        request = CustomerRequest.builder()
                .fullname("Kamilly e Marlene Casa Noturna ME")
                .cnpj("86678081000107")
                .foundation("1989-12-03")
                .registered("2021-09-09_09-45-01")
                .build();

        returnRequest = CustomerRequest.builder()
                .id(2)
                .fullname("Kamilly e Marlene Casa Noturna ME")
                .cnpj("86678081000107")
                .foundation("1989-12-03")
                .registered("2021-09-09_09-45-01")
                .build();

        model = Customer.builder()
                .fullname("Kamilly e Marlene Casa Noturna ME")
                .cnpj("86678081000107")
                .foundation(LocalDate.of(1989, 12, 03))
                .registered(LocalDateTime.of(2021, 9, 9, 9, 45, 01))
                .build();

        returnModel = Customer.builder()
                .id(2)
                .fullname("Kamilly e Marlene Casa Noturna ME")
                .cnpj("86678081000107")
                .foundation(LocalDate.of(1989, 12, 03))
                .registered(LocalDateTime.of(2021, 9, 9, 9, 45, 01))
                .build();

        updatetingModel = Customer.builder()
                .id(2)
                .fullname("Kamilly e Marlene Casa Noturna LTDA")
                .cnpj("86678081000107")
                .foundation(LocalDate.of(1989, 01, 01))
                .registered(LocalDateTime.of(2021, 9, 9, 9, 45, 01))
                .build();

        agendaReturnModel = Agenda.builder()
                .id(2)
                .title("Reunião com Kamilly e Marlene Casa Noturna ME")
                .description("Elaboração Sistema Controle Financeiro")
                .markedTo(LocalDateTime.of(2023, 1, 22, 15, 30, 00))
                .concluded(false)
                .customer(Customer.builder().id(2).build())
                .build();

    }

    @Test
    @DisplayName("Whe Save And Returns Successful")
    void save_Successful() {
        String cnpj = "86678081000107";
        when(repository.findByCnpj(cnpj)).thenReturn(Optional.empty());
        when(repository.save(model)).thenReturn(returnModel);

        CustomerRequest actual = underTest.save(request);
        verify(repository, times(1)).findByCnpj(cnpj);
        verify(repository, times(1)).save(model);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("Whe Save And Returns Illegal Argument Exception Fileds Are Blank")
    void save_IllegalArgumentExceptionFiledsAreBlank() {
        assertThatThrownBy(() -> underTest.save(CustomerRequest.builder().build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As informações do cliente não são válidas.");
    }

    @Test
    @DisplayName("Whe Save And Returns Conflict Exception Cnpj")
    void save_ConflictExceptionFiledsAreBlank() {
        String cnpj = "86678081000107";
        when(repository.findByCnpj(cnpj)).thenReturn(Optional.of(returnModel));

        assertThatThrownBy(() -> underTest.save(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("O cliente com o CNPJ [" + cnpj + "] informado não pode ser cadastrado, " +
                        "ja possui um cadastro.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Successful")
    void findById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        CustomerRequest actual = underTest.findById(id);
        verify(repository, times(1)).findById(id);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception")
    void findById_IllegalArgumentException() {
        Integer id = -1;
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador informado [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found Exception")
    void findById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o cliente com o identificador [" +
                        id + "] informado.");
    }

    @Test
    @DisplayName("When Find By Cnpj And Returns Successful")
    void findByCnpj_Successful() {
        String cnpj = "86678081000107";
        when(repository.findByCnpj(cnpj)).thenReturn(Optional.of(returnModel));

        CustomerRequest actual = underTest.findByCnpj(cnpj);
        verify(repository, times(1)).findByCnpj(cnpj);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Cnpj Null")
    void findByCnpj_IllegalArgumentExceptionCnpjNull() {
        String cnpj = null;
        assertThatThrownBy(() -> underTest.findByCnpj(cnpj))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O CNPJ [" + cnpj + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Cnpj Blank")
    void findByCnpj_IllegalArgumentExceptionCnpjBlank() {
        String cnpj = "";
        assertThatThrownBy(() -> underTest.findByCnpj(cnpj))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O CNPJ [" + cnpj + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found Exception")
    void findByCnpj_NotFoundException() {
        String cnpj = "86678081000107";
        when(repository.findByCnpj(cnpj)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findByCnpj(cnpj))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o cliente com o CNPJ [" + cnpj +
                        "] informado.");
    }

    @Test
    @DisplayName("When Find All And Returns Successful")
    void findAll_Successful() {
        when(repository.findAll()).thenReturn(List.of(returnModel));

        List<CustomerRequest> expected = List.of(returnRequest);
        List<CustomerRequest> actual = underTest.findAll();
        verify(repository, times(1)).findAll();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find All And Returns Not Found Exception")
    void findAll_NotFoundException() {
        when(repository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findAll())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados clientes cadastrados.");
    }

    @Test
    @DisplayName("When Find By Name Contains And Returns Successful")
    void findByNameContains_Successful() {
        String name = "Kamilly e Marlene";
        String nameUndescore = "Kamilly_e_Marlene";
        when(repository.findByFullnameContains(name)).thenReturn(List.of(returnModel));

        List<CustomerRequest> expected = List.of(returnRequest);
        List<CustomerRequest> actual = underTest.find(Map.of("name", nameUndescore));
        verify(repository, times(1)).findByFullnameContains(name);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Name Contains And Returns Illegal Argument Exception Blank")
    void findByNameContains_IllegalArgumentExceptionBlank() {
        String nameUndescore = "";
        assertThatThrownBy(() -> underTest.find(Map.of("name", nameUndescore)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O nome [" + nameUndescore + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Name Contains And Returns Not Found Exception")
    void findByNameContains_NotFoundException() {
        String name = "Kamilly e Marlene";
        String nameUndescore = "Kamilly_e_Marlene";
        when(repository.findByFullnameContains(name)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.find(Map.of("name", nameUndescore)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes que contenham o nome [" +
                        nameUndescore + "].");
    }

    @Test
    @DisplayName("When Find By Foundation And Returns Successful")
    void findByFoundation_Successful() {
        String foundationStr = "1989-12-03";
        LocalDate foundation = LocalDate.of(1989, 12, 3);
        when(repository.findByFoundation(foundation)).thenReturn(List.of(returnModel));

        List<CustomerRequest> expected = List.of(returnRequest);
        List<CustomerRequest> actual = underTest.find(Map.of("foundation", foundationStr));
        verify(repository, times(1)).findByFoundation(foundation);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Foundation And Returns Illegal Argument Exception Null")
    void findByFoundation_IllegalArgumentExceptionNull() {
        String foundationStr = "";
        assertThatThrownBy(() -> underTest.find(Map.of("foundation", foundationStr)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data [" + foundationStr + "] de foundação não é válida.");
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Successful")
    void findByFoundationBetween_Successful() {
        String startFoundationStr = "1989-12-03";
        String endFoundationStr = "1990-01-01";
        LocalDate startFoundation = LocalDate.of(1989, 12, 03);
        LocalDate endFoundation = LocalDate.of(1990, 01, 01);
        when(repository.findByFoundationBetween(startFoundation, endFoundation)).thenReturn(List.of(returnModel));

        List<CustomerRequest> expected = List.of(returnRequest);
        List<CustomerRequest> actual = underTest.find(
                Map.of("foundation-start", startFoundationStr, "foundation-end", endFoundationStr)
        );
        verify(repository, times(1)).findByFoundationBetween(startFoundation, endFoundation);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Illegal Argument Exception Start Blank")
    void findByFoundationBetween_IllegalArgumentExceptionStartBlank() {
        String foundationStartStr = "";
        String foundationEndStr = "1990-01-01";
        assertThatThrownBy(() -> underTest.find(Map.of(
                "foundation-start", foundationStartStr, "foundation-end", foundationEndStr)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A consulta entre datas de fundação não é válida para a data [" +
                        foundationStartStr + "] incial.");
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Illegal Argument Exception End Blank")
    void findByFoundationBetween_IllegalArgumentExceptionEndBlank() {
        String foundationStartStr = "1989-12-03";
        String foundationEndStr = "";
        assertThatThrownBy(() -> underTest.find(Map.of(
                "foundation-start", foundationStartStr, "foundation-end", foundationEndStr)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A consulta entre datas de fundação não é válida para a data [" +
                        foundationEndStr + "] final.");
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Not Found Exception")
    void findByFoundationBetween_NotFoundExceptionEndBlank() {
        String startFoundationStr = "1989-12-03";
        String endFoundationStr = "1990-01-01";
        LocalDate startFoundation = LocalDate.of(1989, 12, 03);
        LocalDate endFoundation = LocalDate.of(1990, 01, 01);

        when(repository.findByFoundationBetween(startFoundation, endFoundation)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.find(
                Map.of("foundation-start", startFoundationStr, "foundation-end", endFoundationStr)
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes entre as datas [" +
                        startFoundationStr + "] e [" + endFoundationStr + "] de fundação.");
    }

    @Test
    @DisplayName("When Find By Registered And Returns Successful")
    void findByRegistered_Successful() {
        String registeredStr = "2021-09-09_09-45-01";
        LocalDateTime registered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        when(repository.findByRegistered(registered)).thenReturn(List.of(returnModel));

        List<CustomerRequest> expected = List.of(returnRequest);
        List<CustomerRequest> actual = underTest.find(Map.of("registered", registeredStr));
        verify(repository, times(1)).findByRegistered(registered);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Registered And Returns Illegal Argument Exception Blank")
    void findByRegistered_IllegalArgumentExceptionBlank() {
        String registeredStr = "";
        assertThatThrownBy(() -> underTest.find(Map.of("registered", registeredStr)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data [" + registeredStr + "] de registro informada não é válida.");
    }

    @Test
    @DisplayName("When Find By Registered And Returns Not Found Exception")
    void findByRegistered_NotFoundException() {
        String registeredStr = "2021-09-09_09-45-01";
        LocalDateTime registered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        when(repository.findByRegistered(registered)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.find(Map.of("registered", registeredStr)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes registrados na data [" +
                        registeredStr + "].");
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Successful")
    void findByRegisteredBetween_Successful() {
        String startRegisteredStr = "2021-09-09_09-45-01";
        String endRegisteredStr = "2021-12-01_09-30-00";
        LocalDateTime startRegistered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        LocalDateTime endRegistered = LocalDateTime.of(2021, 12, 1, 9, 30, 00);
        when(repository.findByRegisteredBetween(startRegistered, endRegistered)).thenReturn(List.of(returnModel));

        List<CustomerRequest> expected = List.of(returnRequest);
        List<CustomerRequest> actual = underTest.find(
                Map.of("registered-start", startRegisteredStr, "registered-end", endRegisteredStr)
        );
        verify(repository, times(1)).findByRegisteredBetween(startRegistered, endRegistered);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Illegal Argument Exception Start Blank")
    void findByRegisteredBetween_IllegalArgumentExceptionStartBlank() {
        String startRegisteredStr = "";
        String endRegisteredStr = "2021-12-01_09-30-00";

        assertThatThrownBy(() -> underTest.find(
                Map.of("registered-start", startRegisteredStr, "registered-end", endRegisteredStr)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A consulta entre datas de registro não é válida para a data [" +
                        startRegisteredStr + "] incial.");
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Illegal Argument Exception Start Not Valid")
    void findByRegisteredBetween_IllegalArgumentExceptionStartNotValid() {
        String startRegisteredStr = "2021.09.09 09:45:01";
        String endRegisteredStr = "2021-12-01_09-30-00";

        assertThatThrownBy(() -> underTest.find(
                Map.of("registered-start", startRegisteredStr, "registered-end", endRegisteredStr)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A consulta entre datas de registro não é válida para a data [" +
                        startRegisteredStr + "] incial.");
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Illegal Argument Exception End Blank")
    void findByRegisteredBetween_IllegalArgumentExceptionEndBlank() {
        String startRegisteredStr = "2021-09-09_09-45-01";
        String endRegisteredStr = "";

        assertThatThrownBy(() -> underTest.find(
                Map.of("registered-start", startRegisteredStr, "registered-end", endRegisteredStr)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A consulta entre datas de registro não é válida para a data [" +
                        endRegisteredStr + "] final.");
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Illegal Argument Exception End Not Valid")
    void findByRegisteredBetween_IllegalArgumentExceptionEndNotValid() {
        String startRegisteredStr = "2021-09-09_09-45-01";
        String endRegisteredStr = "2021.12.01 09:30:00";

        assertThatThrownBy(() -> underTest.find(
                Map.of("registered-start", startRegisteredStr, "registered-end", endRegisteredStr)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A consulta entre datas de registro não é válida para a data [" +
                        endRegisteredStr + "] final.");
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Not Found Exception")
    void findByRegisteredBetween_NotFoundException() {
        String startRegisteredStr = "2021-09-09_09-45-01";
        String endRegisteredStr = "2021-12-01_09-30-00";
        LocalDateTime startRegistered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        LocalDateTime endRegistered = LocalDateTime.of(2021, 12, 1, 9, 30, 00);
        when(repository.findByRegisteredBetween(startRegistered, endRegistered)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.find(
                Map.of("registered-start", startRegisteredStr, "registered-end", endRegisteredStr)
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar clientes registrados entre as datas [" +
                        startRegisteredStr + "] e [" + endRegisteredStr + "].");
    }

    @Test
    @DisplayName("When Update By Id And Returns Successful")
    void updateById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(updatetingModel));
        when(repository.save(returnModel)).thenReturn(returnModel);

        CustomerRequest actual = underTest.updateById(id, request);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(returnModel);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Update By Id And Returns Illegal Argumant Exception Id Null")
    void updateById_IllegalArgumantExceptionIdNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Illegal Argumant Exception Id Less Or Equal To Zero")
    void updateById_IllegalArgumantExceptionIdLessOrEqualToZero() {
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
                .hasMessageContaining("Não foi possivel encontrar o cliente com o identificador [" +
                        id + "] informado.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Successful")
    void deleteById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));
        when(agendaRepository.findByCustomerId(id)).thenReturn(List.of());

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
                .hasMessageContaining("O identificador informado [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Id Less Or Equal To Zero")
    void deleteById_IllegalArgumentExceptionIdLessOrEqualToZero() {
        Integer id = -1;
        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O identificador informado [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Agenda Not Empty")
    void deleteById_IllegalArgumentExceptionAgendaNotEmpty() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));
        when(agendaRepository.findByCustomerId(id)).thenReturn(List.of(agendaReturnModel));

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Existem eventos agendados com o cliente de id [" + id + "]. " +
                        "Para Prosseguir com a operação de remoção do clinte, os eventos marcados com o cliente " +
                        "deverão ser removidos.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Not Found Exception")
    void deleteById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o cliente com o identificador [" +
                        id + "] informado.");
    }

}