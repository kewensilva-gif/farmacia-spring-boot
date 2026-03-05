package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Employee;
import com.kewen.GerenciamentoFarmacia.entities.Person;
import com.kewen.GerenciamentoFarmacia.repositories.EmployeeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        Person person = new Person();
        person.setId(1L);
        person.setFirstname("Ana");
        person.setLastname("Lima");
        person.setCpf("98765432100");

        employee = new Employee();
        employee.setId(1L);
        employee.setHiringDate(LocalDate.of(2022, 3, 1));
        employee.setTerminationDate(null);
        employee.setSalary(new BigDecimal("3500.00"));
        employee.setPerson(person);
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save - deve salvar e retornar o funcionário")
    void save_deveSalvarERetornarFuncionario() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee result = employeeService.save(employee);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSalary()).isEqualByComparingTo("3500.00");
        verify(employeeRepository, times(1)).save(employee);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById - deve retornar Optional com funcionário quando encontrado")
    void findById_deveRetornarFuncionarioQuandoEncontrado() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPerson().getFirstname()).isEqualTo("Ana");
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrado")
    void findById_deveRetornarVazioQuandoNaoEncontrado() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.findById(99L);

        assertThat(result).isEmpty();
        verify(employeeRepository, times(1)).findById(99L);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll - deve retornar lista de funcionários")
    void findAll_deveRetornarListaDeFuncionarios() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<Employee> result = employeeService.findAll();

        assertThat(result).hasSize(1);
        verify(employeeRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // findByHiringAfter / findByHiringBefore
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByHiringAfter - deve retornar funcionários contratados após a data")
    void findByHiringAfter_deveRetornarFuncionarios() {
        LocalDate data = LocalDate.of(2020, 1, 1);
        when(employeeRepository.findByHiringDateAfter(data)).thenReturn(List.of(employee));

        List<Employee> result = employeeService.findByHiringAfter(data);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHiringDate()).isAfter(data);
        verify(employeeRepository, times(1)).findByHiringDateAfter(data);
    }

    @Test
    @DisplayName("findByHiringBefore - deve retornar funcionários contratados antes da data")
    void findByHiringBefore_deveRetornarFuncionarios() {
        LocalDate data = LocalDate.of(2024, 1, 1);
        when(employeeRepository.findByHiringDateBefore(data)).thenReturn(List.of(employee));

        List<Employee> result = employeeService.findByHiringBefore(data);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHiringDate()).isBefore(data);
        verify(employeeRepository, times(1)).findByHiringDateBefore(data);
    }

    // -------------------------------------------------------------------------
    // findActiveEmployees / findInactiveEmployees
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findActiveEmployees - deve retornar funcionários sem data de demissão")
    void findActiveEmployees_deveRetornarFuncionariosAtivos() {
        when(employeeRepository.findByTerminationDateIsNull()).thenReturn(List.of(employee));

        List<Employee> result = employeeService.findActiveEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTerminationDate()).isNull();
        verify(employeeRepository, times(1)).findByTerminationDateIsNull();
    }

    @Test
    @DisplayName("findInactiveEmployees - deve retornar funcionários com data de demissão")
    void findInactiveEmployees_deveRetornarFuncionariosInativos() {
        employee.setTerminationDate(LocalDate.of(2023, 12, 31));
        when(employeeRepository.findByTerminationDateIsNotNull()).thenReturn(List.of(employee));

        List<Employee> result = employeeService.findInactiveEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTerminationDate()).isNotNull();
        verify(employeeRepository, times(1)).findByTerminationDateIsNotNull();
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update - deve atualizar e retornar o funcionário quando encontrado")
    void update_deveAtualizarFuncionarioQuandoEncontrado() {
        Employee detalhes = new Employee();
        detalhes.setHiringDate(LocalDate.of(2022, 3, 1));
        detalhes.setTerminationDate(LocalDate.of(2024, 6, 30));
        detalhes.setSalary(new BigDecimal("4000.00"));
        detalhes.setPerson(employee.getPerson());

        Employee atualizado = new Employee();
        atualizado.setId(1L);
        atualizado.setSalary(new BigDecimal("4000.00"));
        atualizado.setTerminationDate(LocalDate.of(2024, 6, 30));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(atualizado);

        Employee result = employeeService.update(1L, detalhes);

        assertThat(result.getSalary()).isEqualByComparingTo("4000.00");
        assertThat(result.getTerminationDate()).isEqualTo(LocalDate.of(2024, 6, 30));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando não encontrado")
    void update_deveLancarExcecaoQuandoNaoEncontrado() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(99L, new Employee()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(employeeRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteById / existsById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteById(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar true quando funcionário existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertThat(employeeService.existsById(1L)).isTrue();
        verify(employeeRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando funcionário não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThat(employeeService.existsById(99L)).isFalse();
        verify(employeeRepository, times(1)).existsById(99L);
    }
}
