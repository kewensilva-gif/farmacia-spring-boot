package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Customer;
import com.kewen.GerenciamentoFarmacia.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer save(Customer customer) {
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
        customerRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return customerRepository.existsById(id);
    }
}
