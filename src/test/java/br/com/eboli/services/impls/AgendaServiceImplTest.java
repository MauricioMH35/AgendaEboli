package br.com.eboli.services.impls;

import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.models.requests.AgendaRequest;
import br.com.eboli.repositories.AgendaRepository;
import br.com.eboli.services.impls.AgendaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class AgendaServiceImplTest {

    @InjectMocks
    private AgendaServiceImpl underTest;

    @Mock
    private AgendaRepository repository;

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
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findByTitleContains() {
    }

    @Test
    void findByMarkedTo() {
    }

    @Test
    void findByMarkedToBetween() {
    }

    @Test
    void findByConcluded() {
    }

    @Test
    void markedConcluded() {
    }

    @Test
    void updateById() {
    }

    @Test
    void deleteById() {
    }

}