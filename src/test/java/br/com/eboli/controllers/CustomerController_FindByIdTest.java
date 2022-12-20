package br.com.eboli.controllers;

import br.com.eboli.repositories.CustomerRepository;
import br.com.eboli.services.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerController_FindByIdTest {

    @Autowired private MockMvc mvc;
    @InjectMocks private CustomerController underTest;
    @Mock private CustomerService service;
    @Mock private CustomerRepository repository;

    @Test
    @DisplayName("When Find Customer By Id And Returns Ok")
    void whenFindCustomerByIdAndReturnsOk() throws Exception {
        mvc.perform(get("/v1/api/customers/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$.cnpj").value("86678081000107"))
                .andExpect(jsonPath("$.foundation").value("1989.12.03"))
                .andExpect(jsonPath("$.registered").value("2021.09.09 09-45-01"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._links.delete-by-id.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._links.by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._links.by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Find Customer By Zero Id And Returns Bad Request")
    void whenFindCustomerByZeroIdAndReturnsBadRetquest() throws Exception {
        mvc.perform(get("/v1/api/customers/0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O identificador deve ser informado para realizar a operação."));
    }

    @Test
    @DisplayName("When Find Customer By Id And Returns Not Found")
    void whenFindCustomerByIdAndReturnsNotFound() throws Exception {
        mvc.perform(get("/v1/api/customers/10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o cliente indicado pelo identificador."));
    }

}
