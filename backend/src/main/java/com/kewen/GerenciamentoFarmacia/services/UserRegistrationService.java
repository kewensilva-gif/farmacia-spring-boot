package com.kewen.GerenciamentoFarmacia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kewen.GerenciamentoFarmacia.dto.UserRegistrationRequest;
import com.kewen.GerenciamentoFarmacia.entities.Customer;
import com.kewen.GerenciamentoFarmacia.entities.Employee;
import com.kewen.GerenciamentoFarmacia.entities.Person;
import com.kewen.GerenciamentoFarmacia.entities.Role;
import com.kewen.GerenciamentoFarmacia.entities.User;

import jakarta.transaction.Transactional;

@Service
public class UserRegistrationService {
    @Autowired
    private PersonService personService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @Transactional
    public void adminRegisterUser(UserRegistrationRequest request) {
        Role role = roleService.findByName(request.roleName())
            .orElseThrow(() -> new IllegalArgumentException("Role não encontrada: " + request.roleName()));

        validateByRole(role.getName(), request);

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setRole(role);
        userService.save(user);

        Person person = new Person();
        person.setFirstname(request.firstName());
        person.setLastname(request.lastName());
        person.setCpf(request.cpf());
        person.setUser(user);
        personService.save(person);

        switch (role.getName()) {
            case "EMPLOYEE" -> {
                Employee employee = new Employee();
                employee.setHiringDate(request.hiringDate());
                employee.setSalary(request.salary());
                employee.setPerson(person);
                employeeService.save(employee);
            }
            case "CUSTOMER" -> {
                Customer customer = new Customer();
                customer.setRegistrationDate(request.registrationDate());
                customer.setPerson(person);
                customerService.save(customer);
            }
        }
    }

    private void validateByRole(String roleName, UserRegistrationRequest request) {
        if (roleName.equals("EMPLOYEE")) {
            if (request.hiringDate() == null) {
                throw new IllegalArgumentException("Data de contratação é obrigatória para funcionários");
            }
            if (request.salary() == null || request.salary().signum() <= 0) {
                throw new IllegalArgumentException("Salário é obrigatório e deve ser maior que zero");
            }
        }
        if (roleName.equals("CUSTOMER")) {
            if (request.registrationDate() == null) {
                throw new IllegalArgumentException("Data de cadastro é obrigatória para clientes");
            }
        }
    }
}
