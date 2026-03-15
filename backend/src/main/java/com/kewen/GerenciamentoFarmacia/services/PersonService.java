package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Person;
import com.kewen.GerenciamentoFarmacia.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person save(Person person) {
        validateForSave(person);
        return personRepository.save(person);
    }

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findByCpf(String cpf) {
        return personRepository.findByCpf(cpf);
    }

    public Person update(Long id, Person personDetails) {
        return personRepository.findById(id).map(person -> {
            validateForUpdate(id, personDetails);
            person.setFirstname(personDetails.getFirstname());
            person.setLastname(personDetails.getLastname());
            person.setCpf(personDetails.getCpf());
            person.setUser(personDetails.getUser());
            return personRepository.save(person);
        }).orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
    }

    public void deleteById(Long id) {
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        if (person.getEmployee() != null) {
            throw new IllegalStateException("Pessoa não pode ser excluída pois está vinculada a um funcionário");
        }

        if (person.getCustomer() != null) {
            throw new IllegalStateException("Pessoa não pode ser excluída pois está vinculada a um cliente");
        }

        personRepository.delete(person);
    }

    public boolean existsById(Long id) {
        return personRepository.existsById(id);
    }

    public boolean existsByCpf(String cpf) {
        return personRepository.existsByCpf(cpf);
    }

    private void validateForSave(Person person) {
        if (person.getCpf() == null || person.getCpf().isBlank()) {
            throw new IllegalArgumentException("O CPF é obrigatório");
        }

        if (!person.getCpf().matches("\\d{11}")) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos");
        }

        if (personRepository.existsByCpf(person.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (person.getFirstname() == null || person.getFirstname().isBlank()) {
            throw new IllegalArgumentException("O primeiro nome é obrigatório");
        }

        if (person.getLastname() == null || person.getLastname().isBlank()) {
            throw new IllegalArgumentException("O sobrenome é obrigatório");
        }
    }

    private void validateForUpdate(Long id, Person personDetails) {
        if (personDetails.getCpf() == null || personDetails.getCpf().isBlank()) {
            throw new IllegalArgumentException("O CPF é obrigatório");
        }

        if (!personDetails.getCpf().matches("\\d{11}")) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos");
        }

        personRepository.findByCpf(personDetails.getCpf())
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("CPF já cadastrado para outra pessoa");
                }
            });

        if (personDetails.getFirstname() == null || personDetails.getFirstname().isBlank()) {
            throw new IllegalArgumentException("O primeiro nome é obrigatório");
        }

        if (personDetails.getLastname() == null || personDetails.getLastname().isBlank()) {
            throw new IllegalArgumentException("O sobrenome é obrigatório");
        }
    }
}
