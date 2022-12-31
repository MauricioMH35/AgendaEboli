package br.com.eboli.controllers;

import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.repositories.ContactRepository;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContactRepository repository;

    private Contact model;
    private Contact returnModel;
    private Contact updatingModel;

    @BeforeEach
    void setUp() {

        model = Contact.builder()
                .type(ContactType.PHONE)
                .contact("11 2933-3830")
                .customer(Customer.builder().id(2).build())
                .build();

        returnModel = Contact.builder()
                .id(2)
                .type(ContactType.PHONE)
                .contact("11 2933-3830")
                .customer(Customer.builder().id(2).build())
                .build();

        updatingModel = Contact.builder()
                .id(2)
                .type(ContactType.PHONE)
                .contact("21 37162285")
                .customer(Customer.builder().id(2).build())
                .build();

    }

    @Test @DisplayName("When Save And Returns Ok")
    void save_Ok() throws Exception {
        when(repository.save(model)).thenReturn(returnModel);

        mvc.perform(post("/v1/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        "{" +
                                    "\"type\": \"PHONE\", " +
                                    "\"contact\": \"11 2933-3830\", " +
                                    "\"customerId\": 2" +
                                "}"
                )
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath( "$.id").value(2))
                .andExpect(jsonPath( "$.type").value("PHONE"))
                .andExpect(jsonPath( "$.contact").value("11 2933-3830"))
                .andExpect(jsonPath( "$.customerId").value(2))
                .andExpect(jsonPath( "$._links.self.href").value("http://localhost/v1/api/contacts/2"))
                .andExpect(jsonPath( "$._links.find-by-contact.href").value("http://localhost/v1/api/contacts/contact/11%202933-3830"))
                .andExpect(jsonPath( "$._links.find-by-type.href").value("http://localhost/v1/api/contacts/type/PHONE"))
                .andExpect(jsonPath( "$._links.collection.href").value("http://localhost/v1/api/contacts"));
    }

    @Test @DisplayName("When Save And Returns Bad Request")
    void save_BadRequest() throws Exception {
        mvc.perform(post("/v1/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}")
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("As informações do contato não são válidas."));
    }

    @Test @DisplayName("When Save And Returns Conflict")
    void save_Conflict() throws Exception {
        String contact = "11 2933-3830";
        when(repository.findByContact(contact)).thenReturn(Optional.of(returnModel));

        mvc.perform(post("/v1/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        "{" +
                                "\"type\": \"PHONE\", " +
                                "\"contact\": \"11 2933-3830\", " +
                                "\"customerId\": 2" +
                                "}"
                )
        ).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("O contato ["+contact+
                        "] já possui um cadastro."));
    }

    @Test @DisplayName("When Find By Id And Returns Ok")
    void findById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        mvc.perform(get("/v1/api/contacts/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath( "$.id").value(2))
                .andExpect(jsonPath( "$.type").value("PHONE"))
                .andExpect(jsonPath( "$.contact").value("11 2933-3830"))
                .andExpect(jsonPath( "$.customerId").value(2))
                .andExpect(jsonPath( "$._links.self.href").value("http://localhost/v1/api/contacts/2"))
                .andExpect(jsonPath( "$._links.find-by-contact.href").value("http://localhost/v1/api/contacts/contact/11%202933-3830"))
                .andExpect(jsonPath( "$._links.find-by-type.href").value("http://localhost/v1/api/contacts/type/PHONE"))
                .andExpect(jsonPath( "$._links.collection.href").value("http://localhost/v1/api/contacts"));
    }

    @Test @DisplayName("When Find By Id And Returns Bad Request")
    void findById_BadRequest() throws Exception {
        Integer id = -1;
        mvc.perform(get("/v1/api/contacts/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O id [" + id + "] informado não é válido."));
    }

    @Test @DisplayName("When Find By Id And Returns Not Found")
    void findById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(get("/v1/api/contacts/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o contato " +
                        "com id [" + id + "]."));
    }

    @Test @DisplayName("When Find All And Returns Ok")
    void findAll_Ok() throws Exception {
        when(repository.findAll()).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].id").value(2))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].type").value("PHONE"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].contact").value("11 2933-3830"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].customerId").value(2))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.self.href").value("http://localhost/v1/api/contacts/2"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.find-by-contact.href").value("http://localhost/v1/api/contacts/contact/11%202933-3830"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.find-by-type.href").value("http://localhost/v1/api/contacts/type/PHONE"));
    }

    @Test @DisplayName("When Find All And Not Found")
    void findAll_NotFound() throws Exception {
        when(repository.findAll()).thenReturn(List.of());

        mvc.perform(get("/v1/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar contatos cadastrados."));
    }

    @Test @DisplayName("When Find By Contact And Returns Ok")
    void findByContact_Ok() throws Exception {
        String contact = "11 2933-3830";
        when(repository.findByContact(contact)).thenReturn(Optional.of(returnModel));

        mvc.perform(get("/v1/api/contacts/contact/"+contact)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath( "$.id").value(2))
                .andExpect(jsonPath( "$.type").value("PHONE"))
                .andExpect(jsonPath( "$.contact").value("11 2933-3830"))
                .andExpect(jsonPath( "$.customerId").value(2))
                .andExpect(jsonPath( "$._links.self.href").value("http://localhost/v1/api/contacts/2"))
                .andExpect(jsonPath( "$._links.find-by-contact.href").value("http://localhost/v1/api/contacts/contact/11%202933-3830"))
                .andExpect(jsonPath( "$._links.find-by-type.href").value("http://localhost/v1/api/contacts/type/PHONE"))
                .andExpect(jsonPath( "$._links.collection.href").value("http://localhost/v1/api/contacts"));
    }

    @Test @DisplayName("When Find By Contact And Returns Bad Request")
    void findByContact_BadRequest() throws Exception {
        String contact = "11";

        mvc.perform(get("/v1/api/contacts/contact/"+contact)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O contato informado não é válido."));
    }

    @Test @DisplayName("When Find By Contact And Returns Not Found")
    void findByContact_NotFound() throws Exception {
        String contact = "11 2933-3830";
        when(repository.findByContact(contact)).thenReturn(Optional.empty());

        mvc.perform(get("/v1/api/contacts/contact/"+contact)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("O contato informado não foi encontrado."));
    }

    @Test @DisplayName("When Find By Type And Returns Ok")
    void findByType_Ok() throws Exception {
        ContactType type = ContactType.PHONE;
        when(repository.findByType(type)).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/contacts/type/"+type)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].id").value(2))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].type").value("PHONE"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].contact").value("11 2933-3830"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].customerId").value(2))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.self.href").value("http://localhost/v1/api/contacts/2"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.find-by-contact.href").value("http://localhost/v1/api/contacts/contact/11%202933-3830"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.find-by-type.href").value("http://localhost/v1/api/contacts/type/PHONE"));
    }

    @Test @DisplayName("When Find By Type And Returns Bad Request")
    void findByType_BadRequest() throws Exception {
        String type = "PHO";

        mvc.perform(get("/v1/api/contacts/type/"+type)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O tipo [" + type + "] informado não é válido."));
    }

    @Test @DisplayName("When Find By Type And Returns Not Found")
    void findByType_NotFound() throws Exception {
        ContactType type = ContactType.PHONE;
        when(repository.findByType(type)).thenReturn(List.of());

        mvc.perform(get("/v1/api/contacts/type/"+type)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados contatos do tipo [" +
                        type+"] informado."));
    }

    @Test @DisplayName("When Find By Customer Id And Returns Ok")
    void findByCustomerId_Ok() throws Exception {
        Integer id = 2;
        when(repository.findByCustomerId(id)).thenReturn(List.of(returnModel));

        mvc.perform(get("/v1/api/contacts/customer/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].id").value(2))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].type").value("PHONE"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].contact").value("11 2933-3830"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0].customerId").value(2))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.self.href").value("http://localhost/v1/api/contacts/2"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.find-by-contact.href").value("http://localhost/v1/api/contacts/contact/11%202933-3830"))
                .andExpect(jsonPath( "$._embedded.contactResponseList[0]._links.find-by-type.href").value("http://localhost/v1/api/contacts/type/PHONE"));
    }

    @Test @DisplayName("When Find By Customer Id And Returns Bad Request")
    void findByCustomerId_BadRequest() throws Exception {
        Integer id = 0;
        when(repository.findByCustomerId(id)).thenReturn(List.of());

        mvc.perform(get("/v1/api/contacts/customer/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O id ["+id+"] do cliente não é válido."));
    }

    @Test @DisplayName("When Find By Customer Id And Returns Not Found")
    void findByCustomerId_NotFound() throws Exception {
        Integer customerId = 2;
        when(repository.findByCustomerId(customerId)).thenReturn(List.of());

        mvc.perform(get("/v1/api/contacts/customer/"+customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foram encontrados contatos do " +
                        "cliente de id ["+customerId+"] informado."));
    }

    @Test @DisplayName("When Update By Id And Returns Ok")
    void updateById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(updatingModel));
        when(repository.save(model)).thenReturn(returnModel);

        mvc.perform(put("/v1/api/contacts/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                "{" +
                                            "\"type\": \"PHONE\", " +
                                            "\"contact\": \"11 2933-3830\", " +
                                            "\"customerId\": 2" +
                                        "}"
                        )
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath( "$.id").value(2))
                .andExpect(jsonPath( "$.type").value("PHONE"))
                .andExpect(jsonPath( "$.contact").value("11 2933-3830"))
                .andExpect(jsonPath( "$.customerId").value(2))
                .andExpect(jsonPath( "$._links.self.href").value("http://localhost/v1/api/contacts/2"))
                .andExpect(jsonPath( "$._links.find-by-contact.href").value("http://localhost/v1/api/contacts/contact/11%202933-3830"))
                .andExpect(jsonPath( "$._links.find-by-type.href").value("http://localhost/v1/api/contacts/type/PHONE"))
                .andExpect(jsonPath( "$._links.collection.href").value("http://localhost/v1/api/contacts"));
    }

    @Test @DisplayName("When Update By Id And Returns Bad Request")
    void updateById_BadRequest() throws Exception {
        Integer id = 0;
        mvc.perform(put("/v1/api/contacts/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        "{" +
                                    "\"type\": \"PHONE\", " +
                                    "\"contact\": \"11 2933-3830\", " +
                                    "\"customerId\": 2" +
                                "}"
                )
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O id [" + id + "] informado não é válido."));
    }

    @Test @DisplayName("When Update By Id And Returns Not Found")
    void updateById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(put("/v1/api/contacts/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        "{" +
                                    "\"type\": \"PHONE\", " +
                                    "\"contact\": \"11 2933-3830\", " +
                                    "\"customerId\": 2" +
                                "}"
                )
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o contato " +
                        "com id [" + id + "]."));
    }

    @Test @DisplayName("When Delete By Id And Returns Ok")
    void deleteById_Ok() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));
        mvc.perform(delete("/v1/api/contacts/"+id)).andExpect(status().isNoContent());
    }

    @Test @DisplayName("When Delete By Id And Returns Bad Request")
    void deleteById_BadRequest() throws Exception {
        Integer id = -1;
        mvc.perform(delete("/v1/api/contacts/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("O id [" + id + "] informado não é válido."));
    }

    @Test @DisplayName("When Delete By Id And Returns Not Found")
    void deleteById_NotFound() throws Exception {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        mvc.perform(delete("/v1/api/contacts/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Não foi possivel encontrar o contato " +
                        "com id [" + id + "]."));
    }

}