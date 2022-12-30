package br.com.eboli.services.impls;

import br.com.eboli.exceptions.ConflictException;
import br.com.eboli.exceptions.NotFoundException;
import br.com.eboli.models.Contact;
import br.com.eboli.models.Customer;
import br.com.eboli.models.enums.ContactType;
import br.com.eboli.models.requests.ContactRequest;
import br.com.eboli.repositories.ContactRepository;
import br.com.eboli.services.impls.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class ContactServiceImplTest {

    @InjectMocks
    private ContactServiceImpl underTest;

    @Mock
    private ContactRepository repository;

    private Contact model;
    private Contact returnModel;
    private Contact updatingModel;

    private ContactRequest request;
    private ContactRequest returnRequest;

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

        request = ContactRequest.builder()
                .type(ContactType.PHONE)
                .contact("11 2933-3830")
                .customerId(2)
                .build();

        returnRequest = ContactRequest.builder()
                .id(2)
                .type(ContactType.PHONE)
                .contact("11 2933-3830")
                .customerId(2)
                .build();

    }

    @Test
    @DisplayName("When Save And Returns Successful")
    void save_Successful() {
        when(repository.save(model)).thenReturn(returnModel);

        ContactRequest actual = underTest.save(request);
        verify(repository, times(1)).save(model);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Save And Returns Illegal Argument Exception Fields Are Blank")
    void save_IllegalArgumentExceptionFieldsAreBlank() {
        ContactRequest fieldsAreBlanks = ContactRequest.builder().build();

        assertThatThrownBy(() -> underTest.save(fieldsAreBlanks))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As informações do contato não são válidas.");
    }

    @Test
    @DisplayName("When Save And Returns Conflict Exception Contact Exists")
    void save_ConflictExceptionContactExists() {
        String contactExits = "11 2933-3830";
        when(repository.findByContact(contactExits)).thenReturn(Optional.of(returnModel));

        assertThatThrownBy(() -> underTest.save(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("O contato ["+contactExits+"] já possui um cadastro.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Successful")
    void findById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        ContactRequest actual = underTest.findById(id);
        verify(repository, times(1)).findById(id);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Id Null")
    void findById_IllegalArgumentExceptionIdNull() {
        Integer id = null;
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Illegal Argument Exception Id Less Or Equal Zero")
    void findById_IllegalArgumentExceptionIdLessOrEqualZero() {
        Integer id = -1;
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Id And Returns Not Found Exception")
    void findById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o contato com id [" + id + "].");
    }

    @Test
    @DisplayName("When Find By Contact And Returns Successful")
    void findByContact_Successful() {
        String contact = "11 2933-3830";
        when(repository.findByContact(contact)).thenReturn(Optional.of(returnModel));

        ContactRequest actual = underTest.findByContact(contact);
        verify(repository, times(1)).findByContact(contact);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Find By Contact And Returns Illegal Argument Exception Null")
    void findByContact_IllegalArgumentExceptionNull() {
        String contact = null;

        assertThatThrownBy(() -> underTest.findByContact(contact))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O contato informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Contact And Returns Illegal Argument Exception Blank")
    void findByContact_IllegalArgumentExceptionBlank() {
        String contact = "";

        assertThatThrownBy(() -> underTest.findByContact(contact))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O contato informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Contact And Returns Illegal Argument Exception Less Six")
    void findByContact_IllegalArgumentExceptionLessSix() {
        String contact = "11";

        assertThatThrownBy(() -> underTest.findByContact(contact))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O contato informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Contact And Returns Not Found Exception")
    void findByContact_NotFoundException() {
        String contact = "11 2933-3830";
        when(repository.findByContact(contact)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findByContact(contact))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("O contato informado não foi encontrado.");
    }

    @Test
    @DisplayName("When Find All End Returns Successful")
    void findAll_Successful() {
        when(repository.findAll()).thenReturn(List.of(returnModel));

        List<ContactRequest> expected = List.of(returnRequest);
        List<ContactRequest> actual = underTest.findAll();
        verify(repository, times(1)).findAll();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find All End Returns Not Found Exception")
    void findAll_NotFoundException() {
        when(repository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findAll())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar contatos cadastrados.");
    }

    @Test
    @DisplayName("When Find By Type And Returns Successful")
    void findByType_Successful() {
        String contactTypeStr = "PHONE";
        ContactType contactType = ContactType.PHONE;
        when(repository.findByType(contactType)).thenReturn(List.of(returnModel));

        List<ContactRequest> expected = List.of(returnRequest);
        List<ContactRequest> actual = underTest.findByType(contactTypeStr);
        verify(repository, times(1)).findByType(contactType);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Type And Returns Illegal Argument Exception Null")
    void findByType_IllegalArgumentExceptionNull() {
        String contactTypeStr = null;

        assertThatThrownBy(() -> underTest.findByType(contactTypeStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O tipo ["+contactTypeStr+"] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Type And Returns Illegal Argument Exception Blank")
    void findByType_IllegalArgumentExceptionBlank() {
        String contactTypeStr = "";

        assertThatThrownBy(() -> underTest.findByType(contactTypeStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O tipo ["+contactTypeStr+"] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Type And Returns Illegal Argument Exception Not Valid")
    void findByType_IllegalArgumentExceptionNotValid() {
        String contactTypeStr = "PHONE_APPLE";

        assertThatThrownBy(() -> underTest.findByType(contactTypeStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O tipo ["+contactTypeStr+"] informado não é válido.");
    }

    @Test
    @DisplayName("When Find By Type And Returns Not Found Exception")
    void findByType_NotFoundException() {
        String contactTypeStr = "PHONE";
        ContactType contactType = ContactType.PHONE;
        when(repository.findByType(contactType)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByType(contactTypeStr))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados contatos do tipo ["+contactTypeStr+
                        "] informado.");
    }

    @Test
    @DisplayName("When Find By Customer Id And Returns Successful")
    void findByCustomerId_Successful() {
        Integer customerId = 2;
        when(repository.findByCustomerId(customerId)).thenReturn(List.of(returnModel));

        List<ContactRequest> expected = List.of(returnRequest);
        List<ContactRequest> actual = underTest.findByCustomerId(customerId);
        verify(repository, times(1)).findByCustomerId(customerId);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("When Find By Customer Id And Returns Illegal Argument Exception Null")
    void findByCustomerId_IllegalArgumentExceptionNull() {
        Integer customerId = null;

        assertThatThrownBy(() -> underTest.findByCustomerId(customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + customerId + "] do cliente não é válido.");
    }

    @Test
    @DisplayName("When Find By Customer Id And Returns Illegal Argument Exception Less Or Equal Zero")
    void findByCustomerId_IllegalArgumentExceptionCustomerLessOrEqualZero() {
        Integer customerId = -1;

        assertThatThrownBy(() -> underTest.findByCustomerId(customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + customerId + "] do cliente não é válido.");
    }

    @Test
    @DisplayName("When Find By Customer Id And Returns Not Found Exception")
    void findByCustomerId_NotFoundException() {
        Integer customerId = 2;
        when(repository.findByCustomerId(customerId)).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.findByCustomerId(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foram encontrados contatos do cliente de id [" + customerId +
                        "] informado.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Successful")
    void updateById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(updatingModel));
        when(repository.save(returnModel)).thenReturn(returnModel);

        ContactRequest actual = underTest.updateById(id, request);
        verify(repository, times(1)).save(returnModel);
        assertEquals(returnRequest, actual);
    }

    @Test
    @DisplayName("When Update By Id And Returns Illegal Argument Exception Id Null")
    void updateById_IllegalArgumentExceptionIdNull() {
        Integer id = null;

        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Illegal Argument Exception Id Less Or Equal Zero")
    void updateById_IllegalArgumentExceptionIdLessOrEqualZero() {
        Integer id = 0;

        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Update By Id And Returns Not Found Exception")
    void updateById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateById(id, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o contato com id [" + id + "].");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Successful")
    void deleteById_Successful() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.of(returnModel));

        underTest.deleteById(id);
        verify(repository, times(1)).deleteById(id);
        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Id Null")
    void deleteById_IllegalArgumentExceptionIdNull() {
        Integer id = null;

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Illegal Argument Exception Id Less Or Equal Zero")
    void deleteById_IllegalArgumentExceptionIdLessOrEqualZero() {
        Integer id = -1;

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O id [" + id + "] informado não é válido.");
    }

    @Test
    @DisplayName("When Delete By Id And Returns Not Found Exception")
    void deleteById_NotFoundException() {
        Integer id = 2;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Não foi possivel encontrar o contato com id [" + id + "].");
    }

}