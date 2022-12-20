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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerController_SaveTest {

    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("When Save Customer And Returns Success")
    void whenSaveCustomerAndReturnsSuccess() throws Exception {
        mvc.perform(post("/v1/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        "{\n" +
                        "    \"fullname\": \"Elza e Agatha Telecomunicações ME\",\n" +
                        "    \"cnpj\": \"68861391000172\",\n" +
                        "    \"foundation\": \"1990.05.22\",\n" +
                        "    \"registered\": \"2022.12.14 00-47-00\"\n" +
                        "}"
                )
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.fullname").value("Elza e Agatha Telecomunicações ME"))
                .andExpect(jsonPath("$.cnpj").value("68861391000172"))
                .andExpect(jsonPath("$.foundation").value("1990.05.22"))
                .andExpect(jsonPath("$.registered").value("2022.12.14 00-47-00"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/customers/6"))
                .andExpect(jsonPath("$._links.delete-by-id.href").value("http://localhost/v1/api/customers/6"))
                .andExpect(jsonPath("$._links.by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/68861391000172"))
                .andExpect(jsonPath("$._links.by-name.href").value("http://localhost/v1/api/customers/find?name=Elza%20e%20Agatha%20Telecomunica%C3%A7%C3%B5es%20ME"))
                .andExpect(jsonPath("$._links.by-foundation.href").value("http://localhost/v1/api/customers/find?foundation=1990.05.22"))
                .andExpect(jsonPath("$._links.by-registered.href").value("http://localhost/v1/api/customers/find?registered=1990.05.22"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Save Null Customer And Returns Bad Request")
    void whenSaveNullCustomerAndReturnsBadRequest() throws Exception {
        mvc.perform(post("/v1/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}")
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("As informações do cliente devem ser informadas para proceguir com o cadastro."));
    }

    @Test
    @DisplayName("When Save Customer With Existing CNPJ And Returns Bad Request")
    void whenSaveCustomerWithExistingCNPJAndReturnsBadRequest() throws Exception {
        mvc.perform(post("/v1/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{ " +
                        " \"fullname\": \"Bruna e Calebe Alimentos Ltda\", " +
                        " \"cnpj\": \"31449057000104\", " +
                        " \"foundation\": \"1990.01.08\", " +
                        " \"registered\": \"2017.06.23 15-32-13\" " +
                        "}")
        ).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Não é possível cadastrar o mesmo cliente."));
    }

}
