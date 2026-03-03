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
            person.setFirstname(personDetails.getFirstname());
            person.setLastname(personDetails.getLastname());
            person.setCpf(personDetails.getCpf());
            person.setUser(personDetails.getUser());
            return personRepository.save(person);
        }).orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
    }

    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return personRepository.existsById(id);
    }

    public boolean existsByCpf(String cpf) {
        return personRepository.existsByCpf(cpf);
    }
}
