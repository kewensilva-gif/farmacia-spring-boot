package com.kewen.GerenciamentoFarmacia.repositories;

import com.kewen.GerenciamentoFarmacia.entities.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Override
    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Customer> findAll();

    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Customer> findByRegistrationDateAfter(LocalDate date);

    @EntityGraph(attributePaths = {"person", "person.user", "person.user.role"})
    List<Customer> findByRegistrationDateBefore(LocalDate date);
}
