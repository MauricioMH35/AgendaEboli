package br.com.eboli.controllers;

import br.com.eboli.models.Agenda;
import br.com.eboli.models.Customer;
import br.com.eboli.repositories.AgendaRepository;
import br.com.eboli.repositories.CustomerRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerRepository repository;

    @MockBean
    private AgendaRepository agendaRepository;

    private Customer model;
    private Customer returnModel;
    private Customer updatetingModel;

    private Agenda agendaModel;

    @BeforeEach
    void setUp() {

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

        agendaModel = Agenda.builder()
                .title("Reunião com Kamilly e Marlene Casa Noturna ME")
                .description("Elaboração Sistema Controle Financeiro")
                .markedTo(LocalDateTime.of(2023, 1, 22, 15, 30, 00))
                .concluded(false)
                .customer(Customer.builder().id(2).build())
                .build();
    }

    @Test
    @DisplayName("When Save And Returns Ok")
    void save_Ok() throws Exception {
        when(repository.save(model)).thenReturn(returnModel);

        mvc.perform(post("/v1/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"fullname\": \"Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"cnpj\": \"86678081000107\", " +
                                        "\"foundation\": \"1989-12-03\", " +
                                        "\"registered\": \"2021-09-09_09-45-01\" " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.cnpj").value("86678081000107"))
                .andExpect(jsonPath("$.foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Save Returns Bad Request")
    void save_BadRequest() throws Exception {
        mvc.perform(post("/v1/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}")
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("As informações do cliente não são válidas."));
    }

    @Test
    @DisplayName("When Save Return Conflict")
    void save_Conflict() throws Exception {
        when(repository.findByCnpj("68861391000172"))
                .thenReturn(Optional.of(
                        Customer.builder()
                                .fullname("Elza e Agatha Telecomunicações ME")
                                .cnpj("68861391000172")
                                .foundation(LocalDate.of(1990, 05, 22))
                                .build()));

        mvc.perform(post("/v1/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "    \"fullname\": \"Elza e Agatha Telecomunicações ME\", " +
                                        "    \"cnpj\": \"68861391000172\", " +
                                        "    \"foundation\": \"1990-05-22\" " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("O cliente com o CNPJ [68861391000172] informado não pode ser cadastrado, ja possui um cadastro."));
    }

    @Test
    @DisplayName("When Find All And Return Ok")
    void findAll_Ok() throws Exception {
        when(repository.findAll()).thenReturn(List.of(returnModel));
        mvc.perform(get("/v1/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"));
    }

    @Test
    @DisplayName("When Find All And Returns Not Found")
    void findAll_NotFound() throws Exception {
        when(repository.findAll()).thenReturn(List.of());
        mvc.perform(get("/v1/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados clientes cadastrados."));
    }

    @Test
    @DisplayName("When Find By Id And Returns Ok")
    void findById_Ok() throws Exception {
        when(repository.findById(2)).thenReturn(Optional.of(returnModel));

        mvc.perform(get("/v1/api/customers/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.cnpj").value("86678081000107"))
                .andExpect(jsonPath("$.foundation").value("1989-12-03"))
                .andExpect(jsonPath("$.registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Find By Id And Returns Bad Request")
    void findById_BadRequest() throws Exception {
        mvc.perform(get("/v1/api/customers/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O identificador informado [0] não é válido."));
    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found")
    void findById_NotFound() throws Exception {
        when(repository.findById(2)).thenReturn(Optional.empty());

        mvc.perform(get("/v1/api/customers/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o cliente com o identificador [2] informado."));
    }

    @Test
    @DisplayName("When Find By Cnpj And Returns Ok")
    void findByCnpj_Ok() throws Exception {
        String cnpj = "86678081000107";
        when(repository.findByCnpj(cnpj)).thenReturn(Optional.of(returnModel));
        mvc.perform(get("/v1/api/customers/cnpj/" + cnpj)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.cnpj").value("86678081000107"))
                .andExpect(jsonPath("$.foundation").value("1989-12-03"))
                .andExpect(jsonPath("$.registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Find By Cnpj And Returns Not Found")
    void findByCnpj_BadRequest() throws Exception {
        String cnpj = "86678081000107";
        when(repository.findByCnpj(cnpj)).thenReturn(Optional.empty());
        mvc.perform(get("/v1/api/customers/cnpj/" + cnpj)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o cliente com o CNPJ [" + cnpj + "] informado."));
    }

    @Test
    @DisplayName("When Find By Name Contains And Returns Ok")
    void findByNameContains_Ok() throws Exception {
        when(repository.findByFullnameContains("Marlene Casa")).thenReturn(List.of(returnModel));
        mvc.perform(get("/v1/api/customers/find?name=Marlene_Casa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"));
    }

    @Test
    @DisplayName("When Find By Name Contains And Returns Not Found")
    void findByNameContains_NotFound() throws Exception {
        String nameStr = "Marlene_Casa";
        when(repository.findByFullnameContains(nameStr)).thenReturn(List.of());
        mvc.perform(get("/v1/api/customers/find?name=Marlene_Casa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar clientes que contenham o nome [" + nameStr + "]."));
    }

    @Test
    @DisplayName("When Find Foundation And Returns Ok")
    void findByFoundation_Ok() throws Exception {
        String foundationStr = "1989-12-03";
        LocalDate foundation = LocalDate.of(1989, 12, 03);
        when(repository.findByFoundation(foundation)).thenReturn(List.of(returnModel));
        mvc.perform(get("/v1/api/customers/find?foundation=" + foundationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"));
    }

    @Test
    @DisplayName("When Find By Foundation And Returns Bad Request")
    void findByFoundation_BadRequest() throws Exception {
        String foundationStr = "1989.12.03";
        mvc.perform(get("/v1/api/customers/find?foundation=" + foundationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("A data [" + foundationStr + "] de foundação não é válida."));
    }

    @Test
    @DisplayName("When Find By Foundation And Returns Not Found")
    void findByFoundation_NotFound() throws Exception {
        String foundationStr = "1989-12-03";
        LocalDate foundation = LocalDate.of(1989, 12, 03);
        when(repository.findByFoundation(foundation)).thenReturn(List.of());
        mvc.perform(get("/v1/api/customers/find?foundation=" + foundationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o clientes com a data [" + foundationStr + "] de fundação."));
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Ok")
    void findByFoundationBetween_Ok() throws Exception {
        String startFoundationStr = "1989-12-03";
        String endFoundationStr = "1990-01-01";
        LocalDate startFoundation = LocalDate.of(1989, 12, 03);
        LocalDate endFoundation = LocalDate.of(1990, 01, 01);
        when(repository.findByFoundationBetween(startFoundation, endFoundation)).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/customers/find?foundation-start=" + startFoundationStr + "&foundation-end=" + endFoundationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"));
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Bad Request Start Foundation")
    void findByFoundationBetween_BadRequestStartFoundation() throws Exception {
        String startFoundationStr = "1989.12.03";
        String endFoundationStr = "1990-01-01";

        mvc.perform(get("/v1/api/customers/find?foundation-start=" + startFoundationStr + "&foundation-end=" + endFoundationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("A consulta entre datas de fundação não é válida para a data [" + startFoundationStr + "] incial."));
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Bad Request End Foundation")
    void findByFoundationBetween_BadRequestEndFoundation() throws Exception {
        String startFoundationStr = "1989-12-03";
        String endFoundationStr = "1990.01.01";

        mvc.perform(get("/v1/api/customers/find?foundation-start=" + startFoundationStr + "&foundation-end=" + endFoundationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("A consulta entre datas de fundação não é válida para a data [" + endFoundationStr + "] final."));
    }

    @Test
    @DisplayName("When Find By Foundation Between And Returns Not Found")
    void findByFoundationBetween_NotFound() throws Exception {
        String startFoundationStr = "1989-12-03";
        String endFoundationStr = "1990-01-01";
        LocalDate startFoundation = LocalDate.of(1989, 12, 03);
        LocalDate endFoundation = LocalDate.of(1990, 01, 01);
        when(repository.findByFoundationBetween(startFoundation, endFoundation)).thenReturn(List.of());

        mvc.perform(get("/v1/api/customers/find?foundation-start=" + startFoundationStr + "&foundation-end=" + endFoundationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar clientes entre " +
                        "as datas [" + startFoundationStr + "] e [" + endFoundationStr + "] de fundação."));
    }

    @Test
    @DisplayName("When Find By Registered And Returns Ok")
    void findByRegistered_Ok() throws Exception {
        String registeredStr = "2021-09-09_09-45-01";
        LocalDateTime registered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);

        when(repository.findByRegistered(registered)).thenReturn(List.of(returnModel));
        mvc.perform(get("/v1/api/customers/find?registered=" + registeredStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"));
    }

    @Test
    @DisplayName("When Find By Registered And Returns Bad Request")
    void findByRegistered_BadRequest() throws Exception {
        String registeredStr = "2021.09.09_09:45:01";
        mvc.perform(get("/v1/api/customers/find?registered=" + registeredStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("A data [" + registeredStr + "] de registro informada não é válida."));
    }

    @Test
    @DisplayName("When Find By Registered And Returns Not Found")
    void findByRegistered_NotFound() throws Exception {
        String registeredStr = "2021-09-09_09-45-01";
        LocalDateTime registered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);

        when(repository.findByRegistered(registered)).thenReturn(List.of());
        mvc.perform(get("/v1/api/customers/find?registered=" + registeredStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar clientes " +
                        "registrados na data [" + registeredStr + "]."));
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Ok")
    void findByRegisteredBetween_Ok() throws Exception {
        String startRegisteredStr = "2021-09-09_09-45-01";
        String endRegisteredStr = "2021-12-01_09-30-00";
        LocalDateTime startRegistered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        LocalDateTime endRegistered = LocalDateTime.of(2021, 12, 1, 9, 30, 00);

        when(repository.findByRegisteredBetween(startRegistered, endRegistered)).thenReturn(List.of(returnModel));
        mvc.perform(get("/v1/api/customers/find?registered-start=" + startRegisteredStr + "&registered-end=" + endRegisteredStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2021-09-09_09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.find-by-registered.href").value("http://localhost/v1/api/customers/find?registered=2021-09-09_09-45-01"));
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Bad Request Start Registered")
    void findRegisteredBetween_BadRequestStartRegistered() throws Exception {
        String startRegisteredStr = "2021.09.09 09:45:01";
        String endRegisteredStr = "2021-12-01_09-30-00";
        LocalDateTime startRegistered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        LocalDateTime endRegistered = LocalDateTime.of(2021, 12, 1, 9, 30, 00);

        mvc.perform(get("/v1/api/customers/find?registered-start=" + startRegisteredStr + "&registered-end=" + endRegisteredStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("A consulta entre datas de registro não é " +
                        "válida para a data [" + startRegisteredStr + "] incial."));
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Bad Request End Registered")
    void findByRegisteredBetween_BadRequestEndRegistered() throws Exception {
        String startRegisteredStr = "2021-09-09_09-45-01";
        String endRegisteredStr = "2021.12.01_09:30:00";
        LocalDateTime startRegistered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        LocalDateTime endRegistered = LocalDateTime.of(2021, 12, 1, 9, 30, 00);

        mvc.perform(get("/v1/api/customers/find?registered-start=" + startRegisteredStr + "&registered-end=" + endRegisteredStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("A consulta entre datas de registro não é " +
                        "válida para a data [" + endRegisteredStr + "] final."));
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Not Found")
    void findByRegisteredBetween_NotFound() throws Exception {
        String startRegisteredStr = "2021-09-09_09-45-01";
        String endRegisteredStr = "2021-12-01_09-30-00";
        LocalDateTime startRegistered = LocalDateTime.of(2021, 9, 9, 9, 45, 01);
        LocalDateTime endRegistered = LocalDateTime.of(2021, 12, 1, 9, 30, 00);

        when(repository.findByRegisteredBetween(startRegistered, endRegistered)).thenReturn(List.of());
        mvc.perform(get("/v1/api/customers/find?registered-start=" + startRegisteredStr + "&registered-end=" + endRegisteredStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar clientes " +
                        "registrados entre as datas [" + startRegisteredStr + "] e [" + endRegisteredStr + "]."));
    }

    @Test
    @DisplayName("When Update By Id And Returns Ok")
    void updateById_Ok() throws Exception {
        when(repository.findById(2)).thenReturn(Optional.of(updatetingModel));
        when(repository.save(model)).thenReturn(returnModel);

        mvc.perform(put("/v1/api/customers/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"fullname\": \"Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"cnpj\": \"86678081000107\", " +
                                        "\"foundation\": \"1989-12-03\", " +
                                        "\"registered\": \"2021-09-09_09-45-01\" " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.cnpj").value("86678081000107"))
                .andExpect(jsonPath("$.foundation").value("1989-12-03"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._links.find-by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly_e_Marlene_Casa_Noturna_ME"))
                .andExpect(jsonPath("$._links.find-by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._links.find-by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1989-12-03"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Update By Id And Returns Bad Request")
    void updateById_BadRequest() throws Exception {
        String id = "-1";
        mvc.perform(put("/v1/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"fullname\": \"Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"cnpj\": \"86678081000107\", " +
                                        "\"foundation\": \"1989-12-03\", " +
                                        "\"registered\": \"2021-09-09_09-45-01\" " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O identificador [" + id +
                        "] informado não é válido."));
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found")
    void updateById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(2)).thenReturn(Optional.empty());
        mvc.perform(put("/v1/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"fullname\": \"Kamilly e Marlene Casa Noturna ME\", " +
                                        "\"cnpj\": \"86678081000107\", " +
                                        "\"foundation\": \"1989-12-03\", " +
                                        "\"registered\": \"2021-09-09_09-45-01\" " +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o cliente com o " +
                        "identificador [" + id + "] informado."));
    }

    @Test
    @DisplayName("When Delete By Id And Returns No Content")
    void deleteById_NoContent() throws Exception {
        Integer id = 2;
        when(repository.findById(2)).thenReturn(Optional.of(returnModel));
        when(agendaRepository.findByCustomerId(id)).thenReturn(List.of());
        mvc.perform(delete("/v1/api/customers/" + id)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When Delete By Id And Returns Bad Request")
    void deleteById_BadRequest() throws Exception {
        String id = "0";
        mvc.perform(delete("/v1/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O identificador informado [" + id + "] não é válido."));
    }

    @Test
    @DisplayName("When Delete By Id And Returns Bad Request Agenda Exists")
    void deleteById_BadRequestAgendaExists() throws Exception {
        Integer id = 2;
        when(repository.findById(2)).thenReturn(Optional.of(returnModel));
        when(agendaRepository.findByCustomerId(id)).thenReturn(List.of(agendaModel));
        mvc.perform(delete("/v1/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Existem eventos agendados com o cliente " +
                        "de id [" + id + "]. Para Prosseguir com a operação de remoção do clinte, os eventos marcados com " +
                        "o cliente deverão ser removidos."));
    }

    @Test
    @DisplayName("When Delete By Id And Returns Not Found")
    void deleteById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(2)).thenReturn(Optional.empty());
        mvc.perform(delete("/v1/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o cliente com " +
                        "o identificador [" + id + "] informado."));
    }

}