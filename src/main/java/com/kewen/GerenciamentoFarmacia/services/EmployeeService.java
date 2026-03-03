package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Employee;
import com.kewen.GerenciamentoFarmacia.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findByHiringAfter(LocalDate date) {
        return employeeRepository.findByHiringDateAfter(date);
    }

    public List<Employee> findByHiringBefore(LocalDate date) {
        return employeeRepository.findByHiringDateBefore(date);
    }

    public List<Employee> findActiveEmployees() {
        return employeeRepository.findByTerminationDateIsNull();
    }

    public List<Employee> findInactiveEmployees() {
        return employeeRepository.findByTerminationDateIsNotNull();
    }

    public Employee update(Long id, Employee employeeDetails) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setHiringDate(employeeDetails.getHiringDate());
            employee.setTerminationDate(employeeDetails.getTerminationDate());
            employee.setSalary(employeeDetails.getSalary());
            employee.setPerson(employeeDetails.getPerson());
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return employeeRepository.existsById(id);
    }
}
