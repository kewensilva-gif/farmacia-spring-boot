package com.kewen.GerenciamentoFarmacia.controllers;

import com.kewen.GerenciamentoFarmacia.entities.Employee;
import com.kewen.GerenciamentoFarmacia.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable Long id) {
        return employeeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(employee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            return ResponseEntity.ok(employeeService.update(id, employee));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (employeeService.existsById(id)) {
            employeeService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search/after")
    public ResponseEntity<List<Employee>> findByHiringAfter(@RequestParam LocalDate date) {
        return ResponseEntity.ok(employeeService.findByHiringAfter(date));
    }

    @GetMapping("/search/before")
    public ResponseEntity<List<Employee>> findByHiringBefore(@RequestParam LocalDate date) {
        return ResponseEntity.ok(employeeService.findByHiringBefore(date));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Employee>> findActiveEmployees() {
        return ResponseEntity.ok(employeeService.findActiveEmployees());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Employee>> findInactiveEmployees() {
        return ResponseEntity.ok(employeeService.findInactiveEmployees());
    }
}
