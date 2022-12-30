package br.com.eboli.controllers;

import br.com.eboli.models.Address;
import br.com.eboli.models.Customer;
import br.com.eboli.repositories.AddressRepository;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AddressRepository repository;

    private Address model;
    private Address returnModel;
    private Address updatingModel;

    @BeforeEach
    void setUp() {

        model = Address.builder()
                .publicPlace("Rua Joaquim Sérvolo")
                .number(621)
                .complement("AP23")
                .neighborhood("Jardim Petrópolis")
                .city("Piracicaba")
                .state("SP")
                .zipCode("13420667")
                .customer(Customer.builder().id(2).build())
                .build();

        returnModel = Address.builder()
                .id(2)
                .publicPlace("Rua Joaquim Sérvolo")
                .number(621)
                .complement("AP23")
                .neighborhood("Jardim Petrópolis")
                .city("Piracicaba")
                .state("SP")
                .zipCode("13420667")
                .customer(Customer.builder().id(2).build())
                .build();

        updatingModel = Address.builder()
                .id(2)
                .publicPlace("Rua Duque de Caxias")
                .number(444)
                .complement("A123")
                .neighborhood("Serra das Lagoas")
                .city("Campinas")
                .state("SP")
                .zipCode("11608545")
                .customer(Customer.builder().id(1).build())
                .build();

    }

    @Test
    @DisplayName("When Save And Returns Ok")
    void save_Ok() throws Exception {
        when(repository.save(any(Address.class))).thenReturn(returnModel);

        mvc.perform(post("/v1/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"publicPlace\": \"Rua Joaquim Sérvolo\", " +
                                        "\"number\": 621, " +
                                        "\"complement\": \"AP23\", " +
                                        "\"neighborhood\": \"Jardim Petrópolis\", " +
                                        "\"city\": \"Piracicaba\", " +
                                        "\"state\": \"SP\", " +
                                        "\"zipCode\": \"13420667\", " +
                                        "\"customerId\": 2" +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.publicPlace").value("Rua Joaquim Sérvolo"))
                .andExpect(jsonPath("$.number").value(621))
                .andExpect(jsonPath("$.complement").value("AP23"))
                .andExpect(jsonPath("$.neighborhood").value("Jardim Petrópolis"))
                .andExpect(jsonPath("$.city").value("Piracicaba"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.zipCode").value("13420667"))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/addresses/2"))
                .andExpect(jsonPath("$._links.find-by-zipcode.href").value("http://localhost/v1/api/addresses/cep/13420667"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/addresses"));
    }

    @Test
    @DisplayName("When Save And Returns Bad Request")
    void save_BadRequest() throws Exception {
        mvc.perform(post("/v1/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"publicPlace\": \"\", " +
                                        "\"number\": 0, " +
                                        "\"complement\": \"\", " +
                                        "\"neighborhood\": \"\", " +
                                        "\"city\": \"\", " +
                                        "\"state\": \"\", " +
                                        "\"zipCode\": \"\", " +
                                        "\"customerId\": 0" +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("As informações do endereço não são válidas."));
    }

    @Test
    @DisplayName("When Find By Id And Returns Ok")
    void findById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(2)).thenReturn(Optional.of(returnModel));
        mvc.perform(get("http://localhost:8080/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.publicPlace").value("Rua Joaquim Sérvolo"))
                .andExpect(jsonPath("$.number").value(621))
                .andExpect(jsonPath("$.complement").value("AP23"))
                .andExpect(jsonPath("$.neighborhood").value("Jardim Petrópolis"))
                .andExpect(jsonPath("$.city").value("Piracicaba"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.zipCode").value("13420667"))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost:8080/v1/api/addresses/2"))
                .andExpect(jsonPath("$._links.find-by-zipcode.href").value("http://localhost:8080/v1/api/addresses/cep/13420667"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost:8080/v1/api/addresses"));
    }

    @Test
    @DisplayName("When Find By Id And Returns Bad Request")
    void findByid_BadRequest() throws Exception {
        Integer id = -1;
        mvc.perform(get("/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O identificador [" + id + "] não é válido."));

    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found")
    void findById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(get("/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o endereço com " +
                        "o identificador [" + id + "]"));
    }

    @Test
    @DisplayName("When Find All And Returns Ok")
    void findAll_Ok() throws Exception {
        when(repository.findAll()).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].publicPlace").value("Rua Joaquim Sérvolo"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].number").value(621))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].complement").value("AP23"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].neighborhood").value("Jardim Petrópolis"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].city").value("Piracicaba"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].state").value("SP"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].zipCode").value("13420667"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].customerId").value(2))
                .andExpect(jsonPath("$._embedded.addressResponseList[0]._links.self.href").value("http://localhost/v1/api/addresses/2"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0]._links.find-by-zipcode.href").value("http://localhost/v1/api/addresses/cep/13420667"));
    }

    @Test
    @DisplayName("When Find All And Returns Not Found")
    void findAll_NotFound() throws Exception {
        when(repository.findAll()).thenReturn(List.of());

        mvc.perform(get("/v1/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados endereços cadastrados."));
    }

    @Test
    @DisplayName("When Find By Zipcode And Returns Ok")
    void findByZipcode_Ok() throws Exception {
        String zipcode = "13420667";
        when(repository.findByZipCode(zipcode)).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/addresses/cep/" + zipcode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].id").value(2))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].publicPlace").value("Rua Joaquim Sérvolo"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].number").value(621))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].complement").value("AP23"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].neighborhood").value("Jardim Petrópolis"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].city").value("Piracicaba"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].state").value("SP"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].zipCode").value("13420667"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0].customerId").value(2))
                .andExpect(jsonPath("$._embedded.addressResponseList[0]._links.self.href").value("http://localhost/v1/api/addresses/2"))
                .andExpect(jsonPath("$._embedded.addressResponseList[0]._links.find-by-zipcode.href").value("http://localhost/v1/api/addresses/cep/13420667"));
    }

    @Test
    @DisplayName("When Find By Ziocode And Returns Bad Request")
    void findByZipcode_BadRequest() throws Exception {
        String zipcode = "13420";

        mvc.perform(get("/v1/api/addresses/cep/" + zipcode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O CEP [" + zipcode + "] informado não é válido."));
    }

    @Test
    @DisplayName("When Find By Ziocode And Returns Not Found")
    void findByZipcode_NotFound() throws Exception {
        String zipcode = "13420667";
        when(repository.findByZipCode(zipcode)).thenReturn(List.of());

        mvc.perform(get("/v1/api/addresses/cep/" + zipcode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados endereços " +
                        "com CEP [" + zipcode + "] informado."));
    }

    @Test
    @DisplayName("When Update By Id And Returns Ok")
    void updateById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(2)).thenReturn(Optional.of(updatingModel));
        when(repository.save(model)).thenReturn(returnModel);

        mvc.perform(put("/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"publicPlace\": \"Rua Joaquim Sérvolo\", " +
                                        "\"number\": 621, " +
                                        "\"complement\": \"AP23\", " +
                                        "\"neighborhood\": \"Jardim Petrópolis\", " +
                                        "\"city\": \"Piracicaba\", " +
                                        "\"state\": \"SP\", " +
                                        "\"zipCode\": \"13420667\", " +
                                        "\"customerId\": 2" +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.publicPlace").value("Rua Joaquim Sérvolo"))
                .andExpect(jsonPath("$.number").value(621))
                .andExpect(jsonPath("$.complement").value("AP23"))
                .andExpect(jsonPath("$.neighborhood").value("Jardim Petrópolis"))
                .andExpect(jsonPath("$.city").value("Piracicaba"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.zipCode").value("13420667"))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/api/addresses/2"))
                .andExpect(jsonPath("$._links.find-by-zipcode.href").value("http://localhost/v1/api/addresses/cep/13420667"))
                .andExpect(jsonPath("$._links.collection.href").value("http://localhost/v1/api/addresses"));
    }

    @Test
    @DisplayName("When Update By Id And Returns Bad Request")
    void updateById_BadRequest() throws Exception {
        Integer id = -1;

        mvc.perform(put("/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"publicPlace\": \"Rua Joaquim Sérvolo\", " +
                                        "\"number\": 621, " +
                                        "\"complement\": \"AP23\", " +
                                        "\"neighborhood\": \"Jardim Petrópolis\", " +
                                        "\"city\": \"Piracicaba\", " +
                                        "\"state\": \"SP\", " +
                                        "\"zipCode\": \"13420667\", " +
                                        "\"customerId\": 2" +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O identificador [" + id + "] informado não é válido."));
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found")
    void updateById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(put("/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                        "\"publicPlace\": \"Rua Joaquim Sérvolo\", " +
                                        "\"number\": 621, " +
                                        "\"complement\": \"AP23\", " +
                                        "\"neighborhood\": \"Jardim Petrópolis\", " +
                                        "\"city\": \"Piracicaba\", " +
                                        "\"state\": \"SP\", " +
                                        "\"zipCode\": \"13420667\", " +
                                        "\"customerId\": 2" +
                                        "}"
                        )
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o endereço com o identificador [" +
                        id + "]"));
    }

    @Test
    @DisplayName("When Delete By Id And Returns No Content")
    void deleteById_NoContent() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));
        mvc.perform(delete("/v1/api/addresses/" + id)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When Delete By Id And Returns Bad Request")
    void deleteById_BadRequest() throws Exception {
        Integer id = -1;
        mvc.perform(delete("/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O identificador [" + id + "] informado não é válido."));
    }

    @Test
    @DisplayName("When Delete By Id And Returns Not Found")
    void deleteById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(delete("/v1/api/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o endereço com " +
                        "o identificador [" + id + "]"));
    }

}