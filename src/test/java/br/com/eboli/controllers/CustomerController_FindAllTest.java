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
public class CustomerController_FindAllTest {

    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("When Find All Customer And Returns Ok")
    void whenFindAllCustomerAndReturnsOk() throws Exception {
        mvc.perform(get("/v1/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$._embedded.customerResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].fullname").value("Bruna e Calebe Alimentos Ltda"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].cnpj").value("31449057000104"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].foundation").value("1990.01.08"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0].registered").value("2017.06.23 15-32-13"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.self.href").value("http://localhost/v1/api/customers/1"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.delete-by-id.href").value("http://localhost/v1/api/customers/1"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/31449057000104"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.by-name.href").value("http://localhost/v1/api/customers/find?name=Bruna%20e%20Calebe%20Alimentos%20Ltda"))
                .andExpect(jsonPath("$._embedded.customerResponseList[0]._links.collection.href").value("http://localhost/v1/api/customers"))

                .andExpect(jsonPath("$._embedded.customerResponseList[1].id").value(2))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].fullname").value("Kamilly e Marlene Casa Noturna ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].cnpj").value("86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].foundation").value("1989.12.03"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1].registered").value("2021.09.09 09-45-01"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.self.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.delete-by-id.href").value("http://localhost/v1/api/customers/2"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/86678081000107"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.by-name.href").value("http://localhost/v1/api/customers/find?name=Kamilly%20e%20Marlene%20Casa%20Noturna%20ME"))
                .andExpect(jsonPath("$._embedded.customerResponseList[1]._links.collection.href").value("http://localhost/v1/api/customers"));
    }

}
