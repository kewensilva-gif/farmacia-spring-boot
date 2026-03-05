package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Customer;
import com.kewen.GerenciamentoFarmacia.entities.Person;
import com.kewen.GerenciamentoFarmacia.entities.User;
import com.kewen.GerenciamentoFarmacia.repositories.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private Person person;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("joaodasilva");
        user.setEmail("joao@email.com");
        user.setPassword("senha123");

        person = new Person();
        person.setId(1L);
        person.setFirstname("João");
        person.setLastname("da Silva");
        person.setCpf("12345678900");
        person.setUser(user);

        customer = new Customer();
        customer.setId(1L);
        customer.setRegistrationDate(LocalDate.of(2024, 1, 15));
        customer.setPerson(person);
    }

    @Test
    @DisplayName("save - deve salvar e retornar o cliente")
    void save_deveSalvarERetornarCliente() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.save(customer);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPerson().getFirstname()).isEqualTo("João");
        assertThat(result.getPerson().getLastname()).isEqualTo("da Silva");
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("findById - deve retornar Optional com cliente quando encontrado")
    void findById_deveRetornarClienteQuandoEncontrado() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPerson().getCpf()).isEqualTo("12345678900");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrado")
    void findById_deveRetornarVazioQuandoNaoEncontrado() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.findById(99L);

        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("findAll - deve retornar lista de clientes")
    void findAll_deveRetornarListaDeClientes() {
        Customer outro = new Customer();
        outro.setId(2L);
        outro.setRegistrationDate(LocalDate.of(2024, 3, 10));
        outro.setPerson(new Person());

        when(customerRepository.findAll()).thenReturn(List.of(customer, outro));

        List<Customer> result = customerService.findAll();

        assertThat(result).hasSize(2);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - deve retornar lista vazia quando não há clientes")
    void findAll_deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<Customer> result = customerService.findAll();

        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findByRegistrationAfter - deve retornar clientes com cadastro após a data")
    void findByRegistrationAfter_deveRetornarClientesAposData() {
        LocalDate data = LocalDate.of(2024, 1, 1);
        when(customerRepository.findByRegistrationDateAfter(data)).thenReturn(List.of(customer));

        List<Customer> result = customerService.findByRegistrationAfter(data);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRegistrationDate()).isAfter(data);
        verify(customerRepository, times(1)).findByRegistrationDateAfter(data);
    }

    @Test
    @DisplayName("findByRegistrationBefore - deve retornar clientes com cadastro antes da data")
    void findByRegistrationBefore_deveRetornarClientesAntesData() {
        LocalDate data = LocalDate.of(2025, 1, 1);
        when(customerRepository.findByRegistrationDateBefore(data)).thenReturn(List.of(customer));

        List<Customer> result = customerService.findByRegistrationBefore(data);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRegistrationDate()).isBefore(data);
        verify(customerRepository, times(1)).findByRegistrationDateBefore(data);
    }

    @Test
    @DisplayName("update - deve atualizar e retornar o cliente quando encontrado")
    void update_deveAtualizarClienteQuandoEncontrado() {
        Person novaPessoa = new Person();
        novaPessoa.setId(2L);
        novaPessoa.setFirstname("Maria");
        novaPessoa.setLastname("Souza");

        Customer detalhes = new Customer();
        detalhes.setRegistrationDate(LocalDate.of(2025, 6, 1));
        detalhes.setPerson(novaPessoa);

        Customer atualizado = new Customer();
        atualizado.setId(1L);
        atualizado.setRegistrationDate(LocalDate.of(2025, 6, 1));
        atualizado.setPerson(novaPessoa);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(atualizado);

        Customer result = customerService.update(1L, detalhes);

        assertThat(result.getPerson().getFirstname()).isEqualTo("Maria");
        assertThat(result.getRegistrationDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando cliente não encontrado")
    void update_deveLancarExcecaoQuandoNaoEncontrado() {
        Customer detalhes = new Customer();
        detalhes.setRegistrationDate(LocalDate.now());

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(99L, detalhes))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente não encontrado");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(customerRepository).deleteById(1L);

        customerService.deleteById(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar true quando cliente existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        boolean result = customerService.existsById(1L);

        assertThat(result).isTrue();
        verify(customerRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando cliente não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        boolean result = customerService.existsById(99L);

        assertThat(result).isFalse();
        verify(customerRepository, times(1)).existsById(99L);
    }
}