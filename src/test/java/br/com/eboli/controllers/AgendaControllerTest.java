package br.com.eboli.controllers;

import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.repositories.AgendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.eboli.utils.StringUtil.replaceToSpace;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AgendaRepository repository;

    private Agenda model;
    private Agenda returnModel;
    private Agenda updatingModel;

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

    }

    @Test
    @DisplayName("When Save And Returns Ok")
    void save_Ok() throws Exception {
        when(repository.save(model)).thenReturn(returnModel);

        mvc.perform(post("/v1/api/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"title\": \"Reunião com Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"description\": \"Elaboração Sistema Controle Financeiro\", " +
                                        "\"markedTo\": \"2023-01-22_15-30-00\", " +
                                        "\"concluded\": false, " +
                                        "\"customerId\": 2 " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$.markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$.concluded").value(false))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/agenda"));
    }

    @Test
    @DisplayName("When Save And Returns Bad Request Customer Id")
    void save_BadRequestCustomerId() throws Exception {
        mvc.perform(post("/v1/api/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"title\": \"Reunião com Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"description\": \"Elaboração Sistema Controle Financeiro\", " +
                                        "\"markedTo\": \"2023-01-22_15-30-00\", " +
                                        "\"concluded\": false, " +
                                        "\"customerId\": 0 " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("As informações do agendamento do evento " +
                        "não são válidos."));
    }

    @Test
    @DisplayName("When Save And Returns Bad Request Fields Are Blank")
    void save_BadRequestFieldsAreBlank() throws Exception {
        mvc.perform(post("/v1/api/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}")
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("As informações do agendamento do evento " +
                        "não são válidos."));
    }

    @Test
    @DisplayName("When Save And Returns Bad Request Marked To After Now")
    void save_BadRequestMarkedToAfterNow() throws Exception {
        LocalDateTime markedTo = LocalDateTime.of(2022, 12, 25, 15, 30, 00);
        String markedToStr = "2022-12-25_15-30-00";

        when(repository.save(model)).thenReturn(returnModel);
        mvc.perform(post("/v1/api/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"title\": \"Reunião com Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"description\": \"Elaboração Sistema Controle Financeiro\", " +
                                        "\"markedTo\": \"" + markedToStr + "\", " +
                                        "\"concluded\": false, " +
                                        "\"customerId\": 2 " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("When Find By Id And Returns Ok")
    void findById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        mvc.perform(get("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$.markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$.concluded").value(false))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/agenda"));
    }

    @Test
    @DisplayName("When Find By Id And Returns Bad Request")
    void findById_BadRequest() throws Exception {
        Integer id = -1;
        mvc.perform(get("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O id [" + id + "] não é válido."));
    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found")
    void findById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(get("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o evento " +
                        "agendado com o id [" + id + "] informado."));
    }

    @Test
    @DisplayName("When Find All And Returns Ok")
    void findAll_Ok() throws Exception {
        when(repository.findAll()).thenReturn(List.of(returnModel));
        mvc.perform(get("/v1/api/agenda/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].concluded").value(false))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].customerId").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"));
    }

    @Test
    @DisplayName("When Find All And Returns Not Found")
    void findAll_NotFound() throws Exception {
        when(repository.findAll()).thenReturn(List.of());
        mvc.perform(get("/v1/api/agenda/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados evendos agendados."));
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Ok")
    void findByTitleContains_Ok() throws Exception {
        String title = "Kamilly_e_Marlene";
        when(repository.findByTitleContains(replaceToSpace(title))).thenReturn(List.of(returnModel));
        mvc.perform(get("/v1/api/agenda/find?title=" + title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].concluded").value(false))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].customerId").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"));
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Bad Request")
    void findByTitleContains_BadRequest() throws Exception {
        String title = "Kamilly e Marlene";
        mvc.perform(get("/v1/api/agenda/find?title=" + title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O titulo [" + title + "] não é válido."));
    }

    @Test
    @DisplayName("When Find By Title Contains And Returns Not Found")
    void findByTitleContains_NotFound() throws Exception {
        String title = "Kamilly_e_Marlene";
        when(repository.findByTitleContains(title)).thenReturn(List.of());
        mvc.perform(get("/v1/api/agenda/find?title=" + title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados eventos agendados " +
                        "que contenham o titulo [" + title + "] informada."));
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Ok")
    void findByMarkedTo_Ok() throws Exception {
        String markedToStr = "2023-01-22_15-30-00";
        LocalDateTime markedTo = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        when(repository.findByMarkedTo(markedTo)).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/agenda/find?marked-to=" + markedToStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].concluded").value(false))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].customerId").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"));
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Bad Request")
    void findByMarkedTo_BadRequest() throws Exception {
        String markedToStr = "2023.01.22 15:30:00";
        mvc.perform(get("/v1/api/agenda/find?marked-to=" + markedToStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("A data [" + markedToStr + "] marcada para " +
                        "encontrar eventos agendados não é válida."));
    }

    @Test
    @DisplayName("When Find By Marked To And Returns Not Found")
    void findByMarkedTo_NotFound() throws Exception {
        String markedToStr = "2023-01-22_15-30-00";
        LocalDateTime markedTo = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        when(repository.findByMarkedTo(markedTo)).thenReturn(List.of());

        mvc.perform(get("/v1/api/agenda/find?marked-to=" + markedToStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados eventos agendados " +
                        "para a data [" + markedToStr + "] informada."));
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Ok")
    void findByMarkedToBetween_Ok() throws Exception {
        String startDateTimeStr = "2023-01-22_15-30-00";
        String endDateTimeStr = "2023-01-25_15-30-00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 25, 15, 30, 00);
        when(repository.findByMarkedToBetween(startDateTime, endDateTime)).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/agenda/find?marked-start=" + startDateTimeStr + "&marked-end=" + endDateTimeStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].concluded").value(false))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].customerId").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"));
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Bad Request")
    void findByMarkedToBetween_BadRequest() throws Exception {
        String startDateTimeStr = "2023.01.22 15:30:00";
        String endDateTimeStr = "2023.01.25 15:30:00";
        mvc.perform(get("/v1/api/agenda/find?marked-start=" + startDateTimeStr + "&marked-end=" + endDateTimeStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("As datas [" + startDateTimeStr +
                        "] e [" + endDateTimeStr + "] informadas para econtrar os eventos agendados não são válidas."));
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Not Found")
    void findByMarkedToBetween_NotFound() throws Exception {
        String startDateTimeStr = "2023-01-22_15-30-00";
        String endDateTimeStr = "2023-01-25_15-30-00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 22, 15, 30, 00);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 25, 15, 30, 00);
        when(repository.findByMarkedToBetween(startDateTime, endDateTime)).thenReturn(List.of());

        mvc.perform(get("/v1/api/agenda/find?marked-start=" + startDateTimeStr + "&marked-end=" + endDateTimeStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados eventos agendados " +
                        "entre as datas [" + startDateTimeStr + "] e [" + endDateTimeStr + "]."));
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Ok")
    void findByConcluded_Ok() throws Exception {
        Boolean concluded = false;
        String concludedStr = "false";
        when(repository.findByConcluded(concluded)).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/agenda/find?concluded=" + concludedStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].concluded").value(false))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0].customerId").value(2))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._embedded.agendaResponseList[0]._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"));
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Bad Request")
    void findByConcluded_BadRequest() throws Exception {
        String concludedStr = "falso";
        mvc.perform(get("/v1/api/agenda/find?concluded=" + concludedStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Não é válido a informação para realizar " +
                        "a operação."));
    }

    @Test
    @DisplayName("When Find By Marked To Between And Returns Not Found")
    void findByConcluded_NotFound() throws Exception {
        Boolean concluded = true;
        String concludedStr = "true";
        String markedConcludedStr = concluded ? "concluído" : "não concluído";
        when(repository.findByConcluded(concluded)).thenReturn(List.of());

        mvc.perform(get("/v1/api/agenda/find?concluded=" + concludedStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados eventos agendados e " +
                        markedConcludedStr + '.'));
    }

    @Test
    @DisplayName("When Update By Id And Returns Ok")
    void updateById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(updatingModel));
        when(repository.save(model)).thenReturn(returnModel);

        mvc.perform(put("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"title\": \"Reunião com Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"description\": \"Elaboração Sistema Controle Financeiro\", " +
                                        "\"markedTo\": \"2023-01-22_15-30-00\", " +
                                        "\"concluded\": false, " +
                                        "\"customerId\": 2 " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Reunião com Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.description").value("Elaboração Sistema Controle Financeiro"))
                .andExpect(jsonPath("$.markedTo").value("2023-01-22_15-30-00"))
                .andExpect(jsonPath("$.concluded").value(false))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/agenda/2"))
                .andExpect(jsonPath("$._links.find-by-title.href").value("http://localhost/v1/api/agenda/find?title=Reuni%C3%A3o%20com%20Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._links.find-by-marked-to.href").value("http://localhost/v1/api/agenda/find?marked-to=2023-01-22_15-30-00"))
                .andExpect(jsonPath("$._links.find-by-concluded.href").value("http://localhost/v1/api/agenda/find?concluded=false"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/agenda"));
    }

    @Test
    @DisplayName("When Update By Id And Returns Bad Request")
    void updateById_BadRequest() throws Exception {
        Integer id = -1;
        mvc.perform(put("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"title\": \"Reunião com Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"description\": \"Elaboração Sistema Controle Financeiro\", " +
                                        "\"markedTo\": \"2023-01-22_15-30-00\", " +
                                        "\"concluded\": false, " +
                                        "\"customerId\": 2 " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O id [" + id + "] informado não é válido."));
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found")
    void updateById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());
        mvc.perform(put("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"title\": \"Reunião com Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"description\": \"Elaboração Sistema Controle Financeiro\", " +
                                        "\"markedTo\": \"2023-01-22_15-30-00\", " +
                                        "\"concluded\": false, " +
                                        "\"customerId\": 2 " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o evento " +
                        "agendado com o id [" + id + "] informado."));
    }

    @Test
    @DisplayName("Whe Delete By Id And Returns Ok")
    void deleteById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));
        mvc.perform(delete("/v1/api/agenda/" + id)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When Update By Id And Returns Bad Request")
    void deleteById_BadRequest() throws Exception {
        Integer id = -1;
        mvc.perform(delete("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O id [" + id + "] informado não é válido."));
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found")
    void deleteById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());
        mvc.perform(delete("/v1/api/agenda/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o evento " +
                        "agendado com o id [" + id + "] informado."));
    }

}