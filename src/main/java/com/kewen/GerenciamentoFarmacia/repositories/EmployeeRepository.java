package com.kewen.GerenciamentoFarmacia.repositories;

import com.kewen.GerenciamentoFarmacia.entities.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Override
    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Employee> findAll();

    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Employee> findByHiringDateAfter(LocalDate date);

    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Employee> findByHiringDateBefore(LocalDate date);

    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Employee> findByTerminationDateIsNull();

    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Employee> findByTerminationDateIsNotNull();
}
