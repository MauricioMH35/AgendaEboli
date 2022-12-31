package br.com.eboli.services.impls;

import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.repositories.AgendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static br.com.eboli.utils.DateUtil.parseDateTime;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class AgendaServiceImplTest {

    @InjectMocks
    private AgendaServiceImpl underTest;

    @Mock
    private AgendaRepository repository;

    private Clock clock = mock(Clock.class);

    private Agenda model;
    private Agenda returnModel;
    private Agenda updatingModel;

    private AgendaRequest request;
    private AgendaRequest returnRequest;

    @BeforeEach
    void setUp() {

        model = Agenda.builder()
                .title("Reunião com Kamilly e Marlene Casa Noturna ME")
                .description("Elaboração Sistema Controle Financeiro")
                .markedTo(LocalDateTime.of(2023, 1, 22, 15, 30, 00))
                .concluded(false)
                .customer(Customer.builder().id(2).build())
                .build();

        returnModel = Agenda.builder()
                .id(2)
                .title("Reunião com Kamilly e Marlene Casa Noturna ME")
                .description("Elaboração Sistema Controle Financeiro")
                .markedTo(LocalDateTime.of(2023, 1, 22, 15, 30, 00))
                .concluded(false)
                .customer(Customer.builder().id(2).build())
                .build();

        updatingModel = Agenda.builder()
                .id(2)
                .title("Reuniao Giovanni e Leandro Adega Ltda")
                .description("Controle de Estoque")
                .markedTo(LocalDateTime.of(2023, 1, 25, 15, 30, 00))
                .concluded(false)
                .customer(Customer.builder().id(2).build())
                .build();

        request = AgendaRequest.builder()
                .title("Reunião com Kamilly e Marlene Casa Noturna ME")
                .description("Elaboração Sistema Controle Financeiro")
                .markedTo("2023-01-22_15-30-00")
                .concluded(false)
                .customerId(2)
                .build();

        returnRequest = AgendaRequest.builder()
                .id(2)
                .title("Reunião com Kamilly e Marlene Casa Noturna ME")
                .description("Elaboração Sistema Controle Financeiro")
                .markedTo("2023-01-22_15-30-00")
                .concluded(false)
                .customerId(2)
                .build();

    }

    @Test
    @DisplayName("When Save And Returns Successful")
    void save_Successful() {
        LocalDateTime dateTimeNow = LocalDateTime.of(
                2022, 12, 10,
                15, 30, 00
        );
        Clock fixedClock = Clock.fixed(
                dateTimeNow.toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
        when(repository.save(model)).thenReturn(returnModel);

        AgendaRequest actual = underTest.save(request);
        verify(repository, times(1)).save(model);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Save And Returns Illegal Argument Exception Fields Are Blank")
    void save_IllegalArgumentExceptionFieldsAreBlank() {
        AgendaRequest agendaFieldsBlank = AgendaRequest.builder().build();
        assertThatThrownBy(() -> underTest.save(agendaFieldsBlank))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As informações do agendamento do evento não são válidos.");
    }

    @Test
    @DisplayName("When Save And Returns Illegal Argument Exception Marked To Is Before")
    void save_IllegalArgumentExceptionMarkedToIsBefore() {
        LocalDateTime markedTo = parseDateTime(request.getMarkedTo());
        LocalDateTime dateTimeNow = LocalDateTime.of(
                2100, 1, 1,
                15, 30, 00
        );
        Clock fixedClock = Clock.fixed(
                dateTimeNow.toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        assertThatThrownBy(() -> underTest.save(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data marcada [" + markedTo + "] para o evento não pode ser " +
                        "anterior a data atual [" + dateTimeNow + "]");
    }

    @Test
    @DisplayName("When Find By Id And Returns Successful")
    void findById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        AgendaRequest actual = underTest.findById(id);
        verify(repository, times(1)).findById(id);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Id Null")
    void findById_IllegalArgumentExceptionIdNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Id Less Or Equal Zero")
    void findById_IllegalArgumentExceptionIdLessOrEqualZero() {
        Integer id = 0;
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found Exception")
    void findById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o evento agendado com o id ["+id+
                        "] informado.");
    }

    @Test
    @DisplayName("When Find All And Returns Successfult")
    void findAll_Successfult() {
        when(repository.findAll()).thenReturn(List.of(returnModel));

        List<AgendaRequest> expected = List.of(returnRequest);
        List<AgendaRequest> actual = underTest.findAll();
        verify(repository, times(1)).findAll();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find All And Returns Not Found Exception")
    void findAll_NotFoundException() {
        when(repository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findAll())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados evendos agendados.");
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Successful")
    void findByTitleContains_Successful() {
        String titleWhiteSpace = "Kamilly e Marlene";
        String title = "Kamilly_e_Marlene";
        when(repository.findByTitleContains(titleWhiteSpace)).thenReturn(List.of(returnModel));

        List<AgendaRequest> expected = List.of(returnRequest);
        List<AgendaRequest> actual = underTest.findByTitleContains(title);
        verify(repository, times(1)).findByTitleContains(titleWhiteSpace);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Illegal Argument Exception Title Null")
    void findByTitleContains_IllegalArgumentExceptionTitleNull() {
        String title = null;

        assertThatThrownBy(() -> underTest.findByTitleContains(title))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O titulo [" + title + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Illegal Argument Exception Title Blank")
    void findByTitleContains_IllegalArgumentExceptionTitleBlank() {
        String title = "";

        assertThatThrownBy(() -> underTest.findByTitleContains(title))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O titulo [" + title + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Illegal Argument Exception Title Has White Space")
    void findByTitleContains_IllegalArgumentExceptionTitleHasWhiteSpace() {
        String title = "Kamilly e Marlene";

        assertThatThrownBy(() -> underTest.findByTitleContains(title))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O titulo [" + title + "] não é válido.");
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Not Found Exception")
    void findByTitleContains_NotFoundException() {
        String titleWhiteSpace = "Kamilly e Marlene";
        String title = "Kamilly_e_Marlene";
        when(repository.findByTitleContains(titleWhiteSpace)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByTitleContains(title))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados eventos agendados que contenham o titulo [" +
                        title + "] informada.");
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Successful")
    void findByMarkedTo_Successful() {
        String markedToStr = "2023-01-22_15-30-00";
        LocalDateTime markedTo = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        when(repository.findByMarkedTo(markedTo)).thenReturn(List.of(returnModel));

        List<AgendaRequest> expected = List.of(returnRequest);
        List<AgendaRequest> actual = underTest.findByMarkedTo(markedToStr);
        verify(repository, times(1)).findByMarkedTo(markedTo);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Illegal Argument Exception Null")
    void findByMarkedTo_IllegalArgumentExceptionNull() {
        String markedToStr = null;

        assertThatThrownBy(() -> underTest.findByMarkedTo(markedToStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data [" + markedToStr + "] marcada para encontrar eventos agendados " +
                        "não é válida.");
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Illegal Argument Exception Blank")
    void findByMarkedTo_IllegalArgumentExceptionBlank() {
        String markedToStr = "";

        assertThatThrownBy(() -> underTest.findByMarkedTo(markedToStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data [" + markedToStr + "] marcada para encontrar eventos agendados " +
                        "não é válida.");
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Illegal Argument Exception Not Valid")
    void findByMarkedTo_IllegalArgumentExceptionNotValid() {
        String markedToStr = "2023.01.22 15:30:00";

        assertThatThrownBy(() -> underTest.findByMarkedTo(markedToStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data [" + markedToStr + "] marcada para encontrar eventos agendados " +
                        "não é válida.");
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Not Found Exception")
    void findByMarkedTo_NotFoundException() {
        String markedToStr = "2023-01-22_15-30-00";
        LocalDateTime markedTo = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        when(repository.findByMarkedTo(markedTo)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByMarkedTo(markedToStr))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados eventos agendados para a data [" + markedToStr +
                        "] informada.");
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Successful")
    void findByMarkedToBetween_Successful() {
        String startDateTimeStr = "2023-01-22_15-30-00";
        String endDateTimeStr = "2023-01-30_15-30-00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 30, 15, 30, 00);
        when(repository.findByMarkedToBetween(startDateTime, endDateTime)).thenReturn(List.of(returnModel));

        List<AgendaRequest> expected = List.of(returnRequest);
        List<AgendaRequest> actual = underTest.findByMarkedToBetween(startDateTimeStr, endDateTimeStr);
        verify(repository, times(1)).findByMarkedToBetween(startDateTime, endDateTime);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Illegal Argument Exception Null")
    void findByMarkedToBetween_IllegalArgumentExceptionNull() {
        String startDateTimeStr = null;
        String endDateTimeStr = null;

        assertThatThrownBy(() -> underTest.findByMarkedToBetween(startDateTimeStr, endDateTimeStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As datas [" + startDateTimeStr + "] e [" + endDateTimeStr +
                        "] informadas para econtrar os eventos agendados não são válidas.");
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Illegal Argument Exception Blank")
    void findByMarkedToBetween_IllegalArgumentExceptionBlank() {
        String startDateTimeStr = "";
        String endDateTimeStr = "";

        assertThatThrownBy(() -> underTest.findByMarkedToBetween(startDateTimeStr, endDateTimeStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As datas [" + startDateTimeStr + "] e [" + endDateTimeStr +
                        "] informadas para econtrar os eventos agendados não são válidas.");
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Illegal Argument Exception Not Valid")
    void findByMarkedToBetween_IllegalArgumentExceptionNotValid() {
        String startDateTimeStr = "2023.01.22 15:30:00";
        String endDateTimeStr = "2023.01.30 15:30:00";

        assertThatThrownBy(() -> underTest.findByMarkedToBetween(startDateTimeStr, endDateTimeStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As datas [" + startDateTimeStr + "] e [" + endDateTimeStr +
                        "] informadas para econtrar os eventos agendados não são válidas.");
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Not Found Exception")
    void findByMarkedToBetween_NotFoundException() {
        String startDateTimeStr = "2023-01-22_15-30-00";
        String endDateTimeStr = "2023-01-30_15-30-00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 30, 15, 30, 00);
        when(repository.findByMarkedToBetween(startDateTime, endDateTime)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByMarkedToBetween(startDateTimeStr, endDateTimeStr))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados eventos agendados entre as datas [" + startDateTimeStr +
                        "] e [" + endDateTimeStr + "].");
    }

    @Test
    @DisplayName("When Find By Concluded And Returns Successful")
    void findByConcluded_Successful() {
        String concludedStr = "false";
        Boolean concluded = false;
        when(repository.findByConcluded(concluded)).thenReturn(List.of(returnModel));

        List<AgendaRequest> expected = List.of(returnRequest);
        List<AgendaRequest> actual = underTest.findByConcluded(concludedStr);
        verify(repository, times(1)).findByConcluded(concluded);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Concluded And Returns Illegal Argument Exception Null")
    void findByConcluded_IllegalArgumentExceptionNull() {
        String concludedStr = null;

        assertThatThrownBy(() -> underTest.findByConcluded(concludedStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Não é válido a informação para realizar a operação.");
    }

    @Test
    @DisplayName("When Find By Concluded And Returns Illegal Argument Exception Blank")
    void findByConcluded_IllegalArgumentExceptionBlank() {
        String concludedStr = "";

        assertThatThrownBy(() -> underTest.findByConcluded(concludedStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Não é válido a informação para realizar a operação.");
    }

    @Test
    @DisplayName("When Find By Concluded And Returns Illegal Argument Exception Not Valid")
    void findByConcluded_IllegalArgumentExceptionNotValid() {
        String concludedStr = "verdadeiro";

        assertThatThrownBy(() -> underTest.findByConcluded(concludedStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Não é válido a informação para realizar a operação.");
    }

    @Test
    @DisplayName("When Find By Concluded And Returns Not Found Exception")
    void findByConcluded_NotFoundException() {
        String concludedStr = "false";
        Boolean concluded = false;
        String markedConcludedStr = concluded ? "concluído" : "não concluído";

        when(repository.findByConcluded(concluded)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByConcluded(concludedStr))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados eventos agendados e " + markedConcludedStr + '.');
    }

    @Test
    @DisplayName("When Marked Concluded And Returns Successful")
    void markedConcluded_Successful() {
        LocalDateTime dateTimeNow = LocalDateTime.of(
                2023, 1, 30,
                15, 30, 00
        );
        Clock fixedClock = Clock.fixed(
                dateTimeNow.toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        underTest.markedConcluded(id);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).markConcluded(id);
    }

    @Test
    @DisplayName("When Marked Concluded And Returns Illegal Argument Exception Null")
    void markedConcluded_IllegalArgumentExceptionNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.markedConcluded(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Marked Concluded And Returns Illegal Argument Exception Less Or Equls To Zero")
    void markedConcluded_IllegalArgumentExceptionLessOrEqulsToZero() {
        Integer id = -1;

        assertThatThrownBy(() -> underTest.markedConcluded(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] não é válido.");
    }

    @Test
    @DisplayName("When Marked Concluded And Returns Not Found Exception")
    void markedConcluded_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.markedConcluded(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o evento agendado com o id [" +
                        id + "] informado.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Successful")
    void updateById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(updatingModel));
        when(repository.save(returnModel)).thenReturn(returnModel);

        AgendaRequest actual = underTest.updateById(id, request);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(returnModel);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Marked Concluded And Returns Illegal Argument Exception Null")
    void updateById_IllegalArgumentExceptionNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Marked Concluded And Returns Illegal Argument Exception Less Or Equls To Zero")
    void updateById_IllegalArgumentExceptionLessOrEqulsToZero() {
        Integer id = -1;

        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Marked Concluded And Returns Not Found Exception")
    void updateById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o evento agendado com o id [" +
                        id + "] informado.");
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
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Null")
    void deleteById_IllegalArgumentExceptionNull() {
        Integer id = null;

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Less Or Equls To Zero")
    void deleteById_IllegalArgumentExceptionLessOrEqulsToZero() {
        Integer id = -1;

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Not Found Exception")
    void deleteById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o evento agendado com o id [" +
                        id + "] informado.");
    }

}