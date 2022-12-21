package br.com.eboli.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class CustomerController_FindByRegisteredBetweenTest {

    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("When Find By Registered Between And Returns Ok")
    void whenFindByRegisteredBetweenAndReturnsOk() throws Exception {
        mvc.perform(get("/v1/api/customers/find?registered-start=2021.01.01_15-30-00&registered-end=2022.01.01_15-30-00")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1989.12.03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2021.09.09 09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.delete-by-id.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))

                .andExpect(jsonPath("$._embedded.customerResponseList[1].id").value(3))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].fullname").value("Filipe e Alexandre Publicidade e Propaganda Ltda"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].cnpj").value("65203917000139"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].foundation").value("2001.05.21"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].registered").value("2021.09.18 13-01-23"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.self.href").value("http://localhost/v1/api/customers/3"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.delete-by-id.href").value("http://localhost/v1/api/customers/3"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/65203917000139"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.by-name.href").value("http://localhost/v1/api/customers/find?name=Filipe%20e%20Alexandre%20Publicidade%20e%20Propaganda%20Ltda"))

                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Find By Registered Between And Returns Not Found")
    void whenFindByRegisteredBetweenAndReturnsNotFound() throws Exception {
        mvc.perform(get("/v1/api/customers/find?registered-start=1800.01.01_15-30-00&registered-end=1900.01.01_15-30-00")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi encontrado clientes registrado entre as datas indicadas."));
    }

}
