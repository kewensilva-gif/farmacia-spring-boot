package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.dto.EmployeeDto;
import com.kewen.GerenciamentoFarmacia.entities.Employee;
import com.kewen.GerenciamentoFarmacia.entities.Person;
import com.kewen.GerenciamentoFarmacia.mappers.EmployeeMapper;
import com.kewen.GerenciamentoFarmacia.repositories.EmployeeRepository;
import com.kewen.GerenciamentoFarmacia.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    public Employee save(Employee employee) {
        validateForSave(employee);
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<EmployeeDto> findAll() {
        return employeeMapper.toEmployeeDtoList(employeeRepository.findAll());
    }

    public List<EmployeeDto> findByHiringAfter(LocalDate date) {
        return employeeMapper.toEmployeeDtoList(employeeRepository.findByHiringDateAfter(date));
    }

    public List<EmployeeDto> findByHiringBefore(LocalDate date) {
        return employeeMapper.toEmployeeDtoList(employeeRepository.findByHiringDateBefore(date));
    }

    public List<EmployeeDto> findActiveEmployees() {
        return employeeMapper.toEmployeeDtoList(employeeRepository.findByTerminationDateIsNull());
    }

    public List<EmployeeDto> findInactiveEmployees() {
        return employeeMapper.toEmployeeDtoList(employeeRepository.findByTerminationDateIsNotNull());
    }

    public Employee update(Long id, Employee employeeDetails) {
        return employeeRepository.findById(id).map(employee -> {
            validateForUpdate(employeeDetails);
            employee.setHiringDate(employeeDetails.getHiringDate());
            employee.setTerminationDate(employeeDetails.getTerminationDate());
            employee.setSalary(employeeDetails.getSalary());
            employee.setPerson(employeeDetails.getPerson());
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public void deleteById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        if (saleRepository.existsByEmployeeId(id)) {
            throw new IllegalStateException("Funcionário não pode ser excluído pois possui vendas vinculadas");
        }

        employeeRepository.delete(employee);
    }

    public boolean existsById(Long id) {
        return employeeRepository.existsById(id);
    }

    private void validateForSave(Employee employee) {
        if (employee.getPerson() == null || employee.getPerson().getId() == null) {
            throw new IllegalArgumentException("O funcionário precisa estar vinculado a uma pessoa");
        }

        Person person = personService.findById(employee.getPerson().getId())
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada"));

        if (person.getEmployee() != null) {
            throw new IllegalArgumentException("Esta pessoa já está cadastrada como funcionário");
        }

        if (employee.getSalary() == null || employee.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O salário deve ser maior que zero");
        }

        if (employee.getHiringDate() == null) {
            throw new IllegalArgumentException("A data de contratação é obrigatória");
        }

        validateTerminationDate(employee);
    }

    private void validateForUpdate(Employee employee) {
        if (employee.getSalary() == null || employee.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O salário deve ser maior que zero");
        }

        if (employee.getHiringDate() == null) {
            throw new IllegalArgumentException("A data de contratação é obrigatória");
        }

        validateTerminationDate(employee);
    }

    private void validateTerminationDate(Employee employee) {
        if (employee.getTerminationDate() != null && employee.getHiringDate() != null) {
            if (employee.getTerminationDate().isBefore(employee.getHiringDate())) {
                throw new IllegalArgumentException("A data de desligamento não pode ser anterior à data de contratação");
            }
        }
    }
}
