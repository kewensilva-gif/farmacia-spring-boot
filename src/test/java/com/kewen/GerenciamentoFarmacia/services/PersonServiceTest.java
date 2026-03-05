package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Person;
import com.kewen.GerenciamentoFarmacia.entities.User;
import com.kewen.GerenciamentoFarmacia.repositories.PersonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person person;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("carlosmendes");
        user.setEmail("carlos@email.com");
        user.setPassword("senha456");

        person = new Person();
        person.setId(1L);
        person.setFirstname("Carlos");
        person.setLastname("Mendes");
        person.setCpf("11122233344");
        person.setUser(user);
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save - deve salvar e retornar a pessoa")
    void save_deveSalvarERetornarPessoa() {
        when(personRepository.save(any(Person.class))).thenReturn(person);

        Person result = personService.save(person);

        assertThat(result).isNotNull();
        assertThat(result.getFirstname()).isEqualTo("Carlos");
        assertThat(result.getCpf()).isEqualTo("11122233344");
        verify(personRepository, times(1)).save(person);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById - deve retornar Optional com pessoa quando encontrada")
    void findById_deveRetornarPessoaQuandoEncontrada() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        Optional<Person> result = personService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getLastname()).isEqualTo("Mendes");
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrada")
    void findById_deveRetornarVazioQuandoNaoEncontrada() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Person> result = personService.findById(99L);

        assertThat(result).isEmpty();
        verify(personRepository, times(1)).findById(99L);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll - deve retornar lista de pessoas")
    void findAll_deveRetornarListaDePessoas() {
        when(personRepository.findAll()).thenReturn(List.of(person));

        List<Person> result = personService.findAll();

        assertThat(result).hasSize(1);
        verify(personRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - deve retornar lista vazia quando não há pessoas")
    void findAll_deveRetornarListaVazia() {
        when(personRepository.findAll()).thenReturn(List.of());

        List<Person> result = personService.findAll();

        assertThat(result).isEmpty();
        verify(personRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // findByCpf
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByCpf - deve retornar pessoa quando CPF existe")
    void findByCpf_deveRetornarPessoaQuandoCpfExiste() {
        when(personRepository.findByCpf("11122233344")).thenReturn(Optional.of(person));

        Optional<Person> result = personService.findByCpf("11122233344");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstname()).isEqualTo("Carlos");
        verify(personRepository, times(1)).findByCpf("11122233344");
    }

    @Test
    @DisplayName("findByCpf - deve retornar Optional vazio quando CPF não existe")
    void findByCpf_deveRetornarVazioQuandoCpfNaoExiste() {
        when(personRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        Optional<Person> result = personService.findByCpf("00000000000");

        assertThat(result).isEmpty();
        verify(personRepository, times(1)).findByCpf("00000000000");
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update - deve atualizar e retornar a pessoa quando encontrada")
    void update_deveAtualizarPessoaQuandoEncontrada() {
        Person detalhes = new Person();
        detalhes.setFirstname("Carlos");
        detalhes.setLastname("Ferreira");
        detalhes.setCpf("11122233344");
        detalhes.setUser(person.getUser());

        Person atualizado = new Person();
        atualizado.setId(1L);
        atualizado.setFirstname("Carlos");
        atualizado.setLastname("Ferreira");
        atualizado.setCpf("11122233344");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenReturn(atualizado);

        Person result = personService.update(1L, detalhes);

        assertThat(result.getLastname()).isEqualTo("Ferreira");
        verify(personRepository, times(1)).findById(1L);
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando não encontrada")
    void update_deveLancarExcecaoQuandoNaoEncontrada() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.update(99L, new Person()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pessoa não encontrada");

        verify(personRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(personRepository).deleteById(1L);

        personService.deleteById(1L);

        verify(personRepository, times(1)).deleteById(1L);
    }

    // -------------------------------------------------------------------------
    // existsById / existsByCpf
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("existsById - deve retornar true quando pessoa existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(personRepository.existsById(1L)).thenReturn(true);

        assertThat(personService.existsById(1L)).isTrue();
        verify(personRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando pessoa não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        when(personRepository.existsById(99L)).thenReturn(false);

        assertThat(personService.existsById(99L)).isFalse();
        verify(personRepository, times(1)).existsById(99L);
    }

    @Test
    @DisplayName("existsByCpf - deve retornar true quando CPF existe")
    void existsByCpf_deveRetornarTrueQuandoCpfExiste() {
        when(personRepository.existsByCpf("11122233344")).thenReturn(true);

        assertThat(personService.existsByCpf("11122233344")).isTrue();
        verify(personRepository, times(1)).existsByCpf("11122233344");
    }

    @Test
    @DisplayName("existsByCpf - deve retornar false quando CPF não existe")
    void existsByCpf_deveRetornarFalseQuandoCpfNaoExiste() {
        when(personRepository.existsByCpf("00000000000")).thenReturn(false);

        assertThat(personService.existsByCpf("00000000000")).isFalse();
        verify(personRepository, times(1)).existsByCpf("00000000000");
    }
}
