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

import javax.print.attribute.standard.Media;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerController_UpdateByIdTest {

    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("When Update By Id And Returns Success")
    void whenUpdateByIdAndReturnsSuccess() throws Exception {
        mvc.perform(put("/v1/api/customers/5")
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
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.fullname").value("Elza e Agatha Telecomunicações ME"))
                .andExpect(jsonPath("$.cnpj").value("68861391000172"))
                .andExpect(jsonPath("$.foundation").value("1990.05.22"))
                .andExpect(jsonPath("$.registered").value("2022.12.14 00-47-00"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/customers/5"))
                .andExpect(jsonPath("$._links.delete-by-id.href").value("http://localhost/v1/api/customers/5"))
                .andExpect(jsonPath("$._links.by-cnpj.href").value("http://localhost/v1/api/customers/cnpj/68861391000172"))
                .andExpect(jsonPath("$._links.by-name.href").value("http://localhost/v1/api/customers/find?name=Elza%20e%20Agatha%20Telecomunica%C3%A7%C3%B5es%20ME"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/customers"));
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found")
    void whenUpdateByIdAndReturnsNotFound() throws Exception {
        mvc.perform(put("/v1/api/customers/15")
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
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o cliente indicado pelo identificador."));
    }

}
