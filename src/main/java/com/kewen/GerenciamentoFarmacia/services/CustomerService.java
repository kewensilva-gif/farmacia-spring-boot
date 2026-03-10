package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Customer;
import com.kewen.GerenciamentoFarmacia.entities.Person;
import com.kewen.GerenciamentoFarmacia.repositories.CustomerRepository;
import com.kewen.GerenciamentoFarmacia.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private SaleRepository saleRepository;

    public Customer save(Customer customer) {
        validateForSave(customer);
        return customerRepository.save(customer);
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findByRegistrationAfter(LocalDate date) {
        return customerRepository.findByRegistrationDateAfter(date);
    }

    public List<Customer> findByRegistrationBefore(LocalDate date) {
        return customerRepository.findByRegistrationDateBefore(date);
    }

    public Customer update(Long id, Customer customerDetails) {
        return customerRepository.findById(id).map(customer -> {
            customer.setRegistrationDate(customerDetails.getRegistrationDate());
            customer.setPerson(customerDetails.getPerson());
            return customerRepository.save(customer);
        }).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public void deleteById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (saleRepository.existsByCustomerId(id)) {
            throw new IllegalStateException("Cliente não pode ser excluído pois possui vendas vinculadas");
        }

        customerRepository.delete(customer);
    }

    public boolean existsById(Long id) {
        return customerRepository.existsById(id);
    }

    private void validateForSave(Customer customer) {
        if (customer.getPerson() == null || customer.getPerson().getId() == null) {
            throw new IllegalArgumentException("O cliente precisa estar vinculado a uma pessoa");
        }

        Person person = personService.findById(customer.getPerson().getId())
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada"));

        if (person.getCustomer() != null) {
            throw new IllegalArgumentException("Esta pessoa já está cadastrada como cliente");
        }

        if (customer.getRegistrationDate() == null) {
            throw new IllegalArgumentException("A data de cadastro é obrigatória");
        }

        if (customer.getRegistrationDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de cadastro não pode ser futura");
        }
    }
}
